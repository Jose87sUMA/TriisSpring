package com.example.application.views.profile;

import com.example.application.data.entities.User;
import com.example.application.exceptions.UserException;
import com.example.application.services.MakePostService;
import com.example.application.services.PostService;
import com.example.application.services.UserService;
import com.example.application.views.feed.FeedScroller;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.springframework.security.core.context.SecurityContextHolder;


import static java.lang.Math.min;

/**
 * Dialog to change profile picture. Functions as MakePosBox.
 */
public class ChangeProfilePicBox extends MakePostBox {


    private final PostService postService;
    private final User authenticatedUser;
    private final UserService userService;
    private final MakePostService makePostService;
    private final FeedScroller profilePanel;


    /**
     * Constructor initializes a window for changing the user's profile picture
     * @param postService
     * @param userService
     * @param profilePanel
     */
    public ChangeProfilePicBox(PostService postService, UserService userService, MakePostService makePostService, FeedScroller profilePanel) {
        super(postService, userService, makePostService, profilePanel);
        this.authenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        this.postService = postService;
        this.userService = userService;
        this.makePostService = makePostService;
        this.profilePanel = profilePanel;
    }

    /**
     * Creates the dialog with that makes it possible to change the profile picture.
     * @author José Alejandro Sarmiento
     */
    @Override
    public void createUploadPictureLayout(){
        this.setDraggable(true);

        this.uploadComponent = this.createUploadComponent();

        this.add(new H1("Upload Profile Picture"), uploadComponent);

        Button cancelButton = new Button("Cancel", (e) -> this.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button uploadButton = new Button("Upload Profile Picture", (e) -> uploadPicture());
        uploadButton.getStyle().set("background-color","#0C6CE9");

        this.getFooter().add(cancelButton, uploadButton);

    }

    /**
     * Uploads the fileData of this instance to the dropbox server as the user's new profile picture.
     * Notifies the user in case of success or failure.
     * @author José Alejandro Sarmiento
     */
    private void uploadPicture() {
        boolean success = false;
        Notification notification = new Notification();
        notification.setDuration(4000);
        notification.removeThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.removeThemeVariants(NotificationVariant.LUMO_SUCCESS);
        try{
            userService.changeProfilePicture(authenticatedUser, this.fileData);
            success = true;
            this.close();
            profilePanel.refresh();
            this.close();
        }catch(UserException e){
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
            notification.setText("Successfully Changed Profile Picture");
            notification.open();
            this.close();
        }
    }

}
