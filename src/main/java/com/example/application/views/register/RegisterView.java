package com.example.application.views.register;

import com.example.application.services.UserService;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@AnonymousAllowed
@PageTitle("Registration")
@Route(value = "register")
@RouteAlias(value = "register")
public class RegisterView extends VerticalLayout {

    /**
     * @param userService
     */
    public RegisterView(UserService userService) {

        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.addClassName(LumoUtility.AlignItems.CENTER);
        this.setSpacing(false);
        this.setSizeFull();

        this.add(new RegisterForm(userService));
        
    }

}