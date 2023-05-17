package com.example.application.views.feed.postPanel;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class InteractionFooter extends HorizontalLayout {

    private final PostService postService;
    private final User authenticatedUser;
    private final PostPanel postPanel;
    public InteractionFooter(String width, Post post, PostService postService, User authenticatedUser, PostPanel postPanel) {
        this.postService = postService;
        this.authenticatedUser = authenticatedUser;
        this.postPanel = postPanel;

        this.setWidth(width);
        this.setHeight("25px");
        double v = Double.parseDouble(width.substring(0, width.length() - 2)) / (3.5);

        Icon likeIcon;
        if (!this.postService.getAllUsersLiking(post).contains(this.authenticatedUser)) {
            likeIcon = new Icon(VaadinIcon.HEART_O);
        } else {
            likeIcon = new Icon(VaadinIcon.HEART);
            likeIcon.setColor("red");
        }

        Button likeButton = new Button(likeIcon);
        likeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
        likeButton.setHeight("25px");
        likeButton.setWidth(v + "px");
        likeButton.addClickListener(click -> {

            likeButton.setEnabled(false);
            likeClick(post, this.authenticatedUser, likeButton);
            likeButton.setEnabled(true);

        });

        Button repostButton = new Button();

        Icon retweeted = new Icon(VaadinIcon.RETWEET);
        retweeted.setColor("springgreen");
        if (this.postService.isReposted(post, this.authenticatedUser)) {
            repostButton.setIcon(retweeted);
        } else {
            repostButton.setIcon(new Icon(VaadinIcon.RETWEET));
        }

        repostButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
        repostButton.setHeight("25px");
        repostButton.setWidth(v + "px");

        repostButton.addClickListener(click -> {
            repostClick(post, this.authenticatedUser, repostButton);
        });

        Button commentButton = new Button(new Icon(VaadinIcon.COMMENT_O));
        commentButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
        commentButton.setHeight("25px");
        commentButton.setWidth(v + "px");
        commentButton.addClickListener(click -> {

            if (postPanel.commentSection.isVisible()) {
                postPanel.content.setVisible(true);
                postPanel.postHeader.setVisible(true);
                postPanel.postHeader.addClassName(LumoUtility.Border.BOTTOM);
                postPanel.commentSection.setVisible(false);
                commentButton.setIcon(new Icon(VaadinIcon.COMMENT_O));
            } else {
                postPanel.content.setVisible(false);
                postPanel.postHeader.setVisible(false);
                postPanel.postHeader.addClassName(LumoUtility.Border.NONE);
                postPanel.commentSection.setVisible(true);
                commentButton.setIcon(new Icon(VaadinIcon.COMMENT));
            }

        });


        this.addClassName(LumoUtility.Border.TOP);
        this.addClassName(LumoUtility.BorderColor.CONTRAST_90);
        this.setAlignItems(FlexComponent.Alignment.CENTER);
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        this.setSpacing(true);
        this.setPadding(true);
        this.add(likeButton, repostButton, commentButton);
    }

    /**
     * Actioned when the like button has been pressed. This method adds or subtracts a like to the post.
     * @param post
     * @param authUser
     * @param likeButton
     */
    public void likeClick(Post post, User authUser, Button likeButton) {
        if (!postService.getAllUsersLiking(post).contains(authUser)) {
            postService.newLike(authUser, post);
            Icon redHeart = new Icon(VaadinIcon.HEART);
            redHeart.setColor("red");
            likeButton.setIcon(redHeart);
        } else {
            postService.dislike(authenticatedUser, post);
            likeButton.setIcon(new Icon(VaadinIcon.HEART_O));
        }
        postService.save(post);
    }

    /**
     * Actioned when the repost button has been pressed. This method adds or deletes a repost for the user.
     * @param post
     * @param authUser
     * @param repostButton
     */
    public void repostClick(Post post, User authUser, Button repostButton) {
        boolean reposted = postService.isReposted(post, authUser);
        repostButton.setEnabled(false);
        //Already reposted?
        if (!reposted) {
            postService.repost(post, authenticatedUser, repostButton);
        } else {
            postService.unrepost(post, authenticatedUser, repostButton);
        }
        System.out.println("main");

    }
}