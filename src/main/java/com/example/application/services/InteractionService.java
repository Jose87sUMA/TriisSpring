package com.example.application.services;

import com.example.application.data.entities.*;
import com.example.application.data.repositories.*;
import com.example.application.exceptions.PostException;
import com.vaadin.flow.component.messages.MessageListItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to manage all post interactions.
 */
@Service
public class InteractionService {


    private final LikesRepository likeRep;
    private final UsersRepository userRep;
    private final PostsRepository postRep;
    private final PostPointLogRepository postPointLogRep;
    private final UserPointLogRepository userPointLogRep;
    private final CommentsRepository commentsRep;

    @Autowired
    PointLogService postPointLogService;

    /**
     * Constructor that creates service using the needed repositories.
     * When this service is autowired, spring takes care of creating this service
     *
     * @param likeRep
     * @param userRep
     * @param postRep
     * @param postPointLogRep
     * @param userPointLogRep
     * @param commentsRep
     */
    public InteractionService(LikesRepository likeRep, UsersRepository userRep, PostsRepository postRep, PostPointLogRepository postPointLogRep, UserPointLogRepository userPointLogRep, CommentsRepository commentsRep) {
        this.likeRep = likeRep;
        this.userRep = userRep;
        this.postRep = postRep;
        this.postPointLogRep = postPointLogRep;
        this.userPointLogRep = userPointLogRep;
        this.commentsRep = commentsRep;
    }

    /**
     * This method returns a list of users that liked a given Post.
     *
     * @param post
     * @return The list of users that liked the post
     * @author Daniel de los Ríos García & José Alejandro Sarmiento
     */
    public List<User> getAllUsersLiking(Post post) {

        List <Like> likeEntries =  likeRep.findAllByPostId(post.getPostId());
        List <User> usersThatLiked = new ArrayList<>();

        for(Like likeEntry : likeEntries){
            usersThatLiked.add(userRep.findFirstByUserId(likeEntry.getUserId()));
        }

        return usersThatLiked;
    }

    //LIKE BUTTON

    /**
     * This method adds a like given by the user.
     *
     * @param user User that is liking
     * @param post Post that is being liked
     * @author Daniel de los Ríos García & José Alejandro Sarmiento
     */
    public void newLike(User user, Post post) {
        post.setLikes(post.getLikes().add(BigInteger.ONE));
        likeRep.save(new Like(user.getUserId(), post.getPostId()));
    }

    /**
     * This method deletes the like given by the user.
     *
     * @param user User that is disliking
     * @param post Post that is being disliked
     * @author Daniel de los Ríos García & José Alejandro Sarmiento
     */
    public void dislike(User user, Post post) {
        post.setLikes(post.getLikes().subtract(BigInteger.ONE));
        postRep.save(post);
        likeRep.delete(likeRep.findByUserIdAndPostId(user.getUserId(), post.getPostId()));
    }

    //REPOST BUTTON

    /**
     * Gets the number of reposts that a given post has received
     *
     * @param post
     * @return the amount of reposts
     * @author José Alejandro Sarmiento
     */
    public int getAllReposts(Post post){
        if(post.getOriginalPostId() != null)
            return postRep.findAllByOriginalPostId(post.getOriginalPostId()).size();
        else
            return postRep.findAllByOriginalPostId(post.getPostId()).size();

    }


    /**
     * Given a post and a user, this method checks whether:
     * - User has already reposted any post refering to the same original post as the post given as parameter
     * - User has already reposted any repost of the original post, which is the one given as parameter
     *
     * @param post
     * @param user
     * @return true or false depending on the conditions explained before
     * @author Daniel de los Ríos García & José Alejandro Sarmiento
     */
    public boolean isReposted(Post post, User user){
                //User reposting a repost and has already reposted any post refering to the same original post as the post he is reposting
        return (post.getOriginalPostId() != null && !postRep.findAllByUserIdAndOriginalPostId(user.getUserId(), post.getOriginalPostId()).isEmpty())
                //User reposting the original post and has already reposted any repost of the original post
                || !postRep.findAllByUserIdAndOriginalPostId(user.getUserId(), post.getPostId()).isEmpty();
    }

    /**
     * Tries to create a new pointed repost given a post to be reposted and the user trying to repost. If successful, the new repost
     * is then saved on the database and the point distribution is performed.
     *
     * @param post post that is going to be reposted
     * @param user user that is trying to repost
     * @throws PostException if there are no enough points to create the repost
     * @author José Alejandro Sarmiento
     */
    public void pointedRepost(Post post, User user) throws PostException {
        //Enough Points?
        if(user.getType2Points().compareTo(BigInteger.valueOf(5)) >= 0){
            Post newRepost = postRep.save(new Post(post, user, true));
            this.pointDistribution(newRepost, user);
            user.setType2Points(BigInteger.valueOf(user.getType2Points().intValue() - 5));
            userRep.save(user);
        }else{
            throw new PostException("Not enough points for repost! Wait for next refill :)");
        }
    }

    /**
     * Creates a not pointed repost
     *
     * @param post Post being reposted
     * @param user User reposting
     * @author José Alejandro Sarmiento
     */
    public void notPointedRepost(Post post, User user) {
        postRep.save(new Post(post, user, false));
    }

    /**
     * Makes the point distribution following these rules:
     * - The original post always receives 15 points. It doesn't matter if the original post is the one being reposted directly or not.
     * - If the post parameter is not the original post, it receives 10 points.
     * - If the post parameter is not the original post, all the posts between it and the original post (branch) receive 5 points.
     * - In all previous cases, if a post receives points, the owner of that post receives the same amount of points.
     *
     * @param post Post that was directly reposted.
     * @param user User that reposted
     * @author José Alejandro Sarmiento
     */
    public void pointDistribution(Post post, User user) {

        List<Post> branch = postRep.findPostBranch(post.getPostId());

        Post original = branch.get(branch.size()-1);
        Post direct = branch.get(0);
        User originalPoster = userRep.findFirstByUserId(original.getUserId());

        original.setPoints(BigInteger.valueOf(original.getPoints().intValue() + 10));
        originalPoster.setType1Points(BigInteger.valueOf(originalPoster.getType1Points().intValue() + 10));
        userRep.save(originalPoster);

        User directUser = null;
        if(!original.equals(direct)){

            directUser = userRep.findFirstByUserId(direct.getUserId());

            direct.setPoints(BigInteger.valueOf(direct.getPoints().intValue() + 5));
            directUser.setType1Points(BigInteger.valueOf(directUser.getType1Points().intValue() + 5));

            userRep.save(directUser);
        }

        for(Post p : branch){

            User poster = userRep.findFirstByUserId(p.getUserId());

            p.setPoints(BigInteger.valueOf(p.getPoints().intValue() + 5));
            poster.setType1Points(BigInteger.valueOf(poster.getType1Points().intValue() + 5));

            postRep.save(p);
            userRep.save(poster);

            int addedPoints = poster.equals(originalPoster) ? 15 : poster.equals(directUser) ? 10 : 5;
            boolean directBool = (directUser != null ? directUser : originalPoster).getUserId().equals(p.getUserId());

            PostsPointLog postsPointLog = postPointLogRep.save(new PostsPointLog(p, user, addedPoints, directBool, postPointLogService.findLastInsertedPostLog().getCurrentHash()));
            postPointLogService.calculatePointLogHash(postsPointLog);
            UserPointLog userPointLog = userPointLogRep.save(new UserPointLog(userRep.findFirstByUserId(p.getUserId()), user, addedPoints, directBool, postPointLogService.findLastInsertedUserLog().getCurrentHash()));
            postPointLogService.calculatePointLogHash(userPointLog);

        }
    }

    /**
     * Given a User and a Post that has been reposted by the same user, delete the post's repost from user's profile.
     *
     * @param reposter User that reposted the post in the past.
     * @param post Post that was reposted by the user in the past.
     * @author Daniel de los Ríos García & José Alejandro Sarmiento
     */
    public void deleteRepost(User reposter, Post post){

        BigInteger id = post.getOriginalPostId() != null ? post.getOriginalPostId() : post.getPostId();

        postRep.delete(postRep.findByOriginalPostIdAndUserId(id, reposter.getUserId()));
    }


    /**
     * Takes a Post and returns a list of MessageListItem, which are the comments related to that post.
     *
     * @param post
     * @return the list of MessageListItem that are related the post's comments
     * @author Daniel de los Ríos García
     */
    public List<MessageListItem> commentItems(Post post){
        List<Comment> commentList = commentsRep.findAllByPostId(post.getPostId());
        List<MessageListItem> itemList =new ArrayList<>();
        for(Comment c : commentList){

            Instant zonedTime = c.getCommentDate().atZone(ZoneId.systemDefault()).toInstant();

            MessageListItem item = new MessageListItem(c.getUserComment(), zonedTime, (userRep.findFirstByUserId(c.getUserId()).getUsername()));
            itemList.add(item);
        }
        return itemList;
    }


    /**
     * This method takes a post, a user that comments in that post
     * and a String containing the text. A new object Comment is
     * created and then saved by a method of the CommentsRepository.
     *
     * @param post Post that received the new comment
     * @param user User that commented
     * @param text The comment itself
     * @author Daniel de los Ríos García
     */

    public void newComment(Post post, User user, String text){
        Comment comment = new Comment();

        comment.setPostId(post.getPostId());
        comment.setCommentDate(Instant.now());
        comment.setUserId(user.getUserId());
        comment.setUserComment(text);

        commentsRep.save(comment);
    }

    /**
     * Gets the number of comments that a post has received
     *
     * @param post
     * @return number of comments
     * @author José Alejandro Sarmiento
     */
    public int getAllComments(Post post) {
        return commentsRep.findAllByPostId(post.getPostId()).size();
    }
}
