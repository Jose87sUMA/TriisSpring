package com.example.application.views.profile;

import com.example.application.data.entities.PostsPointLog;
import com.example.application.data.entities.User;
import com.example.application.data.entities.UserPointLog;
import com.example.application.services.FeedService;
import com.example.application.services.MakePostService;
import com.example.application.services.PostService;
import com.example.application.services.UserService;
import com.example.application.views.MainLayout;
import com.example.application.views.feed.FeedScroller;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@PageTitle("Triis - Profile")
@Route(value = "profile", layout = MainLayout.class)
@RouteAlias(value = "profile", layout = MainLayout.class)
@PermitAll
public class ProfileView extends VerticalLayout implements HasUrlParameter<String> {

    private FeedScroller profilePanel;
    private User user, authenticatedUser;
    private final UserService userService;
    private final PostService postService;
    private final MakePostService makePostService;


    public ProfileView(UserService userService, PostService postService, MakePostService makePostService) {

        this.postService = postService;
        this.userService = userService;
        this.makePostService = makePostService;
        this.authenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

        this.removeAll();

        if(parameter == null)
            user = authenticatedUser;
        else if((user = userService.findByUsername(parameter)) == null){
            event.forwardTo("feed");
            return;
        }

        profilePanel = new FeedScroller(FeedService.FeedType.PROFILE, user, userService, postService, UI.getCurrent());

        this.setJustifyContentMode(JustifyContentMode.CENTER);
        this.setMargin(true);
        this.setHorizontalComponentAlignment(Alignment.CENTER, profilePanel);

        HorizontalLayout buttons = createButtonsLayout();

        this.setHorizontalComponentAlignment(Alignment.CENTER, buttons);
        this.setAlignItems(Alignment.CENTER);

        removeAll();

        Avatar avatar = new Avatar(user.getUsername());
        StreamResource resource = new StreamResource("profile-pic",
                () -> new ByteArrayInputStream(userService.getProfilePicBytes(user)));
        avatar.setImageResource(resource);
        avatar.setThemeName("xlarge");
        avatar.getElement().setAttribute("tabindex", "-1");

        add(avatar);
        add(new H1(user.getUsername()), buttons, profilePanel);


    }

    Button followers, follow;

    /**
     * CUSTOMIZING  BUTTONS
     */
    private HorizontalLayout createButtonsLayout() {
        //buttons
        Button following = new Button("Following: " + userService.getFollowing(user).size());
        following.addClickListener(event -> UI.getCurrent().navigate("profile/?following/" + user.getUsername()));

        followers = new Button("Followers: " + userService.getFollowers(user).size());
        Button type1 = new Button("Type 1 points: " + user.getType1Points());
        type1.addClickListener(e -> createStatisticsLayout().open());
        Button type2 = new Button("Type 2 points: " + user.getType2Points());

        follow = new Button(!isFollowing(user) ? "Follow" : "Unfollow");
        follow.addClickListener(e -> follow());

        Button makePost = new Button("Make a Post");

        Button editProfile = new Button("Edit Profile");
        editProfile.addClickListener(event -> UI.getCurrent().navigate("profile/?edit/" + user.getUsername()));

        if(!user.equals(userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()))) {
            makePost.setVisible(false);
            editProfile.setVisible(false);
        }else{
            follow.setVisible(false);
            makePost.addClickListener(e -> new MakePostBox(postService, userService, makePostService, profilePanel).open()) ;
        }
        return new HorizontalLayout(followers, following, type1, type2, follow, makePost, editProfile);

    }

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

        List<UserPointLog> userLogs = userService.findAllUserLogsByBeneficiary(user);
        Map<User, Integer> directContributers = new HashMap<>();
        for(UserPointLog userLog : userLogs){

            if(userLog.isDirect()){

                directPoints += userLog.getPoints().intValue();
                User payer = userService.findById(userLog.getPayerUserId());
                directContributers.put(payer, directContributers.getOrDefault(payer,0) + userLog.getPoints().intValue());

            }else{
                indirectPoints += userLog.getPoints().intValue();
            }
        }

        Stream<Map.Entry<User,Integer>> sorted =
                directContributers.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue());

        for(Map.Entry<User,Integer> entry : sorted.toList()){

            Label profileName = new Label(entry.getKey().getUsername() + " - " + entry.getValue());
            directRepostersLayout.add(profileName);

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
     * Following or unfollowing a user and refresing the buttons.
     */
    void follow(){
        if (!isFollowing(user)) userService.follow(authenticatedUser, user);
        else userService.unfollow(authenticatedUser, user);
        followers.setText("Followers: " + userService.getFollowers(user).size());
        follow.setText(!isFollowing(user) ? "Follow" : "Unfollow");
    }

    /**
     * Is the current user following user.
     * @param user The user followed?
     * @return true if followed. false otherwise.
     */
    boolean isFollowing(User user){
        return userService.getFollowing(authenticatedUser).contains(user);
    }

    /*private Image createProfilePicture(){
        Image content = userService.getProfilePicture(user);

        float height = Float.parseFloat(content.getHeight().substring(0,content.getHeight().length()-2))+60;
        this.setHeight(height + "px");
        this.setWidth(content.getWidth());
    }*/



}
