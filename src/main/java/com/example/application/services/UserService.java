package com.example.application.services;

import com.example.application.data.entities.Follow;
import com.example.application.data.entities.Recommendation;
import com.example.application.data.entities.User;
import com.example.application.exceptions.UserException;
import com.example.application.data.repositories.FollowRepository;
import com.example.application.data.repositories.RecommendationRepository;
import com.example.application.data.repositories.UsersRepository;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;

@Service
public class UserService {

    private final UsersRepository userRep;
    private final FollowRepository followRep;
    private final RecommendationRepository recommendationRep;

    @Autowired
    DropboxService dropboxService;

    public UserService(UsersRepository userRepository, FollowRepository followRep, RecommendationRepository recommendationRep) {
        this.userRep = userRepository;
        this.followRep = followRep;
        this.recommendationRep = recommendationRep;
    }
    public User findById(BigInteger userId){ return userRep.findFirstByUserId(userId); }
    public User findByUsername(String username){ return userRep.findFirstByUsername(username); }
    public User findByEmail(String email){ return userRep.findFirstByEmail(email); }

    /**
     * Gets the bytes of the profile picture of a user. Gets from dropbox the image to be displayed which can be the default
     * one in case the user doesn't have a profile picture.
     * @param user
     * @return bytes of the profile picture
     */
    public byte [] getProfilePicBytes(User user) {
        return dropboxService.downloadProfilePicture(user);
    }

    /**
     * Gets the profile picture to be displayed for a user. Gets from dropbox the image to be displayed which can be the default
     * one in case the user doesn't have a profile picture.
     * @param user
     * @return Vaadin.Image object of the profile picture
     */
    public Image getProfilePicImage(User user) {
        String pathFile = (user.getProfilePicture() != null ? user.getProfilePicture() : "default.jpg");
        byte [] profilePictureBytes = getProfilePicBytes(user);

        Image image;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(profilePictureBytes);
            BufferedImage bImg = ImageIO.read(bis);

            StreamResource resource = new StreamResource(pathFile, () -> new ByteArrayInputStream(profilePictureBytes));
            image = new Image(resource, String.valueOf(user.getUserId()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;

    }

    /**
     * Gets the profile picture to be displayed for a user. Gets from dropbox the image to be displayed which can be the default
     * one in case the user doesn't have a profile picture.
     * @param user
     * @return Vaadin.StreamResource object of the profile picture
     */
    public StreamResource getProfilePicImageResource(User user){

        String pathFile = (user.getProfilePicture() != null ? user.getProfilePicture() : "default.jpg");
        byte [] profilePictureBytes = getProfilePicBytes(user);

        StreamResource resource = new StreamResource(pathFile, () -> new ByteArrayInputStream(profilePictureBytes));

        return resource;

    }

    public void changeProfilePicture(User user, InputStream filedata) throws UserException {
        dropboxService.uploadProfilePicture(user, filedata);
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

    public int getNumberOfFollowers(User user){
        return followRep.findAllByUserIdFollowing(user.getUserId()).size();
    }

    /**
     * Adds a Follow entity to the database.
     * @param follower User who is following.
     * @param following User who is being followed.
     */
    public void follow(User follower, User following){
        followRep.save(new Follow(follower.getUserId(), following.getUserId()));
    }

    /**
     * Removes a Follow entity to the database.
     * @param follower User who was following.
     * @param following User who was being followed.
     */
    public void unfollow(User follower, User following){
        followRep.delete(followRep.findByUserIdFollowerAndUserIdFollowing(follower.getUserId(), following.getUserId()));
    }

    /**
     * This method updates information about a user or saves a new User.
     * @param user
     * @return
     */
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

    @Async
    public void loadRecommendations(User user){

        Map<User, Integer> recommendationScore = new HashMap<>();
        List<User> following = this.getFollowing(user);

        for(User userFollowing : following){
            List<User> followingFollowing = this.getFollowing(userFollowing);

            for(User userFollowingFollowing : followingFollowing){
                if(!following.contains(userFollowingFollowing) && !userFollowingFollowing.equals(user))
                    recommendationScore.put(userFollowingFollowing, recommendationScore.getOrDefault(userFollowingFollowing,0)+1);

            }
        }

        for(Map.Entry<User, Integer> entry :  recommendationScore.entrySet()){

            Recommendation recommendation = recommendationRep.findByRecommendedUserIdAndRecommendationUserId(user.getUserId(), entry.getKey().getUserId());

            if(recommendation == null){
                recommendation = new Recommendation(user.getUserId(), entry.getKey().getUserId(), BigInteger.valueOf(entry.getValue()));
            }else{
                recommendation.setScore(BigInteger.valueOf(entry.getValue()));
            }

            recommendationRep.save(recommendation);

        }

    }
}
