package com.example.application.views.feed;

import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

@PageTitle("Triis - Feed")
@Route(value = "feed", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class FeedView extends HorizontalLayout {

    public FeedView(UserService userService, PostService postService) {

        User authenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());


        FeedPanel feedPanel = new FeedPanel(authenticatedUser, userService, postService);

        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.setMargin(true);
        this.setVerticalComponentAlignment(Alignment.CENTER, feedPanel);

        add(feedPanel);
    }

}
