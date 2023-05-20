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

@Service
public class DropboxService {


    private String ACCESS_TOKEN = "sl.Bec4FfXSlcBqmOOTUMfGjvdYQu5V2hIwkpxeV7mMckUeR8EAcZM1lryZPeIWj4Lk3hHOMQ-plG-yEWtRYefrKbjCooXqhqRJ_tUY4QpmbdryuAroCGiTcsR5Vj_kvrJYl0iR4zI";
    private String REFRESH_TOKEN = "6d8EnIVISdoAAAAAAAAAAVPNMHcp1U2Ys9hTuDPhTreq1YrZCHG6hYZyipFG9x8-";
    private String APP_KEY = "t6v4qds3tflheb3";
    private String APP_SECRET = "26k0xiwwznt3r25";
    private final PostsRepository postRep;
    private final UsersRepository userRep;


    public DropboxService(PostsRepository postRep, UsersRepository userRep) {
        this.postRep = postRep;
        this.userRep = userRep;
    }

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

    public User uploadProfilePicture(User user, InputStream fileData){

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

    protected DbxClientV2 getDbxClientV2() {
        DbxCredential cred = new DbxCredential(ACCESS_TOKEN, 14400L, REFRESH_TOKEN, APP_KEY, APP_SECRET);
        DbxRequestConfig config = DbxRequestConfig.newBuilder("Triis").build();
        DbxClientV2 client = new DbxClientV2(config, cred);
        return client;
    }

    protected void refreshToken(DbxClientV2 client) {
        try {
            ACCESS_TOKEN = client.refreshAccessToken().getAccessToken();
            System.out.println("New access token: " + ACCESS_TOKEN);
        } catch (Exception refreshException) {
            System.out.println("Failed to refresh token: " + refreshException.getMessage());
        }
    }


}
