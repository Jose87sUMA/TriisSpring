package com.example.application.services;

import com.example.application.data.entities.*;
import com.example.application.data.repositories.*;
import com.example.application.exceptions.PostException;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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

    public InteractionService(LikesRepository likeRep, UsersRepository userRep, PostsRepository postRep, PostPointLogRepository postPointLogRep, UserPointLogRepository userPointLogRep, CommentsRepository commentsRep) {
        this.likeRep = likeRep;
        this.userRep = userRep;
        this.postRep = postRep;
        this.postPointLogRep = postPointLogRep;
        this.userPointLogRep = userPointLogRep;
        this.commentsRep = commentsRep;
    }

    /**
     * This method returns a list of users that liked the given Post.
     * @param p
     * @return
     */
    public List<User> getAllUsersLiking(Post p) {

        List <Like> likeEntries =  likeRep.findAllByPostId(p.getPostId());
        List <User> usersThatLiked = new ArrayList<>();

        for(Like likeEntry : likeEntries){
            usersThatLiked.add(userRep.findFirstByUserId(likeEntry.getUserId()));
        }

        return usersThatLiked;
    }

    //LIKE BUTTON

    /**
     * This method adds a like given by the user.
     * @param user
     * @param post
     */
    public void newLike(User user, Post post) {
        post.setLikes(post.getLikes().add(BigInteger.ONE));
        likeRep.save(new Like(user.getUserId(), post.getPostId()));
    }

    /**
     * This method deletes the like given by the user.
     * @param user
     * @param post
     */
    public void dislike(User user, Post post) {
        post.setLikes(post.getLikes().subtract(BigInteger.ONE));
        postRep.save(post);
        likeRep.delete(likeRep.findByUserIdAndPostId(user.getUserId(), post.getPostId()));
    }

    //REPOST BUTTON

    public int getAllReposts(Post p){
        if(p.getOriginalPostId() != null)
            return postRep.findAllByOriginalPostId(p.getOriginalPostId()).size();
        else
            return postRep.findAllByOriginalPostId(p.getPostId()).size();

    }


    /**
     * Given a post and a user, this method checks whether the named post is reposted by the user.
     * @param post
     * @param user
     * @return
     */
    public boolean isReposted(Post post, User user){
                //User reposting a repost and has already reposted any post refering to the same original post as the post he is reposting
        return (post.getOriginalPostId() != null && !postRep.findAllByUserIdAndOriginalPostId(user.getUserId(), post.getOriginalPostId()).isEmpty())
                //User reposting the original post and has already reposted any repost of the original post
                || !postRep.findAllByUserIdAndOriginalPostId(user.getUserId(), post.getPostId()).isEmpty();
    }

    public void pointedRepost(Post post, User authenticatedUser) throws PostException {
        //Enough Points?
        if(authenticatedUser.getType2Points().compareTo(BigInteger.valueOf(5)) >= 0){
            Post newRepost = postRep.save(new Post(post, authenticatedUser, true));
            this.pointDistribution(newRepost, authenticatedUser);
            authenticatedUser.setType2Points(BigInteger.valueOf(authenticatedUser.getType2Points().intValue() - 5));
            userRep.save(authenticatedUser);
        }else{
            throw new PostException("Not enough points for repost! Wait for next refill :)");
        }
    }

    public void notPointedRepost(Post post, User authenticatedUser) {
        postRep.save(new Post(post, authenticatedUser, false));
    }

    public void pointDistribution(Post post, User authUser) {

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

            PostsPointLog postsPointLog = postPointLogRep.save(new PostsPointLog(p, authUser, addedPoints, directBool, postPointLogService.findLastInsertedPostLog().getCurrentHash()));
            postPointLogService.calculatePointLogHash(postsPointLog);
            UserPointLog userPointLog = userPointLogRep.save(new UserPointLog(userRep.findFirstByUserId(p.getUserId()), authUser, addedPoints, directBool, postPointLogService.findLastInsertedUserLog().getCurrentHash()));
            postPointLogService.calculatePointLogHash(userPointLog);

        }

        UI ui = UI.getCurrent();
        ui.access(() -> {
            ui.push();
        });
    }

    /**
     * Given a User and a Post that has been reposted, this method deletes the repost from user's profile.
     * @param reposter
     * @param post
     */
    public void deleteRepost(User reposter, Post post){

        BigInteger id = post.getOriginalPostId() != null ? post.getOriginalPostId() : post.getPostId();

        postRep.delete(postRep.findByOriginalPostIdAndUserId(id, reposter.getUserId()));
    }


    /**
     * Takes a Post and returns a list of MessageListItem, which are the comments related to that post.
     * @param post
     * @return
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
     * @param post
     * @param user
     * @param text
     */


    public void newComment(Post post, User user, String text){
        Comment comment = new Comment();

        comment.setPostId(post.getPostId());
        comment.setCommentDate(Instant.now());
        comment.setUserId(user.getUserId());
        comment.setUserComment(text);

        commentsRep.save(comment);
    }

    public int getAllComments(Post post) {
        return commentsRep.findAllByPostId(post.getPostId()).size();
    }
}
