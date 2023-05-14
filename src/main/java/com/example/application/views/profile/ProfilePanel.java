package com.example.application.views.profile;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.views.feed.PostPanel;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

import static java.lang.Math.min;

public class ProfilePanel extends Scroller {

    private User authenticatedUser;
    private final UserService userService;
    private final PostService postService;
    private VerticalLayout content = new VerticalLayout();


    public VerticalLayout getContent() {
        return content;
    }

    public ProfilePanel(User authenticatedUser, UserService userService, PostService postService){

        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.postService = postService;

        this.addClassName("profile-panel");

        //List<Post> myPosts = postService.findAllByUserAndDate(authenticatedUser);

        //for(int i = 0; i < min(15,myPosts.size()); ++i){
        //    content.add(new PostPanel(myPosts.get(i), userService, postService));
        //}

        content.setSpacing(true);
        content.addClassName(LumoUtility.AlignItems.CENTER);

        this.addClassName(LumoUtility.AlignItems.CENTER);
        this.setContent(content);

    }

}
