package com.example.application.views.feed;

import com.example.application.data.entities.User;
import com.example.application.services.InteractionService;
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
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

@PageTitle("Triis - Feed")
@Route(value = "feed", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll

public class FeedView extends HorizontalLayout  {

    private final UserService userService;
    private final PostService postService;
    private User authenticatedUser;

    FeedPanel feedPanel ;

    SpringAsyncConfig executor = new SpringAsyncConfig();

    private TabSheet feedPanel;
    private User authenticatedUser;

    private final UserService userService;
    private final PostService postService;
    private final InteractionService interactionService;

    public FeedView(UserService userService, PostService postService, InteractionService interactionService) {
        this.postService = postService;
        this.userService = userService;
        this.interactionService = interactionService;

        User authenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        executor.getAsyncExecutor().execute(() -> {
            userService.loadRecommendations(authenticatedUser);
        });

        feedPanel = new TabSheet();

        feedPanel.addClassName("feed-panel");
        feedPanel.addClassName(LumoUtility.AlignItems.CENTER);

        feedPanel.add("Recommendations", new FeedScroller(FeedType.RECOMMENDATION, authenticatedUser, userService, postService, UI.getCurrent(), this.interactionService));
        feedPanel.add("Discovery", new FeedScroller(FeedType.DISCOVERY, authenticatedUser, userService, postService, UI.getCurrent(), this.interactionService));
        feedPanel.add("Following", new FeedScroller(FeedType.FOLLOWING, authenticatedUser, userService, postService, UI.getCurrent(), this.interactionService));
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
