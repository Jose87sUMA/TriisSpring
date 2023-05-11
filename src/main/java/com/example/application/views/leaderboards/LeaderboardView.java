package com.example.application.views.leaderboards;

import com.example.application.data.entities.User;
import com.example.application.data.services.LeaderboardService;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

@PageTitle("Triis - Leaderboard")
@Route(value = "leaderboard", layout = MainLayout.class)
@RouteAlias(value = "leaderboard", layout = MainLayout.class)
@PermitAll
public class LeaderboardView extends HorizontalLayout {

    private TabSheet feedPanel;
    private User authenticatedUser;

    private final UserService userService;
    private final PostService postService;

    public LeaderboardView(UserService userService, PostService postService) {

        this.postService = postService;
        this.userService = userService;
        this.authenticatedUser =  userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        feedPanel = new TabSheet();

        feedPanel.addClassName("leaderboard-panel");
        feedPanel.addClassName(LumoUtility.AlignItems.CENTER);

        feedPanel.add("Today", new LeaderboardScroller(LeaderboardService.LeaderboardType.TODAY, authenticatedUser, userService, postService));
        feedPanel.add("This week", new LeaderboardScroller(LeaderboardService.LeaderboardType.THIS_WEEK, authenticatedUser, userService, postService));
        feedPanel.add("This month", new LeaderboardScroller(LeaderboardService.LeaderboardType.THIS_MONTH, authenticatedUser, userService, postService));
        feedPanel.add("This year", new LeaderboardScroller(LeaderboardService.LeaderboardType.THIS_YEAR, authenticatedUser, userService, postService));
        feedPanel.add("All time", new LeaderboardScroller(LeaderboardService.LeaderboardType.ALL_TIME, authenticatedUser, userService, postService));
        feedPanel.add("Users", new LeaderboardScroller(LeaderboardService.LeaderboardType.ALL_TIME, authenticatedUser, userService, postService));
        feedPanel.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);


        this.setJustifyContentMode(JustifyContentMode.CENTER);
        this.setMargin(true);
        this.setVerticalComponentAlignment(Alignment.CENTER, feedPanel);

        add(feedPanel);
    }

    public void loadMore(){
        System.out.println("Loading more");
    }

}
