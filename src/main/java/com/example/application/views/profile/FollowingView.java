package com.example.application.views.profile;

import com.example.application.data.entities.User;
import com.example.application.data.services.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@PermitAll
@Route("profile/*following")
public class FollowingView extends Div  implements HasUrlParameter<String> {
    private final UserService userService;
    private User user;

    //to filter

    public FollowingView(UserService userService ) {
        this.userService = userService;


    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        if(parameter == null)
            this.user = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        else
            user = userService.findByUsername(parameter);

        // tag::snippet1[]
        Grid<User> grid = new Grid<>(User.class, false);
        grid.addColumn(User::getUsername).setHeader("Username");
        grid.addComponentColumn(user -> createStatusIcon(user.getVerified()))
                .setTooltipGenerator(User -> User.getVerified())
                .setHeader("Verified");


        //on right click, actions appear
        UserContextMenu contextMenu = new UserContextMenu(grid);
        // end::snippet1[]

        Button back = new Button("Go back");
        back.addClickListener(e -> UI.getCurrent().navigate("profile/" +user.getUsername()));

        add(back, grid);
        List<User> users = userService.getFollowing(user);
        grid.setItems(users);
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
     *
     * @param status
     * @return Icon   green if verified, red if not
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
