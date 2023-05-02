package com.example.application.views.feed;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.repositories.PostsRepository;
import com.example.application.data.repositories.UsersRepository;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.persistence.Index;

import java.util.List;

public class FeedPanel extends Scroller {

    private User authenticatedUser;
    private final UserService userService;
    private final PostService postService;
    private VerticalLayout content = new VerticalLayout();


    public FeedPanel(User authenticatedUser, UserService userService, PostService postService){

        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.postService = postService;

        this.addClassName("feed-panel");

        List<Post> postsByFollowing = postService.getAllByPeopleFollowed(authenticatedUser);


        try {
            for (int i = 0; i < 15; ++i) {

                content.add(new PostPanel(postsByFollowing.get(i), userService, postService));

            }
        }catch(IndexOutOfBoundsException e){System.out.println("Error creating feed panel, check FOR condition: "+e.getMessage()+" | "+e.getCause());}

        content.setSpacing(true);
        content.addClassName(LumoUtility.AlignItems.CENTER);

        this.addClassName(LumoUtility.AlignItems.CENTER);
        this.setContent(content);

    }

}
