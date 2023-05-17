package com.example.application.views.profile;

import com.example.application.data.entities.User;
import com.example.application.services.FeedService;
import com.example.application.services.MakePostService;
import com.example.application.services.PostService;
import com.example.application.services.UserService;
import com.example.application.views.MainLayout;
import com.example.application.views.feed.FeedScroller;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;

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
