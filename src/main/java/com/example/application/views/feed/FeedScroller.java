package com.example.application.views.feed;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import com.example.application.data.services.FeedService.*;
import com.example.application.data.services.FeedService;


import java.util.*;

/**
 * Class that manages a feed.
 */
public class FeedScroller extends VerticalLayout {
    SortType current = null;

    Button loadMore;
    MenuBar sorting;

    final Map<SortType, Comparator<Post>> sorter = Map.of(SortType.RECENT, Comparator.comparing(Post::getPost_date, Comparator.naturalOrder()),
                                                          SortType.POPULAR, Comparator.comparing(Post::getPoints, Comparator.reverseOrder()));

    PriorityQueue<Post> buffer;

    private final UserService userService;
    private final PostService postService;
    private final FeedService feedService;

    private VerticalLayout content = new VerticalLayout();

    /**
     * Constructs the feed.
     * @param feedType Type of feed.
     * @param user If SortType PROFILE user must be the profile. Otherwise, user is authenticated user.
     * @param userService
     * @param postService
     */
    public FeedScroller(FeedType feedType, User user, UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
        this.feedService = new FeedService(this.postService.getPostRepository(), feedType, user.getUserId());

        loadMore = new Button("Load More Posts", e -> loadMore());

        sorting = sortChooser();
        this.add(sorting);

        this.setSpacing(true);

        this.addClassName(LumoUtility.AlignItems.CENTER);

        changeSorting(SortType.RECENT);

        content.setSpacing(true);
        content.addClassName(LumoUtility.AlignItems.CENTER);

        this.add(content);
        this.add(loadMore);
    }

    /**
     * Adds posts into the feed and loads more posts from database.
     */
    @ClientCallable
    public void loadMore(){
        if (loadMore.isEnabled()) {
            addPosts();
            loadPosts();
        }
    }

    /**
     * Menu Item to choose which type of sorting for the feed.
     * @return Component in charge of this action.
     */
    MenuBar sortChooser(){
        MenuBar menu = new MenuBar();
        MenuItem sortChooser = menu.addItem("Sorting By Recent");
        ComponentEventListener<ClickEvent<MenuItem>> listener = e -> {
            if (e.getSource().getText().equals("Change to Recent")){
                this.changeSorting(SortType.RECENT);
                sortChooser.setText("Sorting By Recent");
            }else if (e.getSource().getText().equals("Change to Popular")){
                this.changeSorting(SortType.POPULAR);
                sortChooser.setText("Sorting By Popular");
            }
        };
        SubMenu subMenuSort = sortChooser.getSubMenu();
        subMenuSort.addItem("Change to Recent", listener);
        subMenuSort.addItem("Change to Popular", listener);
        menu.setOpenOnHover(true);
        return menu;
    }

    /**
     * Adds posts into the feed.
     */
    private void addPosts(){
        for(int i = 0; i < FeedService.ELEMENTS; i++){
            if (buffer.isEmpty()) {
                content.add(new Text("No more posts available."));
                loadMore.setEnabled(false);
                break;
            }
            content.add(new PostPanel(buffer.poll(), userService, postService));
        }
    }

    /**
     * Loads posts from database.
     */
    private void loadPosts(){
        buffer.addAll(feedService.findNextNPosts());
        System.out.println(buffer.toString());
    }

    /**
     * Changes sorting of the feed. Also clears it.
     * @param st Sorting mode.
     */
    public void changeSorting(SortType st){
        if (current != st) {
            current = st;
            feedService.setSort(st);
            refresh();
        }
    }

    /**
     * Refreshes the feed. Clears buffer, resets and adds posts.
     */
    public void refresh(){
        buffer = new PriorityQueue<>(sorter.get(current));
        loadMore.setEnabled(true);
        feedService.reset();
        content.removeAll();
        loadPosts();
        loadPosts();
        addPosts();
    }
}
