package com.example.application.views.feed;

import com.example.application.data.entities.User;
import com.example.application.services.PostService;
import com.example.application.services.UserService;
import com.example.application.services.FeedService.FeedType;
import com.example.application.services.threads.SpringAsyncConfig;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * View manager for the feed.
 */
@PageTitle("Triis - Feed")
@Route(value = "feed", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class FeedView extends HorizontalLayout {

    SpringAsyncConfig executor = new SpringAsyncConfig();

    private TabSheet feedPanel;
    private User authenticatedUser;

    private final UserService userService;
    private final PostService postService;

    public FeedView(UserService userService, PostService postService) {
        this.postService = postService;
        this.userService = userService;
        User authenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        executor.getAsyncExecutor().execute(() -> {
            userService.loadRecommendations(authenticatedUser);
        });

        feedPanel = new TabSheet();

        feedPanel.addClassName("feed-panel");
        feedPanel.addClassName(LumoUtility.AlignItems.CENTER);

        feedPanel.add("Recommendations", new FeedScroller(FeedType.RECOMMENDATION, authenticatedUser, userService, postService, UI.getCurrent()));
        feedPanel.add("Discovery", new FeedScroller(FeedType.DISCOVERY, authenticatedUser, userService, postService, UI.getCurrent()));
        feedPanel.add("Following", new FeedScroller(FeedType.FOLLOWING, authenticatedUser, userService, postService, UI.getCurrent()));
        feedPanel.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);

        feedPanel.setSelectedIndex(1);
        feedPanel.getElement().getChild(0).setAttribute("style", "width: 450px;");

        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.setMargin(true);
        this.setVerticalComponentAlignment(Alignment.CENTER, feedPanel);

        add(feedPanel);
    }

//    public void loadMore(){
//        System.out.println("Loading more");
//    }

}
