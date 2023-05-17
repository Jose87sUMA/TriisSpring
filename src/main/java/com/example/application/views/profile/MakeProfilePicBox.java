package com.example.application.views.profile;

import com.example.application.data.entities.User;
import com.example.application.data.exceptions.MakePostException;
import com.example.application.data.services.MakePostService;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.views.feed.FeedScroller;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
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


import java.io.InputStream;

import static java.lang.Math.min;

public class MakeProfilePicBox extends Dialog {


    private final PostService postService;
    private final User authenticatedUser;
    private final UserService userService;
    private final MakePostService makePostService;
    /**
     * next attribute is used to add the post uploaded directly to profile panel
     */
    private final FeedScroller profilePanel;
    boolean pointedPost;
    private InputStream fileData;
    private boolean uploadByLink = false;
    /**
     *  components that appear and disappear from the window
     */
    private Upload uploadComponent;
    private TextField linkField  = new TextField("Link");
    private Button fileButton = new Button("File");
    private Button linkButton = new Button("Link");


    /**
     * constructor initializes a window for making posts
     * @param postService
     * @param userService
     * @param profilePanel
     */
    public MakeProfilePicBox(PostService postService, UserService userService, MakePostService makePostService, FeedScroller profilePanel) {
        this.authenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        this.postService = postService;
        this.userService = userService;
        this.profilePanel = profilePanel;
        this.pointedPost = false;
        this.fileData = null;
        this.makePostService = makePostService;
        createUploadPictureLayout();

    }

    public MakeProfilePicBox(PostService postService, User authenticatedUser, UserService userService, MakePostService makePostService, FeedScroller profilePanel) {
        this.postService = postService;
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.makePostService = makePostService;
        this.profilePanel = profilePanel;
    }

    /**adds and styles the component of make post window,
     * adds closing functionality
     * calls auxiliar functions to create components and model behaviour
     * */
    private void createUploadPictureLayout(){
        this.setDraggable(true);

        H4 pointsQuestionText = new H4(" Change your profile picture");
        pointsQuestionText.getStyle().set("marginTop", "20px");
        pointsQuestionText.getStyle().set("marginBottom", "30px");

        createUploadComponent();

        H1 headerH1 = new H1("Change the profile picture");
        headerH1.getStyle().set("marginBottom", "25px");

        linkField.setWidth("100%");
        linkField.setVisible(false);
        this.add(headerH1,uploadComponent, linkField);

        Button cancelButton = new Button("Cancel", (e) -> this.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button postButton = new Button("Change", (e) -> setImage(String.valueOf(linkField)));
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
     * SAVES PICTURE UPLOADED BY USER IN DATABASE
     * calls the appropriate methods depending on the type of post (pointed or not) and if it is done by link or file
     */


    public void setImage(String url) {
//        imageResource = null;
        Avatar newAvatar = new Avatar();

        if (url == null) {
            newAvatar.getElement().removeAttribute("img");
        } else {
            newAvatar.getElement().setAttribute("img", url);
        }

    }

    private void post(){
        boolean success = false;
        Notification notification = new Notification();
        notification.setDuration(4000);
        notification.removeThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.removeThemeVariants(NotificationVariant.LUMO_SUCCESS);
        try{
            if(uploadByLink && pointedPost){
                makePostService.postPointedByLink(authenticatedUser, linkField.getValue());
            }else if(uploadByLink && !pointedPost){
                makePostService.postNotPointedByLink(authenticatedUser, linkField.getValue());
            }else if(pointedPost){
                makePostService.postPointedByFile(authenticatedUser, this.fileData);

            }else{
                makePostService.postNotPointedByFile(authenticatedUser, this.fileData);
            }
            success = true;
            this.close();
            profilePanel.refresh();
            this.close();
        }catch(MakePostException e){
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setText(e.getMessage());
            notification.open();
        }catch(Exception e){
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            // System.out.println(e.getMessage());
            notification.setText("Internal error while working with file");
            notification.open();
            this.close();
        }

        if(success){
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setText(pointedPost?"Pointed post uploaded correctly": "Not pointed post uploaded correctly");
            notification.open();
            this.close();
        }


    }

    /**
     * create buttons to manage whether a post is pointed or not
     * @return Horizontal Layout with two buttons
     */
//    private HorizontalLayout createPointedButtons(){
//        Button pointed = new Button("Pointed");
//        Button notPointed = new Button("Not Pointed");
//        pointed.setWidth("50%");
//        notPointed.setWidth("50%");
//        notPointed.getStyle().set("background-color","#0C6CE9");
//
//
//        HorizontalLayout pointedButtons = new HorizontalLayout(pointed, notPointed);
//        pointedButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
//
//        pointed.addClickListener(event -> {
//            pointed.getStyle().set("background-color","#0C6CE9");
//            notPointed.getStyle().set("background-color","#2D3D52");
//            pointedPost = true;
//        });
//
//        notPointed.addClickListener(event -> {
//            notPointed.getStyle().set("background-color","#0C6CE9");
//            pointed.getStyle().set("background-color","#2D3D52");
//            pointedPost = false;
//        });
//        return pointedButtons;
//    }

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

            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        uploadComponent.addSucceededListener(event -> {

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
