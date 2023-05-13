package com.example.application.views.leaderboards;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.LeaderboardService;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

public class LeaderboardScroller extends VerticalLayout {

    private final UserService userService;
    private final PostService postService;
    private final LeaderboardService leaderboardService;

    private VerticalLayout content = new VerticalLayout();

    LeaderboardScroller(LeaderboardService.LeaderboardType leaderboardType, User authenticatedUser, UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
        this.leaderboardService = this.postService.getLeaderboardService(leaderboardType, authenticatedUser.getUserId());

        //this.leaderboardService.initializeLeaderboard();

        this.addClassName(LumoUtility.AlignItems.CENTER);

        content.setSpacing(true);
        content.addClassName(LumoUtility.AlignItems.CENTER);
        addResult();
        this.add(content);
    }

    private void addResult(){
        List<Post> posts = postService.findAllByPointedOriginalPostIdOrderByPointsDesc();
        for(int i = 0; i < Math.min(10, posts.size()); i++){
            content.add(new H4(posts.get(i).getPoints().toString()));
        }
    }

}
