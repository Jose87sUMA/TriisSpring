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
    private final LeaderboardService.LeaderboardType leaderboardType;

    private List<Post> posts;
    private VerticalLayout content = new VerticalLayout();

    LeaderboardScroller(LeaderboardService.LeaderboardType leaderboardType, User authenticatedUser, UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
        this.leaderboardType = leaderboardType;
        //this.leaderboardService.initializeLeaderboard();

        this.addClassName(LumoUtility.AlignItems.CENTER);

        content.setSpacing(true);
        content.addClassName(LumoUtility.AlignItems.CENTER);
        addResult();
        this.add(content);
    }

    private void addResult(){
        switch (leaderboardType){
            case TODAY -> posts = postService.findByPointedOriginalPostIdOrderByPointsDescCreatedToday();
            case THIS_WEEK -> posts = postService.findByPointedOriginalPostIdOrderByPointsDescCreatedThisWeek();
            case THIS_MONTH -> posts = postService.findByPointedOriginalPostIdOrderByPointsDescCreatedThisMonth();
            case THIS_YEAR -> posts = postService.findByPointedOriginalPostIdOrderByPointsDescCreatedThisYear();
            case ALL_TIME -> posts = postService.findAllByPointedOriginalPostIdOrderByPointsDesc();
            //case USERS -> users = postService.findAllByPointedOriginalPostIdOrderByPointsDesc();
        }

        for(int i = 0; i < Math.min(10, posts.size()); i++){
            content.add(new H4(posts.get(i).getPoints().toString()));
        }
    }

}
