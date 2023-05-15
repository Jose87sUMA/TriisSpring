package com.example.application.data.services;

import com.example.application.data.exceptions.MakePostException;
import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;

public class MakePostService {
    private final PostService postService;
    private final User authenticatedUser;
    private final UserService userService;
    boolean pointedPost;
    private InputStream fileData;
    private BigInteger userPoints =  new BigInteger("0");

    // Perform operations with the BigInteger;
    private BigInteger necessaryPointsToMakeAPost =  new BigInteger("0");;

    /**
     * auxiliary functions related with making a post
     * @param authenticatedUser
     * @param userService
     * @param postService
     */
    public  MakePostService ( User authenticatedUser,  UserService userService, PostService postService){
            this.authenticatedUser = authenticatedUser;
            this.postService = postService;
            this.userService = userService;
            this.pointedPost = false;

    }
    /**
     * post a pointed post by file
     * if error throws corresponding exception
     */
    public void postPointedByFile(InputStream postInputStream) throws MakePostException, IOException {
        fileData = postInputStream;
        validateFileContent();
        if(checkEnoughPointsForPost()){
            try{
                validateFileContent();
                Post post = postService.creatPost(authenticatedUser, true, fileData);
                //substract points from user
                subtractPointsFromUser();

            }catch(Exception exception){
                throw new MakePostException("Something went wrong with server");
            }
        }else{
            throw new MakePostException("Not enough points");
        }

    }

    /**
     * substracts points from user
     */
    private void subtractPointsFromUser(){
        userPoints.subtract(necessaryPointsToMakeAPost);
        authenticatedUser.setType1Points(userPoints);
        userService.save(authenticatedUser);
    }

    /**
     * post a non-pointed post by file
     * calls an auxiliary function to convert from link to input stream
     * if error throws corresponding exception
     */
    public void postNotPointedByFile(InputStream postInputStream) throws MakePostException, IOException {
        fileData = postInputStream;
        validateFileContent();
        try{
            Post post = postService.creatPost(authenticatedUser, false, null);
        }catch(Exception exception){
            throw new MakePostException("Something went wrong with server when inserting " + fileData.toString());
        }
    }

    /**
     * post a pointed post by link
     * calls an auxiliary function to convert from link to input stream
     * if error throws corresponding exception
     */
    public void postPointedByLink(String link) throws MakePostException, IOException {
        manageImageURL(link);
        this.postPointedByFile(fileData);

    }

    /**
     * post a non-pointed post by link
     * calls an auxiliary function to convert from link to input stream
     * if error throws corresponding exception
     */
    public void postNotPointedByLink(String link) throws MakePostException,IOException {
        manageImageURL(link);
        this.postNotPointedByFile(fileData);
    }

    /**
     * returns if a user has enough points
     * @return
     */
    private boolean checkEnoughPointsForPost() {
        this.userPoints = userService.findById(authenticatedUser.getUserId()).getType1Points();
        if(userPoints.compareTo(necessaryPointsToMakeAPost) >= 0){
            return true;
        }
        return false;
    }


    /**
     * checks if file is empty
     */
    private void validateFileContent() throws MakePostException,IOException {
            if(fileData == null || fileData.available() == 0) {
                throw new MakePostException("File is empty");
            }
    }

    /**
     * creates an input stream from image link
     */
    private void manageImageURL(String link) throws MakePostException, IOException {
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

    }
}
