package com.example.application.views.feed.postPanel;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.CompletableFuture;


public class PostPanel extends VerticalLayout {

    private final User authenticatedUser;
    Post post;
    User poster;
    private final UserService userService;
    private final PostService postService;
    PostHeader postHeader;
    Image content;
    InteractionFooter interactionFooter;
    CommentSection commentSection;

    public PostPanel(Post post, UserService userService, PostService postService){

        this.post = post;
        this.poster = userService.findById(post.getUserId());
        this.userService = userService;
        this.postService = postService;
        this.authenticatedUser =  userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println("Outside constructor: " + post.getPostId());

    }

    @Async
    public CompletableFuture<Void> loadPostPanel(UI ui){
        this.content = postService.getContent(post, ui);

        Float height = Float.parseFloat(content.getHeight().substring(0,content.getHeight().length()-2))+60;
        this.setHeight(height + "px");
        this.setWidth(content.getWidth());

        this.postHeader = new PostHeader(content.getWidth(), postService, userService, poster, authenticatedUser, post);
        this.interactionFooter = new InteractionFooter(content.getWidth(),post, postService, authenticatedUser, this);
        this.commentSection = new CommentSection(content.getWidth(),post, postService, userService, this);

        this.addClassName(LumoUtility.Border.ALL);
        this.addClassName(LumoUtility.BorderColor.CONTRAST_90);
        this.addClassName(LumoUtility.BorderRadius.LARGE);
        this.addClassName(LumoUtility.BoxSizing.CONTENT);
        this.addClassName(LumoUtility.Padding.Left.NONE);
        this.addClassName(LumoUtility.Padding.Right.NONE);

        this.setSpacing(false);
        this.setAlignItems(FlexComponent.Alignment.CENTER);

        this.add(postHeader, content, interactionFooter, commentSection);
        System.out.println("Finished load: " + post.getPostId());

        return CompletableFuture.completedFuture(null);
    }

    public Post getPost() {
        return post;
    }

}
