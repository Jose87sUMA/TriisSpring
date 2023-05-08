package com.example.application.views.profile;

import com.example.application.data.entities.Post;
import com.example.application.data.services.PostService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

import java.io.InputStream;

public class MakePostBox extends ConfirmDialog{

    private final PostService postService;
    boolean pointedPost;
    boolean notPointedPost;

    public MakePostBox(PostService postService) {
        this.postService = postService;
        this.pointedPost = false;
        this.notPointedPost = false;
        createUploadPictureLayout().open();
    }

    private ConfirmDialog createUploadPictureLayout(){
        ConfirmDialog makePostWindow = new ConfirmDialog();
        makePostWindow.setHeader("Create Post");
        makePostWindow.setCancelable(true);
        makePostWindow.setConfirmText("Post");

        //add an empty line
        Div emptyLine = new Div();
        emptyLine.setHeight("1em");



        makePostWindow.add(createUploadComponent(), emptyLine, new H4(" How do you want your post?"), createPointedButtons());

        return makePostWindow;
    }

    private HorizontalLayout createPointedButtons(){
        Button pointed = new Button("Pointed");
        Button notPointed = new Button("Not Pointed");
        pointed.setWidth("50%");
        pointed.getStyle().set("background-color", "#00001a");
        pointed.getStyle().set("color", "#e6e6ff");
        notPointed.setWidth("50%");
        notPointed.getStyle().set("background-color","#e6e6ff");
        notPointed.getStyle().set("color", "#00001a" );

        HorizontalLayout pointedButtons = new HorizontalLayout(pointed, notPointed);

        pointed.addClickListener(event -> {
            pointed.getStyle().set("background-color","#0C6CE9");
            notPointed.getStyle().set("background-color","#e6e6ff");
            if(pointedPost == true){
                pointed.getStyle().set("background-color","#00001a");
                pointedPost = false;
            }else{
                pointedPost = true;
                notPointedPost = false;
            }

        });

        notPointed.addClickListener(event -> {
            notPointed.getStyle().set("background-color","#0C6CE9");
            pointed.getStyle().set("background-color","#00001a");
            if(notPointedPost == true){
                notPointed.getStyle().set("background-color","#e6e6ff");
                notPointedPost = false;
            }else{
                notPointedPost = true;
                pointedPost = false;
            }


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
