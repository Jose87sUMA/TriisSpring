package com.example.application.views.feed.postPanel;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.PostsPointLog;
import com.example.application.data.entities.Role;
import com.example.application.data.entities.User;
import com.example.application.services.PostService;
import com.example.application.services.UserService;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Represents the header of the post panel. It contains the user's name, their profile picture,
 * the number of points the post has generated, the date the post was created and the options button.
 */
public class PostHeader extends HorizontalLayout {

    private final PostService postService;
    private final UserService userService;
    private final User poster;
    private final User authenticatedUser;
    private final Post post;

    private final Avatar profileAvatar;
    private final Button profileName;

    /**
     *
     * @param width
     * @param postService
     * @param userService
     * @param poster
     * @param authenticatedUser
     * @param post
     */
    public PostHeader(String width, PostService postService, UserService userService, User poster, User authenticatedUser,  Post post) {

        this.postService = postService;
        this.userService = userService;
        this.poster = poster;
        this.authenticatedUser = authenticatedUser;
        this.post = post;

        this.setWidth(width);
        this.setHeight("35px");

        profileAvatar = new Avatar(poster.getUsername());
        profileAvatar.setImageResource(userService.getProfilePicImageResource(poster));

        profileName = new Button(poster.getUsername() + " - "
                                    + post.getPoints() + " - "
                                    + post.getPost_date().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        profileName.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        profileName.setWidth("270px");
        profileName.setHeight("30px");
        profileName.addClickListener(e ->
                profileName.getUI().ifPresent(ui ->
                        ui.navigate("profile/" + poster.getUsername()))
        );

        this.addClassName(LumoUtility.Border.BOTTOM);
        this.addClassName(LumoUtility.BorderColor.CONTRAST_90);
        this.addClassName(LumoUtility.Padding.Top.NONE);
        this.addClassName(LumoUtility.AlignItems.CENTER);


        this.setSpacing(true);
        this.setPadding(true);

        this.add(profileAvatar, profileName, createPostMenuLayout());

    }

    /**
     * Creates the MenuBar that contains:
     * - Delete Post and View Statistics Buttons if the User is either the owner of the post or an administrator
     * - Report Button if not
     *
     * @return the MenuBar containing all the buttons
     * @author Ksenia Myakisheva
     */
    private MenuBar createPostMenuLayout(){
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        MenuItem options = menuBar.addItem(new H3("..."));
        SubMenu subItems = options.getSubMenu();
        if(poster.equals(authenticatedUser) || authenticatedUser.getRoles().contains(Role.ADMIN)){
            MenuItem delete = subItems.addItem("Delete");
            MenuItem statistics = subItems.addItem("View statistics");

            delete.addClickListener(e -> {
                createConfirmDelete().open();
            });

            statistics.addClickListener(e -> {
                createStatisticsLayout().open();
            });

        }else{
            MenuItem report = subItems.addItem("Report");
            report.addClickListener(e -> {
                createReportLayout().open();
            });


        }

        return menuBar;
    }

    /**
     * Creates the ConfirmDialog that allows the user to report the post.
     * Calls postService.newReport if the user confirms the report.
     *
     * @return The ConfirmDialog for the report action
     * @author Ksenia Myakisheva & José Alejandro Sarmiento
     */
    private ConfirmDialog createReportLayout(){
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setWidth("475px");
        dialog.setHeader("Report?");

        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setLabel("Reason for reporting");
        radioGroup.setItems("Violence", "Sexual content", "Discriminatory content", "Other (write your reason)");
        TextField other = new TextField("Other");
        dialog.add(radioGroup, other);

        dialog.setCancelable(true);
        dialog.setConfirmText("Report");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(e -> {
            String reason;
            if(radioGroup.getValue().equals("Other (write your reason)")){
                reason = other.getValue();
            }else{
                reason = radioGroup.getValue();
            }
            postService.newReport(authenticatedUser, post, reason);
            Notification.show("Report successful");
        });
        return dialog;

    }

    /**
     * Creates the ConfirmDialog that allows the user to delete the post.
     * Calls postService.delete if the user confirms the report.
     *
     * @return the ConfirmDialog for the delete action
     * @author Ksenia Myakisheva
     */
    private ConfirmDialog createConfirmDelete(){

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete?");
        dialog.setText(
                "Are you sure you want to permanently delete this item?");

        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> {
            postService.deletePost(post);
            this.removeFromParent();
        });
        return dialog;
    }

    /**
     * Creates the Dialog that allows the user to see the statistics of the generated points by the post.
     * It is divided by:
     * - Direct points (generated when the post is reposted directly). Here you can see the users that reposted.
     * - Indirect points (generated when the post's reposts are reposted)
     *
     * @return the Dialog to see the statistics
     * @author José Alejandro Sarmiento
     */
    private ConfirmDialog createStatisticsLayout(){

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setWidth("475px");
        dialog.setHeader("Point Statistics");
        dialog.setCancelable(true);
        dialog.setConfirmText("Ok");
        dialog.setCancelText("Close");

        VerticalLayout directRepostersLayout = new VerticalLayout();

        int directPoints = 0;
        int indirectPoints = 0;

        List<PostsPointLog> postLogs = postService.findAllLogsByPost(post);

        if(postLogs.isEmpty()){
            dialog.add(new H3("Nothing to see here yet ;)"));
            return dialog;
        }

        for(PostsPointLog postLog : postLogs){

            if(postLog.isDirect()){

                String username = userService.findById(postLog.getUserId()).getUsername();
                Label profileName = new Label(username + " - " + 15);
                directPoints += postLog.getPoints().intValue();
                directRepostersLayout.add(profileName);

            }else{
                indirectPoints += postLog.getPoints().intValue();
            }

        }


        Notification directPointsInfoNot = new Notification("Points from people that reposted you directly");
        Notification indirectPointsInfoNot = new Notification("Points from people that reposted your direct reposters");

        Icon directPointsInfoIcon = new Icon(VaadinIcon.QUESTION_CIRCLE_O);
        directPointsInfoIcon.setSize("17px");

        Icon indirectPointsInfoIcon = new Icon(VaadinIcon.QUESTION_CIRCLE_O);
        indirectPointsInfoIcon.setSize("17px");

        directPointsInfoIcon.getElement().addEventListener("mouseover", e -> directPointsInfoNot.open());
        indirectPointsInfoIcon.getElement().addEventListener("mouseover", e -> indirectPointsInfoNot.open());

        directPointsInfoIcon.getElement().addEventListener("mouseout", e -> directPointsInfoNot.close());
        indirectPointsInfoIcon.getElement().addEventListener("mouseout", e -> indirectPointsInfoNot.close());

        HorizontalLayout directLayout = new HorizontalLayout(new Details("Direct Points: " + directPoints, directRepostersLayout), directPointsInfoIcon);
        directLayout.setAlignItems(Alignment.BASELINE);

        HorizontalLayout indirectLayout = new HorizontalLayout(new Label("Indirect Points: " + indirectPoints), indirectPointsInfoIcon);
        directLayout.setAlignItems(Alignment.BASELINE);

        dialog.add(directLayout, indirectLayout, directPointsInfoNot, indirectPointsInfoNot);
        return dialog;

    }

    /**
     * Refreshes the number of points the post has generated. Useful when the post is reposted.
     *
     * @param points New amount of points to be displayed.
     * @author José Alejandro Sarmiento
     */
    public void refreshPoints(BigInteger points){
        profileName.setText(poster.getUsername() + " - "
                            + points + " - "
                            + post.getPost_date().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }


}