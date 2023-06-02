package com.example.application.views.leaderboards;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.services.InteractionService;
import com.example.application.services.LeaderboardService;
import com.example.application.services.PostService;
import com.example.application.services.UserService;
import com.example.application.services.threads.SpringAsyncConfig;
import com.example.application.views.feed.postPanel.PostPanel;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that manages a particular leaderboard (distinguished by leaderboardType).
 * Functions as FeedScroller.
 */
public class LeaderboardScroller extends VerticalLayout {

    private final UserService userService;
    private final PostService postService;
    private final InteractionService interactionService;
    private final LeaderboardService leaderboardService;
    private final LeaderboardService.LeaderboardType leaderboardType;

    private SpringAsyncConfig executor = new SpringAsyncConfig();
    private UI ui;

    /**
     * the list of posts that appear on the top 10
     */
    private List<Post> posts;
    /**
     * the list of users that appear on the top 10
     */
    private List<User> users;
    private VerticalLayout content = new VerticalLayout();

    /**
     * Calls  a method which initializes the vertical layout CONTENT with necessary components
     *
     * @param leaderboardType    the selected top 10: posts (select by time span) or users
     * @param userService
     * @param interactionService
     * @param leaderboardService
     * @author Ksenia Myakisheva
     */
    LeaderboardScroller(LeaderboardService.LeaderboardType leaderboardType, UserService userService, PostService postService, InteractionService interactionService, LeaderboardService leaderboardService, UI ui) {
        this.userService = userService;
        this.postService =postService;
        this.interactionService = interactionService;
        this.leaderboardService = leaderboardService;
        this.leaderboardType = leaderboardType;
        this.ui = ui;

        this.addClassName(LumoUtility.AlignItems.CENTER);
        content.setSpacing(true);
        content.addClassName(LumoUtility.AlignItems.CENTER);
        addResult();
        this.add(content);
    }

    /**
     * According to leaderboardType, perform a query to select the top ten and add them to CONTENT
     * @author Ksenia Myakisheva
     */
    private void addResult(){
        switch (leaderboardType){
            case TODAY -> posts = leaderboardService.findTenByPointedOriginalPostIdOrderByPointsDescCreatedToday();
            case THIS_WEEK -> posts = leaderboardService.findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisWeek();
            case THIS_MONTH -> posts = leaderboardService.findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisMonth();
            case THIS_YEAR -> posts = leaderboardService.findTenByPointedOriginalPostIdOrderByPointsDescCreatedThisYear();
            case ALL_TIME -> posts = leaderboardService.findTenByPointedOriginalPostIdOrderByPointsDesc();
            case USERS -> users = leaderboardService.findUsersHighestType1Points();
        }

        if(posts != null && posts.size() != 0){
            Map<Integer, PostPanel> newPostPanels = new HashMap<>();
            Map<Integer, Boolean> postIsReady = new HashMap<>();

            for(int i = 0; i < Math.min(10, posts.size()); i++){
                PostPanel postPanel = new PostPanel(posts.get(i), userService, postService, interactionService);
                newPostPanels.put(i, postPanel);
                postIsReady.put(i, false);

                int finalI = i;
                executor.getAsyncExecutor().execute(() -> {
                    postPanel.loadPostPanel(ui);
                    postIsReady.put(finalI, true);
                });
            }
            for(Map.Entry<Integer, PostPanel> entry :  newPostPanels.entrySet()){
                while(!postIsReady.get(entry.getKey())){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                H4 position = new H4 ((entry.getKey()+1) + " Position");
                if(entry.getKey() == 0){
                    position.getStyle().set("color", "#FFD700");
                }else if (entry.getKey() == 1){
                    position.getStyle().set("color", "#C0C0C0");
                } else if (entry.getKey() == 2) {
                    position.getStyle().set("color", "#CD7F32");
                }
                content.add(position, entry.getValue());
            }

        }else if(users != null && users.size() != 0){
            Button buttonUser[] = new Button[users.size()];
            for(int i = 0; i < Math.min(10, users.size()); i++){
                buttonUser[i] = new Button(users.get(i).getUsername() + ":   " + users.get(i).getType1Points() + " points");
                buttonUser[i].setWidth("100%");
                //button[i].addClickListener(UI.getCurrent().navigate(ProfileView.class, username));
                H4 position = new H4 ((i+1) + " Position");
                if(i == 0){
                    position.getStyle().set("color", "#FFD700");
                }else if (i == 1){
                    position.getStyle().set("color", "#C0C0C0");
                } else if (i == 2) {
                    position.getStyle().set("color", "#CD7F32");
                }
                content.add(position, buttonUser[i]);
            }
        }else{
            content.add(new H4("Competition is tight isn't it? D;"));
        }

    }


}
