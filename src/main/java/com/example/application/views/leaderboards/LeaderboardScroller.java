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
    private final LeaderboardService leaderboardService;
    private final LeaderboardService.LeaderboardType leaderboardType;
    /**
     * the list of posts that appear on the top 10
     */
    private List<Post> posts;
    /**
     * the list of users that appear on the top 10
     */
    private List<User> users;
    private VerticalLayout content = new VerticalLayout();

    /**
     * calls  a method which initializes the vertical layout CONTENT with necessary components
     * @param leaderboardType the selected top 10: posts (select by time span) or users
     * @param userService
     * @param leaderboardService
     */
    LeaderboardScroller(LeaderboardService.LeaderboardType leaderboardType, UserService userService, LeaderboardService leaderboardService) {
        this.userService = userService;
        this.leaderboardService = leaderboardService;
        this.leaderboardType = leaderboardType;

        this.addClassName(LumoUtility.AlignItems.CENTER);
        content.setSpacing(true);
        content.addClassName(LumoUtility.AlignItems.CENTER);
        addResult();
        this.add(content);
    }

    /**
     * according to leaderboardType, perform a query to select the top ten and add them to CONTENT
     */
    private void addResult(){
        switch (leaderboardType){
            case TODAY -> posts = leaderboardService.findTenByPointedOriginalPostIdOrderByPointsDescCreatedToday();
            case THIS_WEEK -> posts = leaderboardService.findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisWeek();
            case THIS_MONTH -> posts = leaderboardService.findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisMonth();
            case THIS_YEAR -> posts = leaderboardService.findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisYear();
            case ALL_TIME -> posts = leaderboardService.findTenByPointedOriginalPostIdOrderByPointsDesc();
            case USERS -> users = leaderboardService.findUsersHighestType1Points();
        }

        if(posts != null && posts.size() != 0){
            for(int i = 0; i < Math.min(10, posts.size()); i++){
                content.add(new H4 ((i+1) + " Position"), new PostPanel(posts.get(i), userService, leaderboardService));
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
