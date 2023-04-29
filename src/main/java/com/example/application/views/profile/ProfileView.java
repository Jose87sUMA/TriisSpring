package com.example.application.views.profile;

import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
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

    //buttons
    Button following = new Button("Following");
    Button follow = new Button("Follow");
    Button type1 = new Button("Type 1 points");
    Button type2 = new Button("Type 2 points");
    Button makepost = new Button("Make a post");


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
        this.setMargin(true);
        this.setHorizontalComponentAlignment(Alignment.CENTER, profilePanel);

        HorizontalLayout buttons = createButtonsLayout();

        this.setHorizontalComponentAlignment(Alignment.CENTER, buttons);

        add(new H1(user.getUsername()), buttons, profilePanel);

    }


    /**
     * CUSTOMIZING  BUTTONS
     */
    private HorizontalLayout createButtonsLayout() {

        //follow.addClickListener(event -> UI.getCurrent().navigate(LoginView.class));

        //making buttons more accesible
        following.addClickShortcut(Key.ENTER);
        follow.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(follow, following, type1, type2, makepost);

    }
}
