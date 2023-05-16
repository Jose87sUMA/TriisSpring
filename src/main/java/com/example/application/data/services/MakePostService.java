package com.example.application.data.services;

import com.example.application.data.exceptions.MakePostException;
import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;

@Service
public class MakePostService {

    @Autowired
    PostService postService;
    @Autowired
    UserService userService;

    private BigInteger necessaryPointsToMakeAPost =  new BigInteger("0");;

    /**
     * auxiliary functions related with making a post
     */
    public  MakePostService (){
    }
    /**
     * post a pointed post by file
     * if error throws corresponding exception
     */
    public Post postPointedByFile(User user, InputStream fileData) throws MakePostException, IOException {
        Post post = null;
        validateFileContent(fileData);
        if(checkEnoughPointsForPost(user)){
            try{
                validateFileContent(fileData);
                post = postService.creatPost(user, true, fileData);
                subtractPointsFromUser(user);

            }catch(Exception exception){
                throw new MakePostException("Something went wrong with server");
            }
        }else{
            throw new MakePostException("Not enough points");
        }
        return post;

    }

    /**
     * substracts points from user
     */
    private void subtractPointsFromUser(User user){
        user.setType1Points(user.getType1Points().subtract(getPostCost(user)));
        userService.save(user);
    }

    /**
     * post a non-pointed post by file
     * calls an auxiliary function to convert from link to input stream
     * if error throws corresponding exception
     */
    public Post postNotPointedByFile(User user, InputStream fileData) throws MakePostException, IOException {
        Post post = null;
        validateFileContent(fileData);
        try{
            post = postService.creatPost(user, false, fileData);
        }catch(Exception exception){
            throw new MakePostException("Something went wrong with server when inserting " + fileData.toString());
        }
        return post;
    }

    /**
     * post a pointed post by link
     * calls an auxiliary function to convert from link to input stream
     * if error throws corresponding exception
     */
    public Post postPointedByLink(User user, String link) throws MakePostException, IOException {
        return this.postPointedByFile(user, manageImageURL(link));

    }

    /**
     * post a non-pointed post by link
     * calls an auxiliary function to convert from link to input stream
     * if error throws corresponding exception
     */
    public Post postNotPointedByLink(User user, String link) throws MakePostException,IOException {
        return this.postNotPointedByFile(user, manageImageURL(link));
    }

    /**
     * returns if a user has enough points
     * @return
     */
    private boolean checkEnoughPointsForPost(User user) {
        return user.getType1Points().compareTo(getPostCost(user)) >= 0;
    }


    /**
     * checks if file is empty
     */
    private void validateFileContent(InputStream fileData) throws MakePostException,IOException {
        if(fileData == null || fileData.available() == 0) {
            throw new MakePostException("File is empty");
        }
    }

    /**
     * creates an input stream from image link
     */
    private InputStream manageImageURL(String link) throws MakePostException, IOException {
        InputStream fileData = null;
        if(link.isEmpty()){
            throw new MakePostException("No link ");
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
     *
     * @param user
     * @return how many points user would spend in a pointed post
     */
    public BigInteger getPostCost(User user){
        return new BigInteger(String.valueOf(Math.min(userService.getNumberOfFollowers(user)*3, 30000)));
    }
}
