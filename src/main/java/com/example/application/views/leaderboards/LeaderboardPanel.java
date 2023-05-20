package com.example.application.views.leaderboards;

import com.example.application.data.entities.User;
import com.example.application.services.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class LeaderboardPanel extends TabSheet {

    private VerticalLayout content = new VerticalLayout();

    public LeaderboardPanel(UserService userService, PostService postService, LeaderboardService leaderboardService, InteractionService interactionService){

        this.addClassName("feed-panel");
        this.addClassName(LumoUtility.AlignItems.CENTER);

        this.addClassName("leaderboard-panel");
        this.addClassName(LumoUtility.AlignItems.CENTER);

        this.add("Today", new LeaderboardScroller(LeaderboardService.LeaderboardType.TODAY, userService, postService, interactionService, leaderboardService, UI.getCurrent()));
        this.add("This week", new LeaderboardScroller(LeaderboardService.LeaderboardType.THIS_WEEK, userService, postService, interactionService, leaderboardService, UI.getCurrent()));
        this.add("This month", new LeaderboardScroller(LeaderboardService.LeaderboardType.THIS_MONTH,  userService, postService, interactionService, leaderboardService, UI.getCurrent()));
        this.add("This year", new LeaderboardScroller(LeaderboardService.LeaderboardType.THIS_YEAR, userService,  postService, interactionService, leaderboardService, UI.getCurrent()));
        this.add("All time", new LeaderboardScroller(LeaderboardService.LeaderboardType.ALL_TIME,  userService,  postService, interactionService, leaderboardService, UI.getCurrent()));
        this.add("Users", new LeaderboardScroller(LeaderboardService.LeaderboardType.USERS, userService,  postService, interactionService, leaderboardService, UI.getCurrent()));
        this.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);

    }

}
