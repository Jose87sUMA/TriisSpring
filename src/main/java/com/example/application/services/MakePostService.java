package com.example.application.services;

import com.example.application.exceptions.MakePostException;
import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
/**
 * Service related with making a post.
 */
@Service
public class MakePostService {

    @Autowired
    PostService postService;
    @Autowired
    UserService userService;


    public  MakePostService (){
    }

    /**
     * Post a pointed post by file.
     * If there is an error throws corresponding exception.
     *
     * @param user
     * @param fileData
     * @return
     * @throws MakePostException
     * @throws IOException
     * @author Ksenia Myakisheva
     */
    public Post postPointedByFile(User user, InputStream fileData) throws MakePostException, IOException {
        validateFileContent(fileData);
        if(checkEnoughPointsForPost(user)){
            try{
                Post post = postService.createPost(user, true, fileData);
                //substract points from user
                subtractPointsFromUser(user, getPostCost(user));
                return post;

            }catch(Exception exception){
                throw new MakePostException("Something went wrong with server");
            }
        }else{
            throw new MakePostException("Not enough points");
        }

    }

    /**
     * Post a non-pointed post by file.
     * Calls an auxiliary function to convert from link to input stream.
     * If there is an error throws corresponding exception.
     *
     * @param user
     * @param fileData
     * @return
     * @throws MakePostException
     * @throws IOException
     */
    public Post postNotPointedByFile(User user, InputStream fileData) throws MakePostException, IOException {
        validateFileContent(fileData);
        try{
            Post post = postService.createPost(user, false, fileData);
            return post;
        }catch(Exception exception){
            throw new MakePostException("Something went wrong with server when inserting " + fileData.toString());
        }
    }


    /**
     * Post a pointed post by link.
     * Calls an auxiliary function to convert from link to input stream.
     * If there is an error throws corresponding exception.
     *
     * @param user
     * @param link
     * @return
     * @throws MakePostException
     * @throws IOException
     * @author Ksenia Myakisheva
     */
    public Post postPointedByLink(User user, String link) throws MakePostException, IOException {
        return this.postPointedByFile(user, manageImageURL(link));

    }

    /**
     * Post a non-pointed post by link.
     * Calls an auxiliary function to convert from link to input stream.
     * If there is an error throws corresponding exception.
     *
     * @param user
     * @param link
     * @return
     * @throws MakePostException
     * @throws IOException
     * @author Ksenia Myakisheva
     */
    public Post postNotPointedByLink(User user, String link) throws MakePostException,IOException {
        return this.postNotPointedByFile(user, manageImageURL(link));
    }

    /**
     * Subtracts points from user
     *
     * @param user
     * @param points
     * @author Ksenia Myakisheva
     */
    public void subtractPointsFromUser(User user, BigInteger points){
        user.setType1Points(user.getType1Points().subtract(points));
        userService.save(user);
    }

    /**
     * Checks whether a user has enough points to make a post or not.
     *
     * @param user
     * @return
     * @author Ksenia Myakisheva
     */
    public boolean checkEnoughPointsForPost(User user) {
        return user.getType1Points().compareTo(getPostCost(user)) >= 0;
    }


    /**
     * Checks if file is empty.
     *
     * @param fileData
     * @throws MakePostException
     * @throws IOException
     * @author Ksenia Myakisheva
     */
    public void validateFileContent(InputStream fileData) throws MakePostException,IOException {
        if(fileData == null || fileData.available() == 0) {
            throw new MakePostException("File is empty");
        }
    }

    /**
     * Creates an input stream from image link
     *
     * @param link
     * @return
     * @throws MakePostException
     * @throws IOException
     * @author Ksenia Myakisheva
     */
    public InputStream manageImageURL(String link) throws MakePostException, IOException {
        InputStream fileData = null;
        if(link.isEmpty()){
            throw new MakePostException("No link");
        }
        try{
            URL imageUrl = new URL(link);
            URLConnection connection = imageUrl.openConnection();
            String contentType = connection.getHeaderField("Content-Type");
            if (contentType.equals("image/jpeg") || contentType.equals("image/jpg") || contentType.equals("image/png")) {
                fileData = connection.getInputStream();
            }else{
                throw new MakePostException("Available formats for file: png, jpg, jpeg");
            }
        }catch(IOException e){
            throw new MakePostException("Not a valid link");

        }
        return fileData;

    }

    /**
     * @param user
     * @return How many points user would spend in a pointed post
     * @author Ksenia Myakisheva
     */
    public BigInteger getPostCost(User user){
        return new BigInteger(String.valueOf(Math.min(userService.getNumberOfFollowers(user)*3, 30000)));
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setPostService(PostService postService) {
        this.postService = postService;
    }
}
