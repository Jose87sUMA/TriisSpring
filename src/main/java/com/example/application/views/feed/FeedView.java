package com.example.application.views.feed;

import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.data.services.FeedService.FeedType;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
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

    private TabSheet feedPanel;
    private User authenticatedUser;

    private final UserService userService;
    private final PostService postService;

    public FeedView(UserService userService, PostService postService) {

        User authenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        feedPanel = new TabSheet();

        feedPanel.addClassName("feed-panel");
        feedPanel.addClassName(LumoUtility.AlignItems.CENTER);

        feedPanel.add("Discovery", new FeedScroller(FeedType.DISCOVERY, authenticatedUser, userService, postService));
        feedPanel.add("Following", new FeedScroller(FeedType.FOLLOWING, authenticatedUser, userService, postService));
        feedPanel.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);

//        getElement().executeJs("""
//            var el = this;
//            el.addEventListener("scroll", function(e) {
//                if(el.scrollTop + el.clientHeight == el.scrollHeight) {
//                    this.$server.loadMore();
//                }
//            });
//        """);

        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.setMargin(true);
        this.setVerticalComponentAlignment(Alignment.CENTER, feedPanel);

        add(feedPanel);
    }

//    public void loadMore(){
//        System.out.println("Loading more");
//    }

}
