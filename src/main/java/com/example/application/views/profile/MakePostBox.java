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
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.springframework.security.core.context.SecurityContextHolder;

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
    Dialog makePostWindow;


    public MakePostBox(PostService postService, UserService userService, ProfilePanel profilePanel) {
        this.authenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        this.postService = postService;
        this.userService = userService;
        this.profilePanel = profilePanel;
        this.pointedPost = false;
        this.notPointedPost = true;
        this.fileData = null;
        this.makePostWindow = new Dialog();
        createUploadPictureLayout();
        makePostWindow.open();

    }

    private Dialog createUploadPictureLayout(){
        makePostWindow.setDraggable(true);

        H4 pointsQuestionText = new H4(" How do you want your post?");
        pointsQuestionText.getStyle().set("marginTop", "20px");
        pointsQuestionText.getStyle().set("marginBottom", "10px");

        makePostWindow.add(new H1("Create Post"),createUploadComponent(), pointsQuestionText, createPointedButtons());

        Button cancelButton = new Button("Cancel", (e) -> makePostWindow.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button postButton = new Button("Post", (e) -> post());
        postButton.getStyle().set("background-color","#0C6CE9");
        Button linkButton = new Button("Link");
        linkButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        linkButton.getStyle().set("marginRight", "200px");
        makePostWindow.getFooter().add(linkButton, cancelButton, postButton);


        return makePostWindow;
    }

    void post(){

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
                makePostWindow.close();
            }catch(Exception exception){
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText("Server error: not available at the moment :(");
                notification.open();
                makePostWindow.close();
            }


        }else if(validFile && enoughPoints){
            try{
                Post post = new Post(authenticatedUser,true, fileData);
                postService.save(post);
                profilePanel.getContent().addComponentAsFirst(new PostPanel(post, userService, postService));
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Pointed post saved correctly");
                notification.open();
                makePostWindow.close();
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
    private Upload createUploadComponent(){
        MemoryBuffer uploadBuffer= new MemoryBuffer();
        Upload upload = new Upload(uploadBuffer);
        upload.setAcceptedFileTypes("image/png, image/jpeg, image/jpg");
        upload.setDropLabel(new Label("Drop picture here"));

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(
                    errorMessage,
                    5000,
                    Notification.Position.MIDDLE
            );
            validFile = false;
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        upload.addSucceededListener(event -> {
            validFile = true;
            this.fileData = uploadBuffer.getInputStream();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            upload.clearFileList();
            upload.setDropLabel(new Label("Picture uploaded correctly"));
            upload.getDropLabelIcon().removeFromParent();
            upload.getUploadButton().removeFromParent();
        });

        return upload;
    }
}
