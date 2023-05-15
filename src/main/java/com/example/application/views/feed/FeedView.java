package com.example.application.views.feed;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@PageTitle("Triis - Feed")
@Route(value = "feed", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll

public class FeedView extends HorizontalLayout  {

    private final UserService userServ;
    private final PostService postServ;

    private User user;
    TextField textFilter = new TextField();
    FeedPanel feedPanel ;

    private VerticalLayout content = new VerticalLayout();
    public FeedView(UserService userService, PostService postService) {

        userServ = userService;
        postServ = postService;
        User authenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        user = authenticatedUser;
        feedPanel = new FeedPanel(authenticatedUser, userService, postService);


        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.setMargin(true);

        this.setVerticalComponentAlignment(Alignment.CENTER, feedPanel);

        add(getSearchBar());
        add(feedPanel);
        updateList();
    }
    private void updateList() {

       List<Post> p = postServ.findAllPost(textFilter.getValue(),user);

        FeedPanel feedUpdated = new FeedPanel(p, userServ, postServ);

        this.feedPanel.setContent(feedUpdated);


    }

    private Component getSearchBar() {

        textFilter.setPlaceholder("Search User...");
        textFilter.setClearButtonVisible(true);
        textFilter.setValueChangeMode(ValueChangeMode.LAZY);
        textFilter.addValueChangeListener(e -> updateList());


        HorizontalLayout toolBar = new HorizontalLayout(textFilter);
        toolBar.addClassName("toolBar");
        return toolBar;

    }


}
