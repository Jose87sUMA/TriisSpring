package com.example.application.views.feed;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.services.InteractionService;
import com.example.application.services.PostService;
import com.example.application.services.UserService;
import com.example.application.views.feed.postPanel.PostPanel;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

public class FeedPanel extends Scroller {

    private User authenticatedUser;

    private final UserService userService;
    private final PostService postService;
    private final InteractionService interactionService;

    private VerticalLayout content = new VerticalLayout();

    public FeedPanel(User authenticatedUser, UserService userService, PostService postService, InteractionService interactionService){

        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.postService = postService;
        this.interactionService = interactionService;

        this.addClassName("feed-panel");

        List<Post> postsByFollowing = postService.getAllByPeopleFollowed(authenticatedUser);

        for(int i = 0; i < Math.min(postsByFollowing.size(), 15); ++i){
            content.add(new PostPanel(postsByFollowing.get(i), userService, postService, this.interactionService));
        }

        content.setSpacing(true);
        content.addClassName(LumoUtility.AlignItems.CENTER);

        this.addClassName(LumoUtility.AlignItems.CENTER);
        this.setContent(content);

    }

}
