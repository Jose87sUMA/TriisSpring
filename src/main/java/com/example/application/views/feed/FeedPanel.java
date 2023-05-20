package com.example.application.views.feed;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.services.FeedService;
import com.example.application.services.InteractionService;
import com.example.application.services.PostService;
import com.example.application.services.UserService;
import com.example.application.views.feed.postPanel.PostPanel;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

public class FeedPanel extends TabSheet {

    private User authenticatedUser;

    private final UserService userService;
    private final PostService postService;
    private final InteractionService interactionService;

    private VerticalLayout content = new VerticalLayout();

    public FeedPanel(User authenticatedUser, UserService userService, PostService postService, InteractionService interactionService){

        this.postService = postService;
        this.userService = userService;
        this.interactionService = interactionService;

        this.addClassName("feed-panel");
        this.addClassName(LumoUtility.AlignItems.CENTER);

        this.add("Recommendations", new FeedScroller(FeedService.FeedType.RECOMMENDATION, authenticatedUser, userService, postService, UI.getCurrent(), this.interactionService));
        this.add("Discovery", new FeedScroller(FeedService.FeedType.DISCOVERY, authenticatedUser, userService, postService, UI.getCurrent(), this.interactionService));
        this.add("Following", new FeedScroller(FeedService.FeedType.FOLLOWING, authenticatedUser, userService, postService, UI.getCurrent(), this.interactionService));
        this.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);

        this.setSelectedIndex(1);
        this.getElement().getChild(0).setAttribute("style", "width: 450px;");

    }

}