package com.example.application.views.profile;

import com.example.application.data.entities.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;

/**
 * Dialog containing the users that a certain user follows and
 * a search bar to look for some of them.
 */
public class FollowingDialog extends Dialog {

    private final UserService userService;
    private User user;
    private TextField filterText = new TextField();
    //to filter
    private Grid<User> grid = new Grid<>(User.class, false);

    //to filter

    /**
     * @param userService
     * @param user
     */
    public FollowingDialog(UserService userService, User user) {
        this.userService = userService;
        this.user = user;

        grid.addColumn(User::getUsername).setHeader("Username");
        grid.addComponentColumn(userFoll -> createStatusIcon(userFoll.getVerified()))
                .setTooltipGenerator(User -> User.getVerified())
                .setHeader("Verified");


        //on right click, actions appear
        // UserContextMenu contextMenu = new UserContextMenu(grid);
        // end::snippet1[]

        this.setWidth("450px");

        add(getSearchBar(), grid);

        updateList();
    }

    /**
     * Updates the users that appear on the dialog depending on the search bar.
     * @author Ksenia Myakisheva
     */
    private void updateList() {
        grid.setItems(userService.findAllFollowing(filterText.getValue(),user));
    }

    /**
     * Creates the search bar to look for users.
     * @return the search bar
     * @author Ksenia Myakisheva
     */
    private Component getSearchBar() {

        filterText.setPlaceholder("Search User...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        HorizontalLayout toolBar = new HorizontalLayout(filterText);
        toolBar.addClassName("toolBar");
        return toolBar;

    }

    private static class UserContextMenu extends GridContextMenu<User> {
        public UserContextMenu(Grid<User> target) {
            super(target);

            addItem("Unfollow", e -> e.getItem().ifPresent(User -> {
                //System.out.printf("Edit: %s%n", User.getUsername());
            }));
            addItem("Report", e -> e.getItem().ifPresent(User -> {
                // System.out.printf("Delete: %s%n", User.getUsername());
            }));

            //add(new Hr());

            setDynamicContentHandler(User -> {
                // Do not show context menu when header is clicked
                if (User == null)
                    return false;
                return true;
            });
        }
    }

    /**
     * Creates an icon that shows if a user is verified or not
     *
     * @param status
     * @return Icon   green if verified, red if not
     * @author Ksenia Myakisheva
     */
    private Icon createStatusIcon(String status) {
        boolean isAvailable = "Y".equals(status);
        Icon icon;
        if (isAvailable) {
            icon = VaadinIcon.CHECK.create();
            icon.getElement().getThemeList().add("badge success");
        } else {
            icon = VaadinIcon.CLOSE_SMALL.create();
            icon.getElement().getThemeList().add("badge error");
        }
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        return icon;
    }

}
