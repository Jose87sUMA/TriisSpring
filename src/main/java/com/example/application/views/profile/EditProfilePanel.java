package com.example.application.views.profile;

import com.example.application.data.entities.User;
import com.example.application.data.services.MakePostService;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.views.feed.FeedScroller;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EditProfilePanel extends VerticalLayout {

    private final UserService userService;
    public EditProfilePanel(User user, UserService userService, PostService postService) {

        this.userService = userService;
        Binder<User> binder = new BeanValidationBinder<>(User.class);
        //field creation

        PasswordField currentPassword = new PasswordField("Current password");
        PasswordField newPassword = new PasswordField("New password");
        PasswordField confirmPassword = new PasswordField("Confirm new password");
        Button editPassword = new Button("Edit password");
        editPassword.addClickListener(e -> {

            Notification notification = new Notification();
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

            if ((new BCryptPasswordEncoder()).encode(currentPassword.getValue()).equals(user.getPassword())) {
                notification.setText("Current password not correct");
                notification.open();
            }else if(newPassword.getValue().trim().isEmpty()){
                notification.setText("Enter a password");
                notification.open();
            }else if(newPassword.getValue().length() < 8){
                notification.setText("Password must contain at least 8 characters");
                notification.open();
            }else if(confirmPassword.getValue().trim().isEmpty()){
                notification.setText("Confirm password");
                notification.open();
            }else if(!newPassword.getValue().equals(confirmPassword.getValue())){
                notification.setText("Passwords don't match");
                notification.open();
            }else{
                userService.editPassword(user, newPassword.getValue());
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Passwords changed");
                notification.open();
            }
        });

        HorizontalLayout passwordLayout1 = new HorizontalLayout();
        passwordLayout1.addAndExpand(newPassword, confirmPassword);
        passwordLayout1.setAlignItems(Alignment.BASELINE);

        HorizontalLayout passwordLayout2 = new HorizontalLayout();
        passwordLayout2.addAndExpand(currentPassword, editPassword);
        passwordLayout2.setAlignItems(Alignment.BASELINE);

        Button goBack = new Button("Go back");
        goBack.setWidth("200px");
        goBack.addClickListener(e ->
                goBack.getUI().ifPresent(ui ->
                        ui.navigate("profile/" + user.getUsername()))
        );
        HorizontalLayout newLayout = new HorizontalLayout();

        Button changeProfPic = new Button("Change Profile Picture");

        /*
        * does not update the photo
        * */
        if(!user.equals(userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()))) {
            changeProfPic.setVisible(false);
        }else{
            FeedScroller profilePanel = null;
            changeProfPic.addClickListener(e -> new ChangeProfilePicBox(postService, userService, new MakePostService(), profilePanel).open()) ;
        }



        newLayout.addAndExpand(changeProfPic,goBack);
        newLayout.setAlignItems(Alignment.BASELINE);
        this.setMaxWidth("500px");
        this.setAlignItems(Alignment.CENTER);
        this.add(
                passwordLayout1,
                passwordLayout2,
                newLayout
//                goBack,
//                changeProfPic
        );

    }
}
