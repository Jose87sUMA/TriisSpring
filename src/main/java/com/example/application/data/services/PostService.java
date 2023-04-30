package com.example.application.data.services;

import com.example.application.data.entities.Comment;
import com.example.application.data.entities.Like;
import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.repositories.CommentsRepository;
import com.example.application.data.repositories.LikesRepository;
import com.example.application.data.repositories.PostsRepository;
import com.example.application.data.repositories.UsersRepository;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.messages.*;
import com.vaadin.flow.server.StreamResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.time.*;
import java.util.*;

@Service
public class PostService {
    private final LikesRepository likeRep;
    private final PostsRepository postRep;
    private final UsersRepository userRep;
    private final CommentsRepository commentsRep;

    public PostService(LikesRepository likeRep, PostsRepository postRep, UsersRepository userRep, CommentsRepository commentsRep) {
        this.likeRep = likeRep;
        this.postRep = postRep;
        this.userRep = userRep;
        this.commentsRep = commentsRep;
    }

    public Post save(Post post){
        postRep.save(post);
        return post;
    }

    public Post findById(BigInteger postId){ return postRep.findFirstByPostId(postId); }
    public List<Post> findAllByUser(User user){ return postRep.findAllByUserId(user.getUserId()); }

    public Image getContent(Post post){

        byte[] imageBytes = post.getContent();
        Image image;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage bImg = ImageIO.read(bis);

            StreamResource resource = new StreamResource(post.getPostId()+".jpg", () -> new ByteArrayInputStream(imageBytes));
            image = new Image(resource, String.valueOf(post.getPostId()));
            scaleImage(image, bImg);

        } catch (Exception e) {
            image = getContent(this.findById(post.getOriginalPostId()));
        }
        return image;
    }

    private static void scaleImage(Image image, BufferedImage bufferedImage){
        int imgHeight = bufferedImage.getHeight();
        int imgWidth = bufferedImage.getWidth();
        double aspect = (double) imgHeight / (double) imgWidth;

        if(aspect <= 1){
            image.setWidth("400px");
            image.setHeight(400*aspect + "px");
        }else{
            image.setWidth(400*aspect + "px");
            image.setHeight("400px");
        }
    }

    public List<Post> getAllByPeopleFollowed(User user){return postRep.findAllByUsersFollowedByUserIdOrderByPostDateDesc(user.getUserId());}

    public int getAllLikes(Post p){
        return likeRep.findAllByPostId(p.getPostId()).size();
    }
    public List<User> getAllUsersLiking(Post p) {

        List <Like> likeEntries =  likeRep.findAllByPostId(p.getPostId());
        List <User> usersThatLiked = new ArrayList<>();

        for(Like likeEntry : likeEntries){
            usersThatLiked.add(userRep.findFirstByUserId(likeEntry.getUserId()));
        }

        return usersThatLiked;
    }
    public void likeButton(Post post, User authUser) {
        if(!this.getAllUsersLiking(post).contains(authUser)){
            this.newLike(authUser, post);
        }else{
            this.dislike(authUser, post);
        }
        this.save(post);
    }
    public void newLike(User user, Post post) {
        post.setLikes(post.getLikes().add(BigInteger.ONE));
        likeRep.save(new Like(user.getUserId(), post.getPostId()));
    }

    public void dislike(User user, Post post) {
        post.setLikes(post.getLikes().subtract(BigInteger.ONE));
        likeRep.delete(likeRep.findByUserIdAndPostId(user.getUserId(), post.getPostId()));
    }
    public List<MessageListItem> commentItems(Post post){
        List<Comment> commentList = commentsRep.findAllByPostId(post.getPostId());
        List<MessageListItem> itemList =new ArrayList<>();
        for(Comment c : commentList){

            Timestamp timestamp = new Timestamp((c.getCommentDate()).getTime());
            Instant i = timestamp.toInstant();

            MessageListItem item = new MessageListItem(c.getUserComment(), i,(userRep.findFirstByUserId(c.getUserId()).getUsername()));
            itemList.add(item);
        }
        return itemList;
    }




    public void newComment(Post post, User user, String text){
        Comment comment = new Comment();

        comment.setPostId(post.getPostId());
        comment.setCommentDate(Date.valueOf(LocalDate.now()));
        comment.setUserId(user.getUserId());
        comment.setUserComment(text);

        List<Comment> commentList = commentsRep.findAllByPostId(post.getPostId());
        BigInteger random_id = BigInteger.valueOf((new Random()).nextInt(30,1000));
        boolean found = false;
        for(Comment c : commentList)
            if (c.getCommentId().equals(random_id)) {
                found = true;
                break;
            }
        if(!found) comment.setCommentId(random_id);
        commentsRep.save(comment);
    }
}
