package com.example.application.views.register;

import com.example.application.data.entities.User;
import com.example.application.services.UserService;
import com.example.application.views.login.LoginView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RegisterForm extends VerticalLayout {

    UserService userService;
    Binder<User> binder = new BeanValidationBinder<>(User.class);
    //field creation
    TextField username = new TextField("Username");
    EmailField email = new EmailField("Email");

    PasswordField password = new PasswordField("Password");
    PasswordField confirmPassword = new PasswordField("Confirm password");
    //buttons
    Button save = new Button("Save");
    Button close = new Button("Log in");
    private User user;

    public RegisterForm(UserService userService) {

        this.setSizeUndefined();

        this.userService = userService;
        //using binder to map fields to User class
        binder.bindInstanceFields(this);
        //placing the form on top of the buttons
        VerticalLayout wholeForm = new VerticalLayout(new H1("REGISTRATION"),createFormLayout(), createButtonsLayout());

        wholeForm.addClassName(LumoUtility.AlignItems.CENTER);
        this.addClassName(LumoUtility.AlignItems.CENTER);

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
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2));
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
        close.addClickListener(event -> UI.getCurrent().navigate(LoginView.class));

        //making buttons more accesible
        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(save, close);
    }

    /**
     * VERIFY FIELDS AND SAVE USER
     */
    private void register(){
        String username = this.username.getValue();
        String password1 = password.getValue();
        String password2 = confirmPassword.getValue();
        String emailValue = email.getValue();
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        notification.setDuration(2000);

        if(username.trim().isEmpty()){
            notification.setText("Enter a username");
            notification.open();
        }else if(!username.matches("^[a-z0-9]+$")){
            notification.setText("Username can only contain numbers and lowercase letters");
            notification.open();
        }else if(emailValue.trim().isEmpty()){
            notification.setText("Enter an email");
            notification.open();
        }else if(email.isInvalid()){
            notification.setText("Email invalid format");
            notification.open();
        }else if(password1 .trim().isEmpty()){
            notification.setText("Enter a password");
            notification.open();
        }else if(password1.length() < 8){
            notification.setText("Password must contain at least 8 characters");
            notification.open();
        }else if(password2 .trim().isEmpty()){
            notification.setText("Confirm password");
            notification.open();
        }else if(!password1.equals(password2)){
            notification.setText("Passwords don't match");
            notification.open();
        }else if(userService.findByUsername(username) != null){
            notification.setText("User already exists");
            notification.open();
        }else if(userService.findByEmail(emailValue) != null){
            notification.setText("Email already in use");
            notification.open();
        }else{
            //encrypting password
            PasswordEncoder pass = new BCryptPasswordEncoder();
            CharSequence passwordToEncrypt = password1;

            userService.save(new User(username, pass.encode(passwordToEncrypt), emailValue));
            notification.removeThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);    
            notification.setText("User saved");
            notification.open();

        }

    }



}