package com.example.application.views.leaderboards;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.LeaderboardService;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.views.feed.PostPanel;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

public class LeaderboardScroller extends VerticalLayout {

    private final UserService userService;
    private final PostService postService;
    private final LeaderboardService.LeaderboardType leaderboardType;

    private List<Post> posts;
    private List<User> users;
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
            case TODAY -> posts = postService.findTenByPointedOriginalPostIdOrderByPointsDescCreatedToday();
            case THIS_WEEK -> posts = postService.findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisWeek();
            case THIS_MONTH -> posts = postService.findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisMonth();
            case THIS_YEAR -> posts = postService.findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisYear();
            case ALL_TIME -> posts = postService.findTenByPointedOriginalPostIdOrderByPointsDesc();
            case USERS -> users = userService.findUsersHighestType1Points();
        }

        if(posts != null && posts.size() != 0){
            for(int i = 0; i < Math.min(10, posts.size()); i++){
                content.add(new H4 ((i+1) + " Position"), new PostPanel(posts.get(i), userService, postService));
            }
        }else if(users != null && users.size() != 0){
            Button buttonUser[] = new Button[users.size()];
            for(int i = 0; i < Math.min(10, users.size()); i++){
                buttonUser[i] = new Button(users.get(i).getUsername() + ":   " + users.get(i).getType1Points() + " points");
                buttonUser[i].setWidth("100%");
                //button[i].addClickListener(UI.getCurrent().navigate(ProfileView.class, username));
                content.add(new H4 ((i+1) + " Position"), buttonUser[i]);
            }
        }else{
            content.add(new H4("Competition is tight isn't it? D;"));
        }

    }


}
