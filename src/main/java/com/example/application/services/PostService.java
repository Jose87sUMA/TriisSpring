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

/**
 * Service that manages posts.
 */
@Service
public class PostService {

    private final PostsRepository postRep;
    private final ReportRepository reportRep;
    private final PostPointLogRepository postPointLogRep;

    @Autowired
    DropboxService dropboxService;


    /**
     * @param likeRep
     * @param postRep
     * @param reportRep
     * @param postPointLogRep
     */
    public PostService(LikesRepository likeRep, PostsRepository postRep, ReportRepository reportRep, PostPointLogRepository postPointLogRep) {
        this.postRep = postRep;
        this.reportRep = reportRep;
        this.postPointLogRep = postPointLogRep;
    }

    /**
     * This method updates information about a post or saves a new Post.
     *
     * @param post to be updated or saved
     * @return Updated post
     * @author Daniel de los Ríos García
     */
    public Post save(Post post){
        postRep.save(post);
        return post;
    }

    /**
     * This method removes a Post given as parameter.
     *
     * @param post Post to be removed
     * @author Daniel de los Ríos García
     */
    public void deletePost(Post post){postRep.delete(post);}

    /**
     * Finds a post that has the given ID.
     *
     * @param postId ID of post to find
     * @return Post with the given ID
     */
    public Post findById(BigInteger postId){ return postRep.findFirstByPostId(postId); }

    /**
     * Gets the content to be displayed from a post. Gets from dropbox the image to be displayed which can be from another
     * post in case the parameter is a repost.
     *
     * @param post Post for which the
     * @param ui
     * @return Vaadin.Image object of the content ot be displayed
     * @author José Alejandro Sarmiento
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

    /**
     * Scales a given image.
     *
     * @param image Image to be scaled.
     * @param bufferedImage Same image but as a BufferedImage. Needed to get the dimensions.
     * @author José Alejandro Sarmiento & Ziri Raha
     */
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

    /**
     * Returns Post Repository.
     * @return
     * @author Ziri Raha
     */
    public PostsRepository getPostRepository(){
        return postRep;
    }

    /**
     * Creates a new report with the given parameters.
     *
     * @param user
     * @param post
     * @param reason
     * @author José Alejandro Sarmiento
     */
    public void newReport(User user, Post post, String reason) {
        reportRep.save(new Report(user, post, reason));
    }

    //POINTS LOGS

    /**
     * Finds all the post point logs of a given post.
     *
     * @param post Post for which we need the point logs.
     * @return List of post point logs of the given post.
     * @author José Alejandro Sarmiento
     */
    public List<PostsPointLog> findAllLogsByPost (Post post){
        return postPointLogRep.findAllByPostIdOrderByLogDateDesc(post.getPostId());
    }


    /**
     * Creates a new post object and stores fileData in dropbox server with name postId.jpg
     *
     * @param authenticatedUser
     * @param b
     * @param fileData
     * @return The newly created post
     * @author José Alejandro Sarmiento
     */
    public Post createPost(User authenticatedUser, boolean b, InputStream fileData) {
        Post post = postRep.save(new Post(authenticatedUser, b));
        dropboxService.uploadPost(post, fileData);
        return post;
    }
}
