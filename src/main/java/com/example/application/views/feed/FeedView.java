package com.example.application.views.feed;

import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.data.services.FeedService.FeedType;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
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

@PageTitle("Triis - Feed")
@Route(value = "Feed", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class FeedView extends HorizontalLayout {

    private TabSheet feedPanel;
    private User authenticatedUser;

    private final UserService userService;
    private final PostService postService;

    public FeedView(UserService userService, PostService postService) {

        this.postService = postService;
        this.userService = userService;
        this.authenticatedUser =  userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        feedPanel = new TabSheet();

        feedPanel.addClassName("feed-panel");
        feedPanel.addClassName(LumoUtility.AlignItems.CENTER);

        feedPanel.add("Discovery", new FeedScroller(FeedType.DISCOVERY, authenticatedUser, userService, postService));
        feedPanel.add("Following", new FeedScroller(FeedType.FOLLOWING, authenticatedUser, userService, postService));
        feedPanel.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);

//        getElement().executeJs("""
//            var obj = $0.parentNode;
//            obj.onscroll = function(e){
//                if (obj.scrollTop == (obj.scrollHeight - obj.offsetHeight)){
//                    alert("End");
//                    $0.$server.loadMore();
//                }
//            };
//        """, getElement());

        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.setMargin(true);
        this.setVerticalComponentAlignment(Alignment.CENTER, feedPanel);

        add(feedPanel);
    }

    public void loadMore(){
        System.out.println("Loading more");
    }

}
