package com.example.application.data.services;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.repositories.PostsRepository;
import com.example.application.data.repositories.UsersRepository;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import org.springframework.scheduling.annotation.Async;
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

    private final String ACCESS_TOKEN = "sl.BeYi82OjcuWIxFq-zQtFvMqeLCh-pPKkPbMWtYYLQCne3PMgPG2uL_o_53R5FSWMmA42xIokuwrR_DhBDGTWyP-TlAlFZLyjj3pv4MtLeVzLKvCdFfoxT6NP3aE7VsFgEXgNURU";
    private final PostsRepository postRep;
    public PostService(PostsRepository postRep) {
        this.postRep = postRep;
    }
    public Post findById(BigInteger postId){ return postRep.findFirstByPostId(postId); }
    public List<Post> findAllByUser(User user){ return postRep.findAllByUserId(user.getUserId()); }
    public List<Post> findAllByUserAndDate(User user){ return postRep.findAllByUserIdOrderByPostDateDesc(user.getUserId()); }

    /**
     * Gets the content to be displayed from a post. Gets from dropbox the image to be displayed which can be from another
     * post in case the parameter is a repost.
     * @param post
     * @return Vaadin.Image object of the content ot be displayed
     */
    @Async
    public Image getContent(Post post){

        DbxRequestConfig config = DbxRequestConfig.newBuilder("Triis").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        String pathFile = post.getContent() != null ? post.getContent()  : postRep.findFirstByPostId(post.getOriginalPostId()).getContent();
        byte[] imageBytes;
        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            client.files().download("/posts/" + pathFile).download(outputStream);
            imageBytes = outputStream.toByteArray();

        } catch (DbxException | IOException e) {
            throw new RuntimeException(e);
        }

        Image image;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage bImg = ImageIO.read(bis);

            StreamResource resource = new StreamResource(pathFile, () -> new ByteArrayInputStream(imageBytes));
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

    /**
     * Get All the Posts of people followed by user given as parameter
     * @param user
     * @return
     */
    public List<Post> getAllByPeopleFollowed(User user){return postRep.findAllByUsersFollowedByUserIdOrderByPostDateDesc(user.getUserId());}

    @Async
    public Post save(Post post){
        postRep.save(post);
        return post;
    }

    /**
     * Creates a new post object and stores fileData in dropbox server with name postId.jpg
     * @param authenticatedUser
     * @param b
     * @param fileData
     * @return
     */
    @Async
    public Post creatPost(User authenticatedUser, boolean b, InputStream fileData) {

        Post post = postRep.save(new Post(authenticatedUser, b));

        DbxRequestConfig config = DbxRequestConfig.newBuilder("Triis").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        try {
            FileMetadata metadata = client.files().uploadBuilder("/Posts/" + post.getPostId() + ".jpg")
                    .uploadAndFinish(fileData);
            post.setContent(metadata.getName());
            postRep.save(post);
        } catch (DbxException | IOException e) {
            throw new RuntimeException(e);
        }

        return post;
    }
}
