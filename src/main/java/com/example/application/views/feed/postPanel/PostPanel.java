package com.example.application.views.feed.postPanel;
import com.example.application.services.InteractionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.services.PostService;
import com.example.application.services.UserService;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.CompletableFuture;

/**
 * Represents the post panel that is displayed on the feed.
 * Contains a header with information, a footer for actions and the post's content.
 */
public class PostPanel extends VerticalLayout {

    private final User authenticatedUser;
    private final Post post;
    private final User poster;

    private final UserService userService;
    private final PostService postService;
    private final InteractionService interactionService;

    private PostHeader postHeader;
    private Image content;
    private InteractionFooter interactionFooter;
    private CommentSection commentSection;

    /**
     *
     * @param post
     * @param userService
     * @param postService
     * @param interactionService
     */
    public PostPanel(Post post, UserService userService, PostService postService, InteractionService interactionService){

        this.post = post;
        this.interactionService = interactionService;
        this.poster = userService.findById(post.getUserId());
        this.userService = userService;
        this.postService = postService;
        this.authenticatedUser =  userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println("Outside constructor: " + post.getPostId());

    }

    /**
     * It downloads the post's content from dropbox and sets it as whe post panel content to be displayed.
     * This is done separately from the constructor, so several downloads can be performed at the same time.
     *
     * @param ui needed to work with Vaadin's Images
     * @return
     * @author José Alejandro Sarmiento
     */
    @Async
    public CompletableFuture<Void> loadPostPanel(UI ui){
        this.content = postService.getContent(post, ui);

        Float height = Float.parseFloat(content.getHeight().substring(0,content.getHeight().length()-2))+60;
        ui.access(() -> {
            this.setHeight(height + "px");
            this.setWidth(content.getWidth());
            this.addClassName(LumoUtility.Border.ALL);
            this.addClassName(LumoUtility.BorderColor.CONTRAST_90);
            this.addClassName(LumoUtility.BorderRadius.LARGE);
            this.addClassName(LumoUtility.BoxSizing.CONTENT);
            this.addClassName(LumoUtility.Padding.Left.NONE);
            this.addClassName(LumoUtility.Padding.Right.NONE);

            this.setSpacing(false);
            this.setAlignItems(FlexComponent.Alignment.CENTER);
        });



        this.postHeader = new PostHeader(content.getWidth(), postService, userService, poster, authenticatedUser, post);
        this.interactionFooter = new InteractionFooter(content.getWidth(),post, postService, interactionService, authenticatedUser, this);
        this.commentSection = new CommentSection(content.getWidth(),post, postService, userService, this, interactionService);

        this.add(postHeader, content, interactionFooter, commentSection);
        System.out.println("Finished load: " + post.getPostId());

        return CompletableFuture.completedFuture(null);
    }

    /**
     * @return The post associated with the post panel
     * @author José Alejandro Sarmiento
     */
    public Post getPost() {
        return post;
    }

    /**
     * @return The comment section associated with the post panel
     * @author José Alejandro Sarmiento
     */
    public CommentSection getCommentSection() {
        return commentSection;
    }

    /**
     * @return The Image associated with the post panel
     * @author José Alejandro Sarmiento
     */
    public Image getContent() {
        return content;
    }

    /**
     * @return The Post Header associated with the post panel
     * @author José Alejandro Sarmiento
     */
    public PostHeader getPostHeader() {
        return postHeader;
    }

    /**
     * @return The interaction footer associated with the post panel
     * @author José Alejandro Sarmiento
     */
    public InteractionFooter getInteractionFooter() {
        return interactionFooter;
    }
}
