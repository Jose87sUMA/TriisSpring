package com.example.application.views.feed;

import com.example.application.data.entities.Post;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import com.example.application.data.services.feed.FeedService.SortType;
import com.example.application.data.services.feed.FeedService;


import java.util.*;

public class FeedScroller extends Scroller {
    //public static final int ELEMENTS = 5;
    //enum SortType {RECENT, POPULAR}
    SortType current = null;

    MenuBar menu;
    Button loadMore;

    HashMap<SortType, Comparator<Post>> sorter = new HashMap<>();

    List<Post> allPosts = new ArrayList<>();
    PriorityQueue<Post> buffer;

    private final UserService userService;
    private final PostService postService;
    private final FeedService feedService;

    private VerticalLayout over = new VerticalLayout();
    private VerticalLayout content = new VerticalLayout();

    FeedScroller(FeedService feedService, UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
        this.feedService = feedService;

        this.feedService.initializeFeed();

        sorter.put(SortType.RECENT, Comparator.comparing(Post::getPostDate, Comparator.naturalOrder()));
        sorter.put(SortType.POPULAR, Comparator.comparing(Post::getLikes, Comparator.reverseOrder()));

        //this.setHeightFull();

/*
        getElement().executeJs("""
                    window.onscroll = function(ev) {
                                if ((window.innerHeight + window.scrollY) >= document.body.scrollHeight) {
                                    alert("you're at the bottom of the page");
                                }
                            };
                """);*/

        ComponentEventListener<ClickEvent<Button>> but = e -> {
            loadMore();
        };
        loadMore = new Button("Load More Posts", but);

        menu = new MenuBar();
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

        over.add(menu);

        over.setSpacing(true);
        over.addClassName(LumoUtility.AlignItems.CENTER);

        changeSorting(SortType.RECENT);

        content.setSpacing(true);
        content.addClassName(LumoUtility.AlignItems.CENTER);

        over.add(content);
        over.add(loadMore);

        this.setContent(over);
    }

    @ClientCallable
    void loadMore(){
        System.out.println("Loading More..."); //TODO: REMOVE PRINT
        addPosts();
        loadPosts();
    }

    void addPosts(){
        for(int i = 0; i < FeedService.ELEMENTS; i++){
            if (buffer.isEmpty()) {
                content.add(new Text("No more posts available."));
                loadMore.setEnabled(false);
                break;
            }
            content.add(new PostPanel(buffer.poll(), userService, postService));
        }
    }

    void loadPosts(){
        buffer.addAll(feedService.findNextNPosts());
        System.out.println(buffer.toString());
    }

    void reset(){
        loadMore.setEnabled(true);
        feedService.reset();
        content.removeAll();
        loadPosts();
        loadPosts();
    }

    void changeSorting(SortType st){
        if (current != st) {
            buffer = new PriorityQueue<>(sorter.get(st));
            feedService.setSort(st);
            reset();
            PriorityQueue<Post> aux = new PriorityQueue<>(sorter.get(st));
            aux.addAll(buffer);
            buffer = aux;
            addPosts();
            current = st;
        }
    }
}
