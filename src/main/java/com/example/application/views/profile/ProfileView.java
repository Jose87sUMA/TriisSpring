package com.example.application.views.profile;

import ch.qos.logback.core.Layout;
import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

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
        else
            user = userService.findByUsername(parameter);


        profilePanel = new ProfilePanel(user, userService, postService);

        this.setJustifyContentMode(JustifyContentMode.CENTER);
        this.setAlignItems(Alignment.CENTER);
        this.setMargin(true);
        this.setPadding(true);

        this.setHorizontalComponentAlignment(Alignment.CENTER, profilePanel);

        VerticalLayout buttons = createButtonsLayout();
        buttons.setAlignItems(Alignment.CENTER);
        this.setHorizontalComponentAlignment(Alignment.CENTER, buttons);

        add(new H1(user.getUsername()), buttons, profilePanel);

    }


    /**
     * CUSTOMIZING  BUTTONS
     */
    private VerticalLayout createButtonsLayout() {

        //follow.addClickListener(event -> UI.getCurrent().navigate(LoginView.class));
        //buttons

        Button following = new Button("Following: " + userService.getFollowing(user).size());
        Button follow = new Button("Followers: " + userService.getFollowers(user).size());
        Button type1 = new Button("Type 1 points: " + user.getType1Points());
        Button type2 = new Button("Type 2 points: " + user.getType2Points());
        Button makePost = new Button("Make a Post");
        Button editProfile = new Button("Edit Profile");

        //making buttons more accesible
        following.addClickShortcut(Key.ENTER);
        follow.addClickShortcut(Key.ESCAPE);

        if(!user.equals(userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()))) {
            makePost.setVisible(false);
            editProfile.setVisible(false);
        }
        return new VerticalLayout(new HorizontalLayout(follow, following), new HorizontalLayout(type1, type2), new HorizontalLayout(makePost, editProfile));

    }
}
