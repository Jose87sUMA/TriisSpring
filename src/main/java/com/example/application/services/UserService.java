package com.example.application.services;

import com.example.application.data.entities.Follow;
import com.example.application.data.entities.Recommendation;
import com.example.application.data.entities.User;
import com.example.application.data.entities.UserPointLog;
import com.example.application.data.repositories.UserPointLogRepository;
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
    private final UserPointLogRepository userPointLogRep;

    @Autowired
    DropboxService dropboxService;

    /**
     * @param userRepository
     * @param followRep
     * @param recommendationRep
     * @param userPointLogRep
     */
    public UserService(UsersRepository userRepository, FollowRepository followRep, RecommendationRepository recommendationRep, UserPointLogRepository userPointLogRep) {
        this.userRep = userRepository;
        this.followRep = followRep;
        this.recommendationRep = recommendationRep;
        this.userPointLogRep = userPointLogRep;
    }

    /**
     * This method updates information about a user or saves a new User.
     *
     * @param user
     * @return
     * @author José Alejandro Sarmiento
     */
    public User save(User user){
        userRep.save(user);
        return user;
    }

    /**
     * Finds a User by their ID.
     *
     * @param userId ID of user to find.
     * @return Found User
     * @author José Alejandro Sarmiento
     */
    public User findById(BigInteger userId){ return userRep.findFirstByUserId(userId); }

    /**
     * Finds a User by their username.
     *
     * @param username Username of user to find.
     * @return Found User
     * @author José Alejandro Sarmiento
     */
    public User findByUsername(String username){ return userRep.findFirstByUsername(username); }

    /**
     * Finds a User by their email.
     *
     * @param email Email of user to find.
     * @return Found User
     * @author Ksenia Myakisheva
     */
    public User findByEmail(String email){ return userRep.findFirstByEmail(email); }

    /**
     * Gets the bytes of the profile picture of a user. Gets from dropbox the image to be displayed which can be the default
     * one in case the user doesn't have a profile picture.
     *
     * @param user
     * @return bytes of the profile picture
     * @author José Alejandro Sarmiento
     */
    public byte [] getProfilePicBytes(User user) {
        return dropboxService.downloadProfilePicture(user);
    }

    /**
     * Gets the profile picture to be displayed for a user. Gets from dropbox the image to be displayed which can be the default
     * one in case the user doesn't have a profile picture.
     *
     * @param user
     * @return Vaadin.Image object of the profile picture
     * @author José Alejandro Sarmiento
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
     *
     * @param user
     * @return Vaadin.StreamResource object of the profile picture
     * @author José Alejandro Sarmiento
     */
    public StreamResource getProfilePicImageResource(User user){

        String pathFile = (user.getProfilePicture() != null ? user.getProfilePicture() : "default.jpg");
        byte [] profilePictureBytes = getProfilePicBytes(user);

        StreamResource resource = new StreamResource(pathFile, () -> new ByteArrayInputStream(profilePictureBytes));

        return resource;

    }

    /**
     * Changes the profile picture of a given user.
     *
     * @param user User that is changing their profile picture.
     * @param fileData InputStream containing the new profile picture.
     * @throws UserException Thrown by dropboxService.uploadProfilePicture.
     * @author José Alejandro Sarmiento
     */
    public void changeProfilePicture(User user, InputStream fileData) throws UserException {
        dropboxService.uploadProfilePicture(user, fileData);
    }

    /**
     * Finds and returns the users followed by the user given as parameter.
     *
     * @param user
     * @return List of followed users.
     * @author José Alejandro Sarmiento
     */
    public List<User> getFollowing(User user){

        List<Follow> followingEntries = followRep.findAllByUserIdFollower(user.getUserId());
        List<User> followingUsers = new ArrayList<>();

        for(Follow followEntry: followingEntries){
            followingUsers.add(this.findById(followEntry.getUserIdFollowing()));
        }
        return followingUsers;
    }

    /**
     * Finds and returns the followers of a user given as parameter.
     *
     * @param user
     * @return List of the user's followers.
     * @author José Alejandro Sarmiento
     */
    public List<User> getFollowers(User user){

        List<Follow> followingEntries = followRep.findAllByUserIdFollowing(user.getUserId());
        List<User> followers = new ArrayList<>();

        for(Follow followEntry: followingEntries){

            followers.add(this.findById(followEntry.getUserIdFollower()));

        }
        return followers;
    }

    /**
     * @param user
     * @return Number of followers of the given user.
     * @author Ksenia Myakisheva
     */
    public int getNumberOfFollowers(User user){
        return followRep.findAllByUserIdFollowing(user.getUserId()).size();
    }

    /**
     * Adds a Follow entity to the database.
     *
     * @param follower User who is following.
     * @param following User who is being followed.
     * @author Ziri Raha
     */
    public void follow(User follower, User following){
        followRep.save(new Follow(follower.getUserId(), following.getUserId()));
    }

    /**
     * Removes a Follow entity to the database.
     *
     * @param follower User who was following.
     * @param following User who was being followed.
     * @author Ziri Raha
     */
    public void unfollow(User follower, User following){
        followRep.delete(followRep.findByUserIdFollowerAndUserIdFollowing(follower.getUserId(), following.getUserId()));
    }

    /**
     * Finds all the users matching the pattern.
     *
     * @param match Pattern
     * @return List of Users that match the pattern.
     * @author Laura de Haro García
     */
    List<User> findAllByMatchingUsername(String match){return userRep.findAllByUsernameContainsIgnoreCase(match);}

    /**
     * Updates the password of a given user.
     *
     * @param user
     * @param password new Password
     * @author José Alejandro Sarmiento
     */
    public void editPassword(User user, String password) {
        user.setPassword((new BCryptPasswordEncoder()).encode(password));
    }

    /**
     * Updates the recommendations of a given User.
     * Recommendations for a certain user are the users followed by the users followed by the user.
     *
     * @param user User for which the recommendations are being generated.
     * @author José Alejandro Sarmiento
     */
    @Async
    public void loadRecommendations(User user){

        recommendationRep.deleteAllByRecommendedUserId(user.getUserId());

        Map<User, Integer> recommendationScore = new HashMap<>();
        List<User> following = this.getFollowing(user);

        for(User userFollowing : following){
            List<User> followingFollowing = this.getFollowing(userFollowing);

            for(User userFollowingFollowing : followingFollowing){
                if(!following.contains(userFollowingFollowing) && !userFollowingFollowing.equals(user))
                    recommendationScore.put(userFollowingFollowing,
                                            recommendationScore.getOrDefault(userFollowingFollowing,0)+1);

            }
        }

        for(Map.Entry<User, Integer> entry :  recommendationScore.entrySet()){
            Recommendation recommendation = new Recommendation(user.getUserId(),
                                                               entry.getKey().getUserId(),
                                                               BigInteger.valueOf(entry.getValue()));
            recommendationRep.save(recommendation);
        }

    }

    //Point logs

    /**
     * Finds all the user point logs where the given user is the beneficiary.
     *
     * @param user
     * @return List of UserPointLogs
     * @author José Alejandro Sarmiento
     */
    public List<UserPointLog> findAllUserLogsByBeneficiary(User user){
        return userPointLogRep.findAllByBenfUserIdOrderByPointsDesc(user.getUserId());
    }

    /**
     * Finds all the users that match the given pattern.
     *
     * @param pattern
     * @return List of Users that match the pattern.
     * @author Laura de Haro García
     */
    public List<User> findAllProfiles(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return null;
        } else {
            return userRep.searchUsers(pattern);
        }
    }

    /**
     * Finds all the users followed by the User given as parameter that match the given pattern.
     *
     * @param pattern
     * @param user
     * @return List of Users that match the pattern.
     * @author Laura de Haro García
     */
    public List<User> findAllFollowing(String pattern, User user) {
        if (pattern == null || pattern.isEmpty()) {
            return getFollowing(user);
        } else {
            return userRep.searchFollowing(pattern,getFollowing(user));
        }
    }

    /**
     * Finds all the users that follow the User given as parameter that match the given pattern.
     *
     * @param pattern
     * @param user
     * @return List of Users that match the pattern.
     * @author Laura de Haro García
     */
    public List<User> findAllFollower(String pattern, User user) {
        if (pattern == null || pattern.isEmpty()) {
            return getFollowers(user);
        } else {

            return userRep.searchFollowers(pattern, getFollowers(user));
        }
    }

}
