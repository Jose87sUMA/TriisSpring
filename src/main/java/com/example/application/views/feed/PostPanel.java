package com.example.application.views.feed;

import com.example.application.data.entities.Like;
import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.*;
import com.vaadin.flow.component.notification.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.*;
import java.util.*;


public class PostPanel extends VerticalLayout {

    Post post;
    User poster;
    private final UserService userService;
    private final PostService postService;
    public PostPanel(Post post, UserService userService, PostService postService){

        this.post = post;
        this.poster = userService.findById(post.getUserId());
        this.userService = userService;
        this.postService = postService;

        Image content = postService.getContent(post);

        float height = Float.parseFloat(content.getHeight().substring(0,content.getHeight().length()-2))+60;
        this.setHeight(height + "px");
        this.setWidth(content.getWidth());

        PostHeader postHeader = new PostHeader(content.getWidth());
        InteractionFooter interactionFooter = new InteractionFooter(content.getWidth(),post);
        CommentSection commentSection = new CommentSection(content.getWidth(),post);
        this.addClassName(LumoUtility.Border.ALL);
        this.addClassName(LumoUtility.BorderColor.CONTRAST_90);
        this.addClassName(LumoUtility.BorderRadius.LARGE);
        this.addClassName(LumoUtility.BoxSizing.CONTENT);
        this.addClassName(LumoUtility.Padding.Left.NONE);
        this.addClassName(LumoUtility.Padding.Right.NONE);

        this.setSpacing(false);
        this.setAlignItems(FlexComponent.Alignment.CENTER);

        this.add(postHeader, content, interactionFooter,commentSection);

    }

    private class PostHeader extends HorizontalLayout {
        public PostHeader(String width) {

            this.setWidth(width);
            this.setHeight("35px");

            Avatar profileAvatar = new Avatar(poster.getUsername());
            profileAvatar.setImageResource(userService.getProfilePicImageResource(poster));

            Button profileName = new Button(poster.getUsername());
            profileName.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            profileName.setWidth("300px");
            profileName.setHeight("30px");

            this.addClassName(LumoUtility.Border.BOTTOM);
            this.addClassName(LumoUtility.BorderColor.CONTRAST_90);
            this.addClassName(LumoUtility.Padding.Top.NONE);
            this.addClassName(LumoUtility.AlignItems.CENTER);


            this.setSpacing(true);
            this.setPadding(true);

            this.add(profileAvatar, profileName);

        }
    }

    private class InteractionFooter extends HorizontalLayout {

        public InteractionFooter(String width, Post post) {

            User authUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

            this.setWidth(width);
            this.setHeight("25px");
            double v = Double.parseDouble(width.substring(0, width.length() - 2))/(3.5) ;

            Icon likeIcon;
            if(!postService.getAllUsersLiking(post).contains(authUser)){
                likeIcon = new Icon(VaadinIcon.HEART_O);
            }else{
                likeIcon = new Icon(VaadinIcon.HEART);
            }

            Button likeButton = new Button(likeIcon);
            likeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
            likeButton.setHeight("25px");
            likeButton.setWidth(v + "px");
            likeButton.addClickListener(click -> {
                postService.likeButton(post, authUser);

                if(!postService.getAllUsersLiking(post).contains(authUser)){
                    likeButton.setIcon(new Icon(VaadinIcon.HEART_O));
                }else{
                    likeButton.setIcon(new Icon(VaadinIcon.HEART));
                }
            });


            Button repostButton = new Button(new Icon(VaadinIcon.RETWEET));
            repostButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
            repostButton.setHeight("25px");
            repostButton.setWidth(v + "px");
            repostButton.addClickListener(click -> {
               Notification.show("Repost");
            });

            Button commentButton = new Button(new Icon(VaadinIcon.COMMENT_O));
            commentButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
            commentButton.setHeight("25px");
            commentButton.setWidth(v + "px");
            commentButton.addClickListener(click -> {


            });


            this.addClassName(LumoUtility.Border.TOP);
            this.addClassName(LumoUtility.BorderColor.CONTRAST_90);
            this.setAlignItems(FlexComponent.Alignment.CENTER);
            this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

            this.setSpacing(true);
            this.setPadding(true);
            this.add(likeButton, repostButton, commentButton);

        }

    }

    private class CommentSection extends VerticalLayout{
        public CommentSection(String width, Post post){
            this.setWidth(width);
            this.setHeight("125px");

            MessageInput input = new MessageInput();
            MessageList list = new MessageList();


            List<User> usersLiking = postService.getAllUsersLiking(post);
            for(User u: usersLiking){
                list.setItems();
            }

            input.addSubmitListener(submitEvent -> {
                Notification.show("Message received: " + submitEvent.getValue(),
                        3000, Notification.Position.MIDDLE);
            });



            add(list);
            add(input);
        }
    }



}
