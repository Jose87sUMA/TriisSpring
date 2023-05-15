package com.example.application.views.profile;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.views.feed.FeedScroller;
import com.example.application.views.feed.PostPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.springframework.security.core.context.SecurityContextHolder;


import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import static java.lang.Math.min;

public class MakePostBox extends Dialog {


    private final PostService postService;
    private final User authenticatedUser;
    private final UserService userService;
    /**
     * next attribute is used to add the post uploaded directly to profile panel
     */
    private final FeedScroller profilePanel;
    boolean pointedPost;
    boolean notPointedPost;
    private InputStream fileData;
    private boolean validFile = false;
    private boolean uploadByLink = false;
    /**
     *  components that appear and disappear from the window
     */
    private Upload uploadComponent;
    private TextField linkField  = new TextField("Link");
    private Button fileButton = new Button("File");
    private Button linkButton = new Button("Link");



    public MakePostBox(PostService postService, UserService userService, FeedScroller profilePanel) {
        this.authenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        this.postService = postService;
        this.userService = userService;
        this.profilePanel = profilePanel;
        this.pointedPost = false;
        this.notPointedPost = true;
        this.fileData = null;
        createUploadPictureLayout();

    }

    /**ADDS AND STYLES THE COMPONENT OF MAKE POST WINDOW,
     * ADDS CLOSING FUNCTIONALITY
     * CALLS AUXILIAR FUNCTIONS TO CREATE COMPONENTS AND MODEL BEHAVIOUR
     * */
    private void createUploadPictureLayout(){
        this.setDraggable(true);

        H4 pointsQuestionText = new H4(" How do you want your post?");
        pointsQuestionText.getStyle().set("marginTop", "20px");
        pointsQuestionText.getStyle().set("marginBottom", "10px");

        createUploadComponent();

        linkField.setWidth("100%");
        linkField.setVisible(false);
        this.add(new H1("Create Post"),uploadComponent, linkField, pointsQuestionText, createPointedButtons());

        Button cancelButton = new Button("Cancel", (e) -> this.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button postButton = new Button("Post", (e) -> post());
        postButton.getStyle().set("background-color","#0C6CE9");

        fileButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        fileButton.getStyle().set("marginRight", "200px");
        fileButton.setVisible(false);
        fileButton.addClickListener((e)-> fileButtonEvent());

        linkButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        linkButton.getStyle().set("marginRight", "200px");
        linkButton.addClickListener((e)-> linkButtonEvent());

        this.getFooter().add(fileButton, linkButton, cancelButton, postButton);
        
    }

    /**
     * auxiliar function
     * models behaviour of link button
     * user can see a link field on the screen now
     */
    private void linkButtonEvent(){
        uploadComponent.setVisible(false);
        linkField.setVisible(true);
        linkButton.setVisible(false);
        fileButton.setVisible(true);
        uploadByLink = true;
    }

    /**
     * auxiliar function
     * models behaviour of filebutton
     * user can see an upload picture componentn ow
     */
    private void fileButtonEvent(){
        uploadComponent.setVisible(true);
        linkField.setVisible(false);
        linkButton.setVisible(true);
        fileButton.setVisible(false);
        uploadByLink = false;
    }

    /**
     * USES POST CONSTRUCTOR TO SAVE PICTURE UPLOADED BY USER IN DATABASE
     * if user can make a post: a non-pointed post or enough points for a pointed one: check the validity of the file first
     * pictures can be uploaded by means of files or a link to a picture
     * the input stream is taken in both cases
     * there is a function to take the input stream from an url
     * when files are uploaded, upload component is used
     * error checking is done with notifications for the user
     * validFile means we can try to put it in the database
     */

    private void post(){
        Notification notification = new Notification();
        notification.setDuration(2000);
        boolean enoughPoints = false;

        //if we can make the post we check the validity of the file
        if(notPointedPost || enoughPoints){
            if(uploadByLink){
                manageImageURL();
            }else{
                validatePostContentFromFile();
            }


        }else{ //if not enough points and pointed post nothing is done
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setText("Sorry, not enough points");
            notification.open();
        }

        //now we manage the posting
        if(validFile && notPointedPost){
            try{
                Post post = postService.creatPost(authenticatedUser, false, fileData);
                profilePanel.refresh();
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Not-pointed post saved correctly");
                notification.open();
                this.close();
            }catch(Exception exception){
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText("Server error: not available at the moment :(");
                notification.open();
                this.close();
            }


        }else if(validFile && enoughPoints){
            try{
                Post post = postService.creatPost(authenticatedUser, true, fileData);
                profilePanel.refresh();
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Pointed post saved correctly");
                notification.open();
                this.close();
            }catch (Exception exception){
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText("Server error: not available at the moment");
                notification.open();
            }



            //resta los points y eso
        }

    }


    /**
     * sets validFile and shows notifications appropriately
     * depending on whether there is a valid file loaded at the moment of posting
     * checks if file is empty
     */
    private void validatePostContentFromFile(){
        Notification notification = new Notification();
        notification.setDuration(4000);
        if(!validFile) {
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setText("No file");
            notification.open();
        }else{
            try {
                if(fileData == null || fileData.available() == 0){
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setText("File is empty or not valid");
                    notification.open();
                    validFile = false;
                    this.close();
                    this.open();
                }else{
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    notification.setText("Nice picture :D");
                    validFile = true;
                    notification.open();

                }
            } catch (IOException ex) {
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText("Error while dealing with file");
                validFile = false;
                notification.open();
            }
        }

    }


    /**
     * sets validFile and shows notifications appropriately
     * depending on whether the right format of image can be extracted from the url at the moment of posting
     * checks if file is empty
     */
    private void validatePostContentFromLink(){
        Notification notification = new Notification();
        notification.setDuration(4000);

        try {
            if(fileData == null || fileData.available() == 0){
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText("Not the right format, try jpeg, jpg or pdf");
                validFile = false;
                notification.open();
            }else{
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Nice picture:D");
                validFile = true;
                notification.open();
            }
        } catch (IOException e) {
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setText("Error while dealing with file");
            validFile = false;
            notification.open();
        }

    }

    /**
     * Creates an input stream from image link
     */
    private void manageImageURL(){
        URL imageUrl = null;
        Notification notification = new Notification();
        notification.setDuration(4000);
        try {
            imageUrl = new URL(linkField.getValue());
            URLConnection connection = imageUrl.openConnection();
            String contentType = connection.getHeaderField("Content-Type");
            if (contentType.equals("image/jpeg") || contentType.equals("image/jpg") || contentType.equals("image/png")) {
                fileData = connection.getInputStream();

            }
            validatePostContentFromLink();

        } catch (IOException e) {
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setText("Could not upload a picture with provided link");
            validFile = false;
            notification.open();
        }
    }


    /**
     * CREATE BUTTONS TO MANAGE WHETHER A POST IS POINTED OR NOT
     * @return Horizontal Layout with two buttons
     */
    private HorizontalLayout createPointedButtons(){
        Button pointed = new Button("Pointed");
        Button notPointed = new Button("Not Pointed");
        pointed.setWidth("50%");
        notPointed.setWidth("50%");
        notPointed.getStyle().set("background-color","#0C6CE9");


        HorizontalLayout pointedButtons = new HorizontalLayout(pointed, notPointed);
        pointedButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        pointed.addClickListener(event -> {
            pointed.getStyle().set("background-color","#0C6CE9");
            notPointed.getStyle().set("background-color","#2D3D52");
            pointedPost = true;
            notPointedPost = false;
        });

        notPointed.addClickListener(event -> {
            notPointed.getStyle().set("background-color","#0C6CE9");
            pointed.getStyle().set("background-color","#2D3D52");
            pointedPost = false;
            notPointedPost = true;
        });
        return pointedButtons;
    }

    /**
     * upload component which notifies in case of an error or saves image as an attribute of the class (fileData)
     * @return upload component for images
     */
    private void createUploadComponent(){
        MemoryBuffer uploadBuffer= new MemoryBuffer();
        this.uploadComponent = new Upload(uploadBuffer);
        uploadComponent.setAcceptedFileTypes("image/png, image/jpeg, image/jpg");
        uploadComponent.setDropLabel(new Label("Drop picture here"));

        uploadComponent.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(
                    errorMessage,
                    5000,
                    Notification.Position.MIDDLE
            );
            validFile = false;
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        uploadComponent.addSucceededListener(event -> {
            validFile = true;
            this.fileData = uploadBuffer.getInputStream();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {

            }
            linkButton.setEnabled(false);
            uploadComponent.clearFileList();
            uploadComponent.setDropLabel(new Label("Picture uploaded correctly"));
            uploadComponent.getDropLabelIcon().removeFromParent();
            uploadComponent.getUploadButton().removeFromParent();
        });

    }
}
