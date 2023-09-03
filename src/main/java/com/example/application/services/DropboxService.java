package com.example.application.services;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.exceptions.UserException;
import com.example.application.data.repositories.PostsRepository;
import com.example.application.data.repositories.UsersRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class that manages the connections to the dropbox repository containing all the images.
 */
@Service
public class DropboxService {

    private String ACCESS_TOKEN = System.getenv("DROPBOX_ACCESS_TOKEN");
    private String REFRESH_TOKEN = System.getenv("DROPBOX_REFRESH_TOKEN");
    private String APP_KEY = System.getenv("DROPBOX_APP_KEY");
    private String APP_SECRET = System.getenv("DROPBOX_APP_SECRET");
    private final PostsRepository postRep;
    private final UsersRepository userRep;


    /**
     * @param postRep
     * @param userRep
     */
    public DropboxService(PostsRepository postRep, UsersRepository userRep) {
        this.postRep = postRep;
        this.userRep = userRep;
    }

    /**
     * Downloads the image that corresponds to a certain post.
     *
     * @param post Post for which the content will be downloaded
     * @return the byte array of the image
     * @author José Alejandro Sarmiento
     */
    public byte[] downloadPostContent(Post post){

        DbxClientV2 client = getDbxClientV2();
        String pathFile = post.getContent() != null ? post.getContent() : postRep.findFirstByPostId(post.getOriginalPostId()).getContent();
        byte[] imageBytes = null;

        boolean success = false;
        while(!success){
            try {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                client.files().download("/posts/" + pathFile).download(outputStream);
                imageBytes = outputStream.toByteArray();
                success = true;
            } catch (InvalidAccessTokenException e) {
                refreshToken(client);
            }catch(IOException | DbxException e){
                e.printStackTrace();
            }
        }



        return imageBytes;
    }

    /**
     * Uploads a certain image, contained in the InputStream, as the content of a certain post.
     *
     * @param post Post that will contain the uploaded image
     * @param fileData InputStream containing the image.
     * @return The updated post
     * @author José Alejandro Sarmiento
     */
    public Post uploadPost(Post post, InputStream fileData){

        DbxClientV2 client = getDbxClientV2();
        boolean success = false;
        while(!success) {
            try {

                FileMetadata metadata = client.files().uploadBuilder("/Posts/" + post.getPostId() + ".jpg")
                                                      .withMode(WriteMode.OVERWRITE)
                                                      .uploadAndFinish(fileData);
                post.setContent(metadata.getName());
                postRep.save(post);
                success = true;

            } catch (InvalidAccessTokenException e) {
                refreshToken(client);
            } catch (IOException | DbxException e) {
                e.printStackTrace();
            }
        }
        return post;
    }

    /**
     * Downloads the profile picture of a given user.
     *
     * @param user User for which the profile picture will be downloaded
     * @return the byte array of the image
     * @author José Alejandro Sarmiento
     */
    public byte[] downloadProfilePicture(User user) {

        String pathFile = (user.getProfilePicture() != null ? user.getProfilePicture() : "default.jpg");

        DbxClientV2 client = getDbxClientV2();
        byte[] imageBytes = null;
        boolean success = false;
        while(!success){
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                client.files().download("/ProfilePictures/" + pathFile).download(outputStream);
                imageBytes = outputStream.toByteArray();
                success = true;
            } catch (InvalidAccessTokenException e) {
                refreshToken(client);
            }catch(IOException | DbxException e){ e.printStackTrace();}
        }

        return imageBytes;
    }

    /**
     * Uploads a certain image, contained in the InputStream, as the profile picture of a certain user.
     *
     * @param user User that will contain the uploaded image
     * @param fileData InputStream containing the image.
     * @return The updated User
     * @throws UserException If there is an error uploading the image.
     * @author José Alejandro Sarmiento
     */
    public User uploadProfilePicture(User user, InputStream fileData) throws UserException{

        DbxClientV2 client = getDbxClientV2();
        boolean success = false;
        while(!success) {
            try {

                FileMetadata metadata = client.files().uploadBuilder("/ProfilePictures/" + user.getUserId() + ".jpg")
                                                      .withMode(WriteMode.OVERWRITE)
                                                      .uploadAndFinish(fileData);
                user.setProfilePicture(metadata.getName());
                userRep.save(user);

                success = true;

            } catch (InvalidAccessTokenException e) {
                refreshToken(client);
            } catch (IOException | DbxException e) {
                throw new UserException("Error uploading image");
            }
        }
        return user;
    }

    /**
     * Gets the Dropbox Client used to upload or download files.
     *
     * @return the Dropbox client
     * @author José Alejandro Sarmiento
     */
    protected DbxClientV2 getDbxClientV2() {
        DbxCredential cred = new DbxCredential(ACCESS_TOKEN, 14400L, REFRESH_TOKEN, APP_KEY, APP_SECRET);
        DbxRequestConfig config = DbxRequestConfig.newBuilder("Triis").build();
        DbxClientV2 client = new DbxClientV2(config, cred);
        return client;
    }

    /**
     * Refreshes the access token in case it has expired
     *
     * @param client Dropbox Client that we are trying to access
     * @author José Alejandro Sarmiento
     */
    protected void refreshToken(DbxClientV2 client) {
        try {
            ACCESS_TOKEN = client.refreshAccessToken().getAccessToken();
            System.out.println("New access token: " + ACCESS_TOKEN);
        } catch (Exception refreshException) {
            System.out.println("Failed to refresh token: " + refreshException.getMessage());
        }
    }


}
