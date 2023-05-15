package com.example.application.data.services;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.example.application.data.entities.Follow;
import com.example.application.data.entities.FollowCompositePK;
import com.example.application.data.entities.User;
import com.example.application.data.repositories.FollowRepository;
import com.example.application.data.repositories.UsersRepository;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final String ACCESS_TOKEN = "sl.Bee1ZEEpAqyABocpTctUBufdIpQAsx2vQh50t6kNVTLWXzuQlMpLazdMs_e0l2t0wMPaAQEpts0wz8CVBY60BcAC1phk4toeMr7MM3LHllS8V7Q7_H1U5XytAch6yIDmmnZugt0";
    private final UsersRepository userRep;
    private final FollowRepository followRep;
    public UserService(UsersRepository userRepository, FollowRepository followRep) {
        this.userRep = userRepository;
        this.followRep = followRep;
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
    public byte [] getProfilePicImageBytes(User user) {

        String pathFile = "/ProfilePictures/" + (user.getProfilePicture() != null ? user.getProfilePicture() : "default.jpg");

        return getBytesFromDropbox(pathFile);

    }

    /**
     * Gets the profile picture to be displayed for a user. Gets from dropbox the image to be displayed which can be the default
     * one in case the user doesn't have a profile picture.
     * @param user
     * @return Vaadin.Image object of the profile picture
     */
    public Image getProfilePicImage(User user) {
        String pathFile = "/ProfilePictures/" + (user.getProfilePicture() != null ? user.getProfilePicture() : "default.jpg");
        byte [] profilePictureBytes = getProfilePicImageBytes(user);

        Image image;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(profilePictureBytes);
            BufferedImage bImg = ImageIO.read(bis);

            StreamResource resource = new StreamResource(user.getUsername(), () -> new ByteArrayInputStream(profilePictureBytes));
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

        String pathFile = "/ProfilePictures/" + (user.getProfilePicture() != null ? user.getProfilePicture() : "default.jpg");
        byte [] profilePictureBytes = getProfilePicImageBytes(user);

        StreamResource resource = new StreamResource(user.getUsername(), () -> new ByteArrayInputStream(profilePictureBytes));

        return resource;

    }

    /**
     * Gets the bytes of the picture to be displayed. Gets from dropbox the image to be displayed from the pathFile
     * @param pathFile
     * @return bytes of image on dropbox
     */
    protected byte[] getBytesFromDropbox(String pathFile){

        DbxRequestConfig config = DbxRequestConfig.newBuilder("Triis").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        byte[] imageBytes;
        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            client.files().download(pathFile).download(outputStream);
            imageBytes = outputStream.toByteArray();

        } catch (DbxException | IOException e) {
            throw new RuntimeException(e);
        }

        return imageBytes;

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
}
