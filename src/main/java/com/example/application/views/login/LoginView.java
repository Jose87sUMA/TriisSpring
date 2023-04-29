package com.example.application.views.login;

import com.example.application.security.AuthenticatedUser;
import com.example.application.views.register.RegisterView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {

        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.addClassName(LumoUtility.AlignItems.CENTER);
        this.setSpacing(false);
        this.setSizeFull();
        this.authenticatedUser = authenticatedUser;

        this.add(new H3("Use niks 123password to enter"),new TriisLoginForm(this), new RouterLink("Register", RegisterView.class));

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            event.forwardTo("");
        }
    }
}
