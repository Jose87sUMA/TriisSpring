package com.example.application.views.profile;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.InputStream;
import java.math.BigInteger;

import static java.lang.Math.min;

public class MakePostBox extends Dialog {


    private final PostService postService;
    private final User authenticatedUser;
    boolean pointedPost;
    boolean notPointedPost;

    public MakePostBox(PostService postService, UserService userService) {
        this.authenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        this.postService = postService;
        this.pointedPost = false;
        this.notPointedPost = true;
        createUploadPictureLayout().open();
    }

    private Dialog createUploadPictureLayout(){
        Dialog makePostWindow = new Dialog();

        //add an empty line
        Div emptyLine = new Div();
        emptyLine.setHeight("1em");

        Notification notification = new Notification();
        notification.setDuration(2000);
        Button postButton = new Button("Post");

        postButton.addClickListener(e -> {
            boolean enoughPoints = false;
            if(notPointedPost){
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.removeThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText("Not-pointed post saved correctly");
                notification.open();
                makePostWindow.close();
                //añades non pointed post
            }else if(enoughPoints){
                notification.removeThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Pointed post saved correctly");
                notification.open();
                makePostWindow.close();

                //resta los points y eso
            }else{ //nothing is done
                notification.removeThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText("Sorry, not enough points");
                notification.open();

            }
        });

        makePostWindow.add(new H1("Create Post"),createUploadComponent(), emptyLine, new H4(" How do you want your post?"), createPointedButtons());

        Button cancelButton = new Button("Cancel", (e) -> makePostWindow.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        makePostWindow.getFooter().add(cancelButton, postButton);


        return makePostWindow;
    }

    private HorizontalLayout createPointedButtons(){
        Button pointed = new Button("Pointed");
        Button notPointed = new Button("Not Pointed");
        pointed.setWidth("50%");
        pointed.getStyle().set("background-color", "#00001a");
        pointed.getStyle().set("color", "#e6e6ff");
        notPointed.setWidth("50%");
        notPointed.getStyle().set("background-color","#0C6CE9");
        notPointed.getStyle().set("color", "#00001a" );

        HorizontalLayout pointedButtons = new HorizontalLayout(pointed, notPointed);

        pointed.addClickListener(event -> {
            pointed.getStyle().set("background-color","#0C6CE9");
            notPointed.getStyle().set("background-color","#e6e6ff");
            pointedPost = true;
            notPointedPost = false;

        });

        notPointed.addClickListener(event -> {
            notPointed.getStyle().set("background-color","#0C6CE9");
            pointed.getStyle().set("background-color","#00001a");
            pointedPost = false;
            notPointedPost = true;


        });


        return pointedButtons;
    }

    //auxiliar function to manage only the uploading of images
    private Upload createUploadComponent(){
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/png, image/jpeg");
        upload.setDropLabel(new Label("Drop picture here"));


        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(
                    errorMessage,
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        upload.addSucceededListener(event -> {
            // Get information about the uploaded file
            InputStream fileData = buffer.getInputStream();
            String fileName = event.getFileName();
            long contentLength = event.getContentLength();
            String mimeType = event.getMIMEType();
            // Do something with the file data
            // processFile(fileData, fileName, contentLength, mimeType);

            postService.save(new Post());
        });



        return upload;
    }
}
