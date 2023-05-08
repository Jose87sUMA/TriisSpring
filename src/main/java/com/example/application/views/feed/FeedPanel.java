package com.example.application.views.feed;

import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.data.services.feed.DiscoveryService;
import com.example.application.data.services.feed.FeedService;
import com.example.application.data.services.feed.FollowingService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.*;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class FeedPanel extends TabSheet {
    private User authenticatedUser;
    private final UserService userService;
    private final PostService postService;

    public FeedPanel(User authenticatedUser, UserService userService, PostService postService){

        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.postService = postService;

        this.addClassName("feed-panel");
        this.addClassName(LumoUtility.AlignItems.CENTER);

        DiscoveryService dis = (DiscoveryService) postService.getFeedService(FeedService.FeedType.DISCOVERY);
        this.add("Discovery", new Div(new FeedScroller(dis, userService, postService)));
        FollowingService fol = (FollowingService) postService.getFeedService(FeedService.FeedType.FOLLOWING);
        fol.setUserId(authenticatedUser.getUserId());
        this.add("Following", new Div(new FeedScroller(fol, userService, postService)));

        this.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
    }

}
