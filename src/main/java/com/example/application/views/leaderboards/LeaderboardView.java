package com.example.application.views.leaderboards;

import com.example.application.data.repositories.PostsRepository;
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
@PageTitle("Triis - Leaderboard")
@Route(value = "leaderboard", layout = MainLayout.class)
@RouteAlias(value = "leaderboard", layout = MainLayout.class)
@PermitAll
public class LeaderboardView extends HorizontalLayout {

    private TabSheet leaderboardPanel;
    private final UserService userService;
    private final PostService postService;
    private final LeaderboardService leaderboardService;

    /**
     * contains a Tab to select a leaderboard
     * each leaderboard initializes its own type of scroller (predefined types)
     * leaderboard types are divided into:
     * - 10 top posts with the highest number of points from a given type span
     * - 10 top users with the highest number of points
     * @param userService
     * @param leaderboardService
     */
    public LeaderboardView(UserService userService,PostService postService, LeaderboardService leaderboardService) {

        this.userService = userService;
        this.postService = postService;
        this.leaderboardService = leaderboardService;

        leaderboardPanel = new TabSheet();

        leaderboardPanel.addClassName("leaderboard-panel");
        leaderboardPanel.addClassName(LumoUtility.AlignItems.CENTER);

        leaderboardPanel.add("Today", new LeaderboardScroller(LeaderboardService.LeaderboardType.TODAY, userService, postService, leaderboardService));
        leaderboardPanel.add("This week", new LeaderboardScroller(LeaderboardService.LeaderboardType.THIS_WEEK, userService, postService,leaderboardService));
        leaderboardPanel.add("This month", new LeaderboardScroller(LeaderboardService.LeaderboardType.THIS_MONTH,  userService, postService, leaderboardService));
        leaderboardPanel.add("This year", new LeaderboardScroller(LeaderboardService.LeaderboardType.THIS_YEAR, userService,  postService,leaderboardService));
        leaderboardPanel.add("All time", new LeaderboardScroller(LeaderboardService.LeaderboardType.ALL_TIME,  userService,  postService,leaderboardService));
        leaderboardPanel.add("Users", new LeaderboardScroller(LeaderboardService.LeaderboardType.USERS, userService,  postService,leaderboardService));
        leaderboardPanel.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);


        this.setJustifyContentMode(JustifyContentMode.CENTER);
        this.setMargin(true);
        this.setVerticalComponentAlignment(Alignment.CENTER, leaderboardPanel);

        add(leaderboardPanel);
    }

}
