package com.example.application.views.leaderboards;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.LeaderboardService.SortType;
import com.example.application.data.services.LeaderboardService;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.example.application.views.feed.PostPanel;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

public class LeaderboardScroller extends VerticalLayout {
    SortType current = null;

    Button loadMore;

    final Map<SortType, Comparator<Post>> sorter = Map.of(SortType.SORT_BY_POST, Comparator.comparing(Post::getPoints, Comparator.reverseOrder()));

    PriorityQueue<Post> buffer;

    private final UserService userService;
    private final PostService postService;
    private final LeaderboardService leaderboardService;

    private VerticalLayout content = new VerticalLayout();

    LeaderboardScroller(LeaderboardService.LeaderboardType leaderboardType, User authenticatedUser, UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
        this.leaderboardService = this.postService.getLeaderboardService(leaderboardType, authenticatedUser.getUserId());

        this.leaderboardService.initializeLeaderboard();



        ComponentEventListener<ClickEvent<Button>> but = e -> {
            loadMore();
        };
        loadMore = new Button("Load More", but);

        MenuBar menu = new MenuBar();
        MenuItem sortChooser = menu.addItem("Sorting By Users"); //Sorting By Popular
        ComponentEventListener<ClickEvent<MenuItem>> listener = e -> {
            if (e.getSource().getText().equals("Change to Users")){

                sortChooser.setText("Sorting By Users"); // Sorting By Recent
            }else if (e.getSource().getText().equals("Change to Posts")){
                this.changeSorting(SortType.SORT_BY_POST);
                sortChooser.setText("Sorting By Posts"); //Sorting By Popular
            }
        };
        SubMenu subMenuSort = sortChooser.getSubMenu();
        subMenuSort.addItem("Change to Users", listener); //recent
        subMenuSort.addItem("Change to Posts", listener); //popular
        menu.setOpenOnHover(true);

        this.add(menu);

        this.setSpacing(true);

        this.addClassName(LumoUtility.AlignItems.CENTER);

        changeSorting(SortType.SORT_BY_POST); //por probar

        content.setSpacing(true);
        content.addClassName(LumoUtility.AlignItems.CENTER);

        this.add(content);
        this.add(loadMore);
    }

    @ClientCallable
    void loadMore(){
        if (loadMore.isEnabled()) {
            addPosts();
            loadPosts();
        }
    }

    void addPosts(){
        for(int i = 0; i < LeaderboardService.ELEMENTS; i++){
            if (buffer.isEmpty()) {
                content.add(new Text("No more posts available."));
                loadMore.setEnabled(false);
                break;
            }
            content.add(new PostPanel(buffer.poll(), userService, postService));
        }
    }

    void loadPosts(){
        buffer.addAll(leaderboardService.findNextNPosts());
        System.out.println(buffer.toString());
    }

    void reset(){
        loadMore.setEnabled(true);
        leaderboardService.reset();
        content.removeAll();
        loadPosts();
        loadPosts();
    }

    void changeSorting(SortType st){
        if (current != st) {
            current = st;
            leaderboardService.setSort(st);
            refresh();
        }
    }

    void refresh(){
        buffer = new PriorityQueue<>(sorter.get(current));
        reset();
        PriorityQueue<Post> aux = new PriorityQueue<>(sorter.get(current));
        aux.addAll(buffer);
        buffer = aux;
        addPosts();
    }
}
