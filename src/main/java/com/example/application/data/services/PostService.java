package com.example.application.data.services;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.repositories.PostsRepository;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
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

    public List<Post> getAllByPeopleFollowed(User user){return postRep.findAllByUsersFollowedByUserIdOrderByPostDateDesc(user.getUserId());}

    /**
     * Returns Post Repository.
     * @return
     */
    public PostsRepository getPostRepository(){
        return postRep;
    }

    //modifica el nombre de esto
    public List<Post> findTenByPointedOriginalPostIdOrderByPointsDescCreatedToday(){
        return postRep.findTenByPointedAndOriginalPostIdIsNullCreatedAtAfterOrderByPointsDesc(Date.valueOf(LocalDate.now()));
    }
    public List<Post> findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisWeek(){
        return postRep.findTenByPointedAndOriginalPostIdIsNullCreatedAtAfterOrderByPointsDesc(Date.valueOf(LocalDate.now().minusWeeks(1)));
    }

    public List<Post> findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisMonth(){
        return postRep.findTenByPointedAndOriginalPostIdIsNullCreatedAtAfterOrderByPointsDesc(Date.valueOf(LocalDate.now().minusMonths(1)));
    }

    public List<Post> findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisYear(){
        return postRep.findTenByPointedAndOriginalPostIdIsNullCreatedAtAfterOrderByPointsDesc(Date.valueOf(LocalDate.now().minusYears(1)));
    }

    public List<Post> findTenByPointedOriginalPostIdOrderByPointsDesc(){
        return postRep.findTenByPointedAndOriginalPostIdIsNullOrderByPointsDesc();
    }
}
