package com.example.application.data.services;

import com.example.application.data.entities.Follow;
import com.example.application.data.entities.User;
import com.example.application.data.repositories.FollowRepository;
import com.example.application.data.repositories.UsersRepository;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {


    private final UsersRepository userRep;
    private final FollowRepository followRep;
    public UserService(UsersRepository userRepository, FollowRepository followRep) {
        this.userRep = userRepository;
        this.followRep = followRep;
    }
    public User findById(BigInteger userId){ return userRep.findFirstByUserId(userId); }
    public User findByUsername(String username){ return userRep.findFirstByUsername(username); }

    public Image getProfilePicImage(User user){

        byte[] imageBytes = user.getProfilePicture();
        StreamResource resource = new StreamResource(user.getUsername()+".jpg", () -> new ByteArrayInputStream(imageBytes));
        Image image = new Image(resource, user.getUsername());

        return image;

    }

    public StreamResource getProfilePicImageResource(User user){

        byte[] imageBytes = user.getProfilePicture();
        StreamResource resource = new StreamResource(user.getUsername()+".jpg", () -> new ByteArrayInputStream(imageBytes));

        return resource;

    }

    public List<User> getFollowing(User user){

        List<Follow> followingEntries = followRep.findAllByUserIdFollower(user.getUserId());
        List<User> followingUsers = new ArrayList<>();

        for(Follow followEntry: followingEntries){
            followingUsers.add(this.findById(followEntry.getUserIdFollowing()));
        }
        return followingUsers;
    }

    public User save(User user){
        userRep.save(user);
        return user;
    }


}
