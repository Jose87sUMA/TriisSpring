package com.example.application.views.feed;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.views.MainLayout;
import com.example.application.views.profile.FollowingView;
import com.example.application.views.profile.MakePostBox;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
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

    Grid<User> grid = new Grid<>(User.class, false);

    private VerticalLayout content = new VerticalLayout();
    private VerticalLayout contentGrid = new VerticalLayout();

    public FeedView(UserService userService, PostService postService) {

        userServ = userService;
        postServ = postService;
        User authenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        user = authenticatedUser;
        feedPanel = new FeedPanel(authenticatedUser, userService, postService);


        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.setMargin(true);
        this.setVerticalComponentAlignment(Alignment.CENTER, feedPanel);

        grid.addComponentColumn(us -> createButtonToProfile(us.getUsername())).setHeader("Profiles");

        grid.setWidth("300px");  // esto no hace nada
        grid.setHeight("300px");

        contentGrid.add(getSearchBar(), grid);
        contentGrid.setWidth("50x");  // esto no hace nada
        contentGrid.setHeight("500px");
        add(contentGrid);
        //add(getSearchBar(),grid);
        updateList();
        add(feedPanel);


    }





    private void updateList() {

        List <User> us = userServ.findAllProfiles(textFilter.getValue(),user);
        if(us == null){
            grid.setItems(Collections.emptyList());
        } else {

            grid.setItems(us);

        }



    }


    private Component getSearchBar() {

        textFilter.setPlaceholder("Search Profile...");
        textFilter.setClearButtonVisible(true);
        textFilter.setValueChangeMode(ValueChangeMode.LAZY);
        textFilter.addValueChangeListener(e -> updateList());

        HorizontalLayout toolBar = new HorizontalLayout(textFilter);
        toolBar.addClassName("toolBar");
        return toolBar;

    }

    private Button createButtonToProfile(String Username) {
        Button but = new Button(Username);
        but.addClickListener(e ->
                but.getUI().ifPresent(ui ->
                        ui.navigate("profile/" + Username))
        );
        return but;
    }


}
