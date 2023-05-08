package com.example.application.views.profile;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.InputStream;

@PageTitle("Triis - Profile")
@Route(value = "profile", layout = MainLayout.class)
@RouteAlias(value = "profile", layout = MainLayout.class)
@PermitAll
public class ProfileView extends VerticalLayout implements HasUrlParameter<String> {

    private ProfilePanel profilePanel;
    private User user;

    private final UserService userService;
    private final PostService postService;


    public ProfileView(UserService userService, PostService postService) {

        this.postService = postService;
        this.userService = userService;

    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

        if(parameter == null)
            user = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        else if((user = userService.findByUsername(parameter)) == null){
            event.forwardTo("feed");
            return;
        }

        profilePanel = new ProfilePanel(user, userService, postService);

        this.setJustifyContentMode(JustifyContentMode.CENTER);
        this.setMargin(true);
        this.setHorizontalComponentAlignment(Alignment.CENTER, profilePanel);

        HorizontalLayout buttons = createButtonsLayout();

        this.setHorizontalComponentAlignment(Alignment.CENTER, buttons);
        this.setAlignItems(Alignment.CENTER);

        add(new H1(user.getUsername()), buttons, profilePanel);

    }


    /**
     * CUSTOMIZING  BUTTONS
     */
    private HorizontalLayout createButtonsLayout() {
        //buttons
        Button following = new Button("Following: " + userService.getFollowing(user).size());
        following.addClickListener(event -> UI.getCurrent().navigate("profile/?following/" + user.getUsername()));

        Button follow = new Button("Followers: " + userService.getFollowers(user).size());
        Button type1 = new Button("Type 1 points: " + user.getType1Points());
        Button type2 = new Button("Type 2 points: " + user.getType2Points());
        Button makePost = new Button("Make a Post");

        Button editProfile = new Button("Edit Profile");
        editProfile.addClickListener(event -> UI.getCurrent().navigate("profile/?edit/" + user.getUsername()));

        if(!user.equals(userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()))) {
            makePost.setVisible(false);
            editProfile.setVisible(false);
        }else{
            makePost.addClickListener(e -> new MakePostBox(postService)) ;
        }
        return new HorizontalLayout(follow, following, type1, type2, makePost, editProfile);

    }





}
