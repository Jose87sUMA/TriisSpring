package com.example.application.views.login;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;

public class TriisLoginForm extends LoginForm {

    public TriisLoginForm(LoginView loginView) {

        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), loginView.getClass()));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Triis");
        i18n.getHeader().setDescription("Login using niks/123password");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);
        setForgotPasswordButtonVisible(true);
    }


}
