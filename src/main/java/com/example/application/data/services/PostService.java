package com.example.application.data.services;

import com.example.application.data.entities.*;
import com.example.application.data.repositories.*;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.sql.*;
import java.time.*;
import java.util.*;

@Service
public class PostService {
    private final LikesRepository likeRep;
    private final PostsRepository postRep;
    private final UsersRepository userRep;
    private final ReportRepository reportRep;
    private final CommentsRepository commentsRep;
    private final PostPointLogRepository postPointLogRep;
    private final UserPointLogRepository userPointLogRep;

    public PostService(LikesRepository likeRep, PostsRepository postRep, UsersRepository userRep, ReportRepository reportRep, CommentsRepository commentsRep, PostPointLogRepository postPointLogRep, UserPointLogRepository userPointLogRep) {
        this.likeRep = likeRep;
        this.postRep = postRep;
        this.userRep = userRep;
        this.reportRep = reportRep;
        this.commentsRep = commentsRep;
        this.postPointLogRep = postPointLogRep;
        this.userPointLogRep = userPointLogRep;
    }

    public Post save(Post post){
        postRep.save(post);
        return post;
    }

    public void deletePost(Post post){postRep.delete(post);}


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

    //LIKE BUTTON
    @Async
    public void newLike(User user, Post post) {
        post.setLikes(post.getLikes().add(BigInteger.ONE));
        postRep.save(post);
        likeRep.save(new Like(user.getUserId(), post.getPostId()));
    }

    @Async
    public void dislike(User user, Post post) {
        post.setLikes(post.getLikes().subtract(BigInteger.ONE));
        postRep.save(post);
        likeRep.delete(likeRep.findByUserIdAndPostId(user.getUserId(), post.getPostId()));
    }

    //REPOST BUTTON
    public boolean isReposted(Post post, User user){
                //User reposting a repost and has already reposted any post refering to the same original post as the repost he is reposting
       return (post.getOriginalPostId() != null && !postRep.findAllByUserIdAndOriginalPostId(user.getUserId(), post.getOriginalPostId()).isEmpty())
                //User reposting the original post and has already reposted any repost of the original post
               || !postRep.findAllByUserIdAndOriginalPostId(user.getUserId(), post.getPostId()).isEmpty(); //
    }

    @Async
    public void repost(Post post, User authUser, Button repostButton) {

        UI.setCurrent(repostButton.getUI().get());

        var ref = new Object() {
            boolean repostSuccess = true;
        };
        Notification repostSuccessNoti = new Notification("Reposted correctly.");
        repostSuccessNoti.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        repostSuccessNoti.setDuration(5000);

        //Post allows point investment?
        if(post.getPointed().equals("Y")){

            Notification usePoints = new Notification();
            usePoints.setDuration(-1);
            usePoints.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            Div statusText = new Div(new Text("Use points?"));

            Button yes = new Button("Yes");
            yes.addClickListener(event -> {

                UI.getCurrent().access(() -> {
                    usePoints.close();
                });

                //Enough Points?
                if(authUser.getType2Points().compareTo(BigInteger.valueOf(5)) >= 0){
                    Post newRepost = this.save(new Post(post, authUser, true));
                    this.pointDistribution(newRepost, authUser);
                    authUser.setType2Points(BigInteger.valueOf(authUser.getType2Points().intValue() - 5));
                    userRep.save(authUser);
                    UI.getCurrent().access(() -> {
                        repostSuccessNoti.open();
                    });
                }else{
                    Notification notEnough = new Notification("Not enough points for repost! Wait for next refill :)");
                    notEnough.addThemeVariants(NotificationVariant.LUMO_ERROR);

                    UI.getCurrent().access(() -> {
                        notEnough.open();
                        ref.repostSuccess = false;
                    });
                }
                UI.getCurrent().access(() -> {
                    repostButton.setEnabled(true);
                });
            });
            Button no = new Button("No");
            no.addClickListener(event -> {
                this.save(new Post(post, authUser, false));

                UI.getCurrent().access(() -> {
                    usePoints.close();
                    repostButton.setEnabled(true);
                    repostSuccessNoti.open();
                });
            });

            HorizontalLayout buttons = new HorizontalLayout(statusText, yes, no);
            buttons.setAlignItems(FlexComponent.Alignment.CENTER);
            usePoints.add(buttons);

            UI.getCurrent().access(() -> {
                usePoints.open();
            });

        }else{
            this.save(new Post(post, authUser, false));
            UI.getCurrent().access(() -> {
                repostButton.setEnabled(true);
            });
        }

        if(ref.repostSuccess){
            Icon icon = new Icon(VaadinIcon.RETWEET);
            icon.setColor("springgreen");
            UI.getCurrent().access(() -> {
                repostButton.setIcon(icon);
            });
        }

        System.out.println("thread");

    }

    @Async
    public void unrepost(Post post, User authUser, Button repostButton){

        UI.setCurrent(repostButton.getUI().get());

        Notification deletePostCheck = new Notification();
        deletePostCheck.setDuration(-1);
        deletePostCheck.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        Div statusText = new Div(new Text("Are you sure you want to unrepost? You won't get any used points back :("));

        Button yes = new Button("Yes");
        yes.addClickListener(event -> {
            this.deleteRepost(authUser, post);

            UI.getCurrent().access(() -> {
                deletePostCheck.close();
                repostButton.setIcon(new Icon(VaadinIcon.RETWEET));
                Notification.show("Unreposted");
                repostButton.setEnabled(true);
            });
        });
        Button no = new Button("No");
        no.addClickListener(event -> {
            UI.getCurrent().access(() -> {
                deletePostCheck.close();
                repostButton.setEnabled(true);
            });
        });
        HorizontalLayout buttons = new HorizontalLayout(statusText, yes, no);
        buttons.setAlignItems(FlexComponent.Alignment.CENTER);
        deletePostCheck.add(buttons);

        UI.getCurrent().access(() -> {
           deletePostCheck.open();
        });
        System.out.println("thread");

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
            postPointLogRep.save(new PostsPointLog(p, authUser, addedPoints, directBool));
            userPointLogRep.save(new UserPointLog(userRep.findFirstByUserId(p.getUserId()), authUser, addedPoints, directBool));
        }

        UI ui = UI.getCurrent();
        ui.access(() -> {
            ui.push();
        });
    }


    public void deleteRepost(User reposter, Post post){

        BigInteger id = post.getOriginalPostId() != null ? post.getOriginalPostId() : post.getPostId();

        deletePost(postRep.findByOriginalPostIdAndUserId(id, reposter.getUserId()));
    }


    //COMMENT RELATED
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
        comment.setCommentDate(new java.sql.Date(Instant.now().toEpochMilli()));
        comment.setUserId(user.getUserId());
        comment.setUserComment(text);

        commentsRep.save(comment);
    }


    public void newReport(User authenticatedUser, Post post, String reason) {
        reportRep.save(new Report(authenticatedUser, post, reason));
    }

    //POINTS LOGS

    public List<PostsPointLog> findAllLogsByPost (Post post){

        return postPointLogRep.findAllByPostIdOrderByLogDateDesc(post.getPostId());

    }


}
