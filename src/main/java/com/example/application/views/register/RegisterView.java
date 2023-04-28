package com.example.application.views.register;

import com.example.application.data.entities.User;
import com.example.application.data.services.UserService;
import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.shared.Registration;

@AnonymousAllowed
@PageTitle("Registration")
@Route(value = "register")
public class RegisterView extends FormLayout {
    UserService userService;
    Binder<User> binder = new BeanValidationBinder<>(User.class);
    //field creation
    TextField username = new TextField("Username");
    EmailField email = new EmailField("Email");

    PasswordField password = new PasswordField("Password");
    PasswordField confirmPassword = new PasswordField("Confirm password");
    //buttons
    Button save = new Button("Save");
    Button close = new Button("Cancel");
    private User user;

    public RegisterView(UserService userService) {
        this.userService = userService;
        //using binder to map fields to User class
        binder.bindInstanceFields(this);
        //placing the form on top of the buttons
        VerticalLayout wholeForm = new VerticalLayout(createFormLayout(), createButtonsLayout());

        add(wholeForm);
    }

        public void setUser(User user){
            this.user = user;
            binder.readBean(user);
        }

    /**
     * CUSTOMIZING  FORM
     */
    private FormLayout createFormLayout(){
        FormLayout formLayout = new FormLayout();
        formLayout.add(username, email, password,
                confirmPassword);

        //customizing form
        formLayout.setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("500px", 2));
        // Stretch the username and email field over 2 columns
        formLayout.setColspan(username, 2);
        formLayout.setColspan(email , 2);

        return formLayout;
    }

    /**
     * CUSTOMIZING  BUTTONS
     */
    private HorizontalLayout createButtonsLayout() {
        //creating themes for the buttons
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> register());

        //making buttons more accesible
        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(save, close);
    }

    private void register(){
        String userName = username.getValue();
        String password1 = password.getValue();
        String password2 = confirmPassword.getValue();
        String emailValue = email.getValue();

        if(userName.trim().isEmpty()){
            Notification.show("Enter a username");
        }else if(password1 .trim().isEmpty()){
            Notification.show("Enter a password");
        }else if(password2 .trim().isEmpty()){
            Notification.show("Confirm password");
        }else if(!password1.equals(password2)){
            Notification.show("Password don't match");
        }else if(userService.findByUsername(userName) != null){
            Notification.show("User already exists");
        }else if(userService.findByEmail(emailValue) != null){
            Notification.show("Email already in use");
        }else{
            userService.save(new User(userName, password1, emailValue));
            Notification.show("User saved");
        }

    }








}