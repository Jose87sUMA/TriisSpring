package com.example.application.views.profile;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import static java.lang.Math.min;

public class MakePostBox extends Dialog {


    private final PostService postService;
    private final User authenticatedUser;
    private final UserService userService;
    /**
     * next attribute is used to add the post uploaded directly to profile panel
     */
    private final ProfilePanel profilePanel;
    boolean pointedPost;
    boolean notPointedPost;
    private InputStream fileData;
    private boolean validFile = false;
    /**
     *  components that appear and disappear from the window
     */
    private Upload uploadComponent;
    private TextField linkField  = new TextField("Link");
    private Button fileButton = new Button("File");
    private Button linkButton = new Button("Link");


    public MakePostBox(PostService postService, UserService userService, ProfilePanel profilePanel) {
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
    }


    /**
     * returns whether there is a valid file/link loaded at the moment of posting
     * @return boolean
     */
    private boolean validatePostContent(){
        return true;
    }
    private void post(){

        Notification notification = new Notification();
        notification.setDuration(2000);
        boolean enoughPoints = false;

        //first we check the validity of the file
        if(notPointedPost || enoughPoints){

            if(!validFile){
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText("No file appended");
                notification.open();

            }else{
                try {
                    if(fileData.available() == 0){
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        notification.setText("File is empty");
                        notification.open();
                        validFile = false;
                        this.close();
                        this.open();
                    }else{
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        notification.setText("Nice picture:D");
                        notification.open();

                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }


        }else{ //nothing is done
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setText("Sorry, not enough points");
            notification.open();
        }

        //now we manage the posting
        if(validFile && notPointedPost){
            try{
                Post post = new Post(authenticatedUser,false, fileData);
                postService.save(post);
                profilePanel.getContent().addComponentAsFirst(new PostPanel(post, userService, postService));
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
                Post post = new Post(authenticatedUser,true, fileData);
                postService.save(post);
                profilePanel.getContent().addComponentAsFirst(new PostPanel(post, userService, postService));
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
                throw new RuntimeException(e);
            }

            uploadComponent.clearFileList();
            uploadComponent.setDropLabel(new Label("Picture uploaded correctly"));
            uploadComponent.getDropLabelIcon().removeFromParent();
            uploadComponent.getUploadButton().removeFromParent();
        });

    }
}
