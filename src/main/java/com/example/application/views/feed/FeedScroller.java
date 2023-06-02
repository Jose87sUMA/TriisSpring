package com.example.application.views.feed;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.services.InteractionService;
import com.example.application.services.PostService;
import com.example.application.services.UserService;
import com.example.application.services.threads.SpringAsyncConfig;
import com.example.application.views.feed.postPanel.PostPanel;
import com.example.application.views.feed.searchbar.SearchBar;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.*;
import java.util.*;

import com.example.application.services.FeedService.*;
import com.example.application.services.FeedService;


import java.util.*;
import java.util.concurrent.locks.Condition;

/**
 * Class that manages a feed.
 */
public class FeedScroller extends VerticalLayout {

    private final UserService userService;
    private final PostService postService;
    private final FeedService feedService;
    private final InteractionService interactionService;

    private SpringAsyncConfig executor = new SpringAsyncConfig();

    private Button loadMore;

    private SortType current = null;
    private MenuBar sorting;
    private UI ui;

    private final Map<SortType, Comparator<Post>> sorter = Map.of(SortType.RECENT, Comparator.comparing(Post::getPost_date, Comparator.reverseOrder()),
                                                          SortType.POPULAR, Comparator.comparing(Post::getPoints, Comparator.reverseOrder()));

    private PriorityQueue<Post> buffer;
    private VerticalLayout content = new VerticalLayout();

    /**
     * Constructs the feed.
     *
     * @param feedType           Type of feed.
     * @param user               If SortType PROFILE user must be the profile. Otherwise, user is authenticated user.
     * @param userService
     * @param postService
     * @param interactionService
     * @author Ziri Raha
     */
    public FeedScroller(FeedType feedType, User user, UserService userService, PostService postService, UI ui, InteractionService interactionService) {

        this.ui = ui;
        this.userService = userService;
        this.postService = postService;
        this.interactionService = interactionService;
        this.feedService = new FeedService(this.postService.getPostRepository(), feedType, user.getUserId());

        loadMore = new Button("Load More Posts", e -> loadMore());

        sorting = sortChooser();
        changeSorting(SortType.RECENT);

        content.setSpacing(true);
        content.addClassName(LumoUtility.AlignItems.CENTER);

        this.addClassName(LumoUtility.AlignItems.CENTER);
        this.setSpacing(true);

        this.add(sorting);
        this.add(content);
        this.add(loadMore);
    }

    /**
     * Adds posts into the feed and loads more posts from database.
     * @author Ziri Raha
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
     * @author Ziri Raha
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
     * Adds posts into the feed. It first creates a post panel for each of the post
     * and then starts downloading the image of that post panel concurrently.
     * They are then added to the content layout depending on the current sort.
     *
     * @author Ziri Raha & José Alejandro Sarmiento
     */
    private void addPosts(){

        Map<Post, Boolean> newPostPanelsBool = new TreeMap<>(sorter.get(current));
        Map<Post, PostPanel> newPostPanels = new TreeMap<>(sorter.get(current));
        boolean empty = false;
        for(int i = 0; i < FeedService.ELEMENTS; i++){
            if (buffer.isEmpty()) {
                empty = true;
                loadMore.setEnabled(false);
                break;
            }
            PostPanel postPanel = new PostPanel(buffer.poll(), userService, postService, interactionService);
            newPostPanelsBool.put(postPanel.getPost(), false);
            newPostPanels.put(postPanel.getPost(), postPanel);

            executor.getAsyncExecutor().execute(() -> {
                postPanel.loadPostPanel(ui);
                newPostPanelsBool.put(postPanel.getPost(), true);
            });
        }
        for(Map.Entry<Post, PostPanel> entry :  newPostPanels.entrySet()){
            while(!newPostPanelsBool.get(entry.getKey())){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            content.add(entry.getValue());
        }

        if(empty)
            content.add(new Text("No more posts available."));
        
        System.out.println("Outside addPosts");
    }

    /**
     * Loads posts from database.
     * @author Ziri Raha
     */
    private void loadPosts(){
        buffer.addAll(feedService.findNextNPosts());
        System.out.println(buffer.toString());
    }

    /**
     * Changes sorting of the feed. Also clears it.
     * @param st Sorting mode.
     * @author Ziri Raha
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
     * @author Ziri Raha
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
