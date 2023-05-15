package com.example.application.data.services;

import com.example.application.data.entities.Follow;
import com.example.application.data.entities.User;
import com.example.application.data.repositories.FollowRepository;
import com.example.application.data.repositories.UsersRepository;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
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
    public User findByEmail(String email){ return userRep.findFirstByEmail(email); }

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

    public List<User> getFollowers(User user){

        List<Follow> followingEntries = followRep.findAllByUserIdFollowing(user.getUserId());
        List<User> followers = new ArrayList<>();

        for(Follow followEntry: followingEntries){

            followers.add(this.findById(followEntry.getUserIdFollower()));

        }
        return followers;
    }

    public User save(User user){
        userRep.save(user);
        return user;
    }

    List<User> findAllByMatchingUsername(String match){return userRep.findAllByUsernameContainsIgnoreCase(match);}

    public boolean editUsername(User user, String username) {
        if(!username.isEmpty() && username.matches("^[a-z0-9]+$") && findByUsername(username) == null) {
            user.setUsername(username);
            userRep.save(user);
            return true;
        }else{
            return false;
        }
    }

    public boolean editEmail(User user, String email) {
        if(!email.isEmpty() && findByEmail(email) == null) {
            user.setEmail(email);
            userRep.save(user);
            return true;
        }else{
            return false;
        }
    }

    public void editPassword(User user, String password) {
        user.setPassword((new BCryptPasswordEncoder()).encode(password));
    }


    public List<User> findAllFollowing(String stringFilter, User user) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return getFollowing(user);
        } else {

            return userRep.searchFollowing(stringFilter,getFollowing(user));
        }
    }
   public List<User> findAllFollower(String stringFilter, User user) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return getFollowers(user);
        } else {

            return userRep.searchFollowers(stringFilter, getFollowers(user));
        }
    }


}
