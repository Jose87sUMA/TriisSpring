package com.example.application.data.services;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.repositories.PostsRepository;
import com.example.application.data.repositories.UsersRepository;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

@Service
public class PostService {

    private final PostsRepository postRep;
    public PostService(PostsRepository postRep) {
        this.postRep = postRep;
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

    public List<Post> getAllByPeopleFollowed(User user){return postRep.findAllByUsersFollowedByUserIdOrderByPostDateDesc(user.getUserId());}

    public Post save(Post post){
        postRep.save(post);
        return post;
    }

}
