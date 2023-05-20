package com.example.application.services;

import com.example.application.data.entities.*;
import com.example.application.data.repositories.*;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Service
public class PostService {

    private final PostsRepository postRep;
    private final UsersRepository userRep;
    private final ReportRepository reportRep;
    private final CommentsRepository commentsRep;
    private final LikesRepository likeRep;
    private final PostPointLogRepository postPointLogRep;
    private final UserPointLogRepository userPointLogRep;

    @Autowired
    DropboxService dropboxService;


    public PostService(LikesRepository likeRep, PostsRepository postRep, UsersRepository userRep, ReportRepository reportRep, CommentsRepository commentsRep, PostPointLogRepository postPointLogRep, UserPointLogRepository userPointLogRep) {
        this.likeRep = likeRep;
        this.postRep = postRep;
        this.userRep = userRep;
        this.reportRep = reportRep;
        this.commentsRep = commentsRep;
        this.postPointLogRep = postPointLogRep;
        this.userPointLogRep = userPointLogRep;
    }

    /**
     * This method updates information about a post or saves a new Post.
     * @param post
     * @return
     */
    public Post save(Post post){
        postRep.save(post);
        return post;
    }

    /**
     * This method removes a Post given as parameter.
     * @param post
     */
    public void deletePost(Post post){postRep.delete(post);}

    /**
     * Finds a post that has the given ID
     * @param postId
     * @return
     */
    public Post findById(BigInteger postId){ return postRep.findFirstByPostId(postId); }

    /**
     * Returns a list of the posts of the given user.
     * @param user
     * @return
     */
    public List<Post> findAllByUser(User user){ return postRep.findAllByUserId(user.getUserId()); }
    //public List<Post> findAllByUserAndDate(User user){ return postRep.findAllByUserIdOrderByPostDateDesc(user.getUserId()); }

    /**
     * Gets the content to be displayed from a post. Gets from dropbox the image to be displayed which can be from another
     * post in case the parameter is a repost.
     *
     * @param post
     * @param ui
     * @return Vaadin.Image object of the content ot be displayed
     */
    public Image getContent(Post post, UI ui){

        UI.setCurrent(ui);

        byte [] imageBytes = dropboxService.downloadPostContent(post);
        String pathFile = post.getContent() != null ? post.getContent() : postRep.findFirstByPostId(post.getOriginalPostId()).getContent();

        Image image;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage bImg = ImageIO.read(bis);

            byte[] finalImageBytes = imageBytes;
            StreamResource resource = new StreamResource(pathFile, () -> new ByteArrayInputStream(finalImageBytes));
            image = new Image(resource, String.valueOf(post.getPostId()));
            scaleImage(image, bImg);

        } catch (Exception e) {
            image = getContent(this.findById(post.getOriginalPostId()), ui);
        }
        return image;
    }

    private static void scaleImage(Image image, BufferedImage bufferedImage){
        int imgHeight = bufferedImage.getHeight();
        int imgWidth = bufferedImage.getWidth();
        double aspect = (double) imgHeight / (double) imgWidth;

//        if(aspect <= 1) {
            image.setWidth("400px");
            image.setHeight(400 * aspect + "px");
//        }else{
//            image.setWidth(400/aspect + "px");
//            image.setHeight("400px");
//        }
    }


    public static byte[] getBlobFromFile(File file){
        BufferedImage img = null;
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        ByteArrayOutputStream baos = null;

        try {
            baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                baos.close();
            } catch (Exception e) {
            }
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        return bais.readAllBytes();

    }

    /**
     * Returns a list of Post from users followed by the given user.
     * @param user
     * @return
     */
    public List<Post> getAllByPeopleFollowed(User user){return postRep.findAllByUsersFollowedByUserIdOrderByPostDateDesc(user.getUserId());}

    /**
     * Returns Post Repository.
     * @return
     */
    public PostsRepository getPostRepository(){
        return postRep;
    }

    public void newReport(User authenticatedUser, Post post, String reason) {
        reportRep.save(new Report(authenticatedUser, post, reason));
    }

    //POINTS LOGS

    public List<PostsPointLog> findAllLogsByPost (Post post){
        return postPointLogRep.findAllByPostIdOrderByLogDateDesc(post.getPostId());
    }


    /**
     * Creates a new post object and stores fileData in dropbox server with name postId.jpg
     * @param authenticatedUser
     * @param b
     * @param fileData
     * @return The newly created post
     */
    public Post createPost(User authenticatedUser, boolean b, InputStream fileData) {

        Post post = postRep.save(new Post(authenticatedUser, b));
        dropboxService.uploadPost(post, fileData);
        return post;
    }
}
