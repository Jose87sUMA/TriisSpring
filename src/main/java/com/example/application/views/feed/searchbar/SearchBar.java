package com.example.application.views.feed.searchbar;

import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.Collections;
import java.util.List;

public class SearchBar extends Dialog {

    private final UserService userService;
    private final PostService postService;

    private TextField textFilter;
    private Grid<User> grid;

    public SearchBar(UserService userService, PostService postService){

        this.userService = userService;
        this.postService = postService;

        textFilter = new TextField();
        grid = new Grid<>(User.class, false);

        grid.addComponentColumn(us -> createButtonToProfile(us.getUsername())).setHeader("Profiles");
        grid.setWidth("300px");  // esto no hace nada
        grid.setHeight("300px");

        this.add(getSearchBar(), grid);
        this.setWidth("50x");  // esto no hace nada
        this.setHeight("500px");
        updateList();

    }

    private void updateList() {

        List<User> us = userService.findAllProfiles(textFilter.getValue());
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
        but.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        but.addClickListener(e ->
                but.getUI().ifPresent(ui ->
                        ui.navigate("profile/" + Username))
        );
        return but;
    }
}
