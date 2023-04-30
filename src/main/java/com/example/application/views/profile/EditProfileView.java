package com.example.application.views.profile;

import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

@PageTitle("Triis - Profile")
@Route(value = "profile/?edit")
@RouteAlias(value = "profile/edit")
@PermitAll
public class EditProfileView extends VerticalLayout implements HasUrlParameter<String> {

    private EditProfilePanel editProfilePanel;
    private User user;

    private final UserService userService;
    private final PostService postService;


    public EditProfileView(UserService userService, PostService postService) {

        this.postService = postService;
        this.userService = userService;

    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

        if(!parameter.equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            event.forwardTo("feed");
        }

        user = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        editProfilePanel = new EditProfilePanel(user, userService, postService);

        this.setJustifyContentMode(JustifyContentMode.CENTER);
        this.setMargin(true);
        this.setAlignItems(Alignment.CENTER);

        add(new H1("Edit Profile"), editProfilePanel);

    }
}
