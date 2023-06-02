package com.example.application.views.leaderboards;

import com.example.application.services.LeaderboardService;
import com.example.application.services.PostService;
import com.example.application.services.UserService;
import com.example.application.services.InteractionService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
@PageTitle("Triis - Leaderboards")
@Route(value = "leaderboards", layout = MainLayout.class)
@RouteAlias(value = "leaderboards", layout = MainLayout.class)
@PermitAll
public class LeaderboardView extends HorizontalLayout {

    private TabSheet leaderboardPanel;

    /**
     * Contains a Tab to select a leaderboard
     * each leaderboard initializes its own type of scroller (predefined types)
     * leaderboard types are divided into:
     * - 10 top posts with the highest number of points from a given type span
     * - 10 top users with the highest number of points
     *
     * @param userService
     * @param postService
     * @param interactionService
     * @param leaderboardService
     */
    public LeaderboardView(UserService userService, PostService postService, LeaderboardService leaderboardService, InteractionService interactionService) {

        leaderboardPanel = new LeaderboardPanel(userService, postService, leaderboardService, interactionService);

        this.setJustifyContentMode(JustifyContentMode.CENTER);
        this.setMargin(true);
        this.setVerticalComponentAlignment(Alignment.CENTER, leaderboardPanel);

        this.add(leaderboardPanel);
    }

}
