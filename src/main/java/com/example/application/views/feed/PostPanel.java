package com.example.application.views.feed;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;

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
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PostPanel extends VerticalLayout {

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

        this.content = postService.getContent(post);

        Float height = Float.parseFloat(content.getHeight().substring(0,content.getHeight().length()-2))+60;
        this.setHeight(height + "px");
        this.setWidth(content.getWidth());

        this.postHeader = new PostHeader(content.getWidth());
        this.interactionFooter = new InteractionFooter(content.getWidth(),post);
        this.commentSection = new CommentSection(content.getWidth(),post);

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

    protected class PostHeader extends HorizontalLayout {
        public PostHeader(String width) {

            this.setWidth(width);
            this.setHeight("35px");

            Avatar profileAvatar = new Avatar(poster.getUsername());
            profileAvatar.setImageResource(userService.getProfilePicImageResource(poster));

            Button profileName = new Button(poster.getUsername() + " - " + post.getPoints());
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

    protected class InteractionFooter extends HorizontalLayout {

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

                likeClick(post, authUser);

                if(!postService.getAllUsersLiking(post).contains(authUser)){
                    likeButton.setIcon(new Icon(VaadinIcon.HEART_O));
                }else{
                    likeButton.setIcon(new Icon(VaadinIcon.HEART));
                }
            });

            Button repostButton = new Button();

            Icon retweeted = new Icon(VaadinIcon.RETWEET);
            retweeted.setColor("springgreen");
            if(postService.isReposted(post, authUser)){
                repostButton.setIcon(retweeted);
            }else{
                repostButton.setIcon(new Icon(VaadinIcon.RETWEET));
            }

            repostButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
            repostButton.setHeight("25px");
            repostButton.setWidth(v + "px");

            repostButton.addClickListener(click -> {
                repostClick(post, authUser, repostButton);
            });

            Button commentButton = new Button(new Icon(VaadinIcon.COMMENT_O));
            commentButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
            commentButton.setHeight("25px");
            commentButton.setWidth(v + "px");
            commentButton.addClickListener(click -> {

                    if(commentSection.isVisible()){
                        content.setVisible(true);
                        postHeader.setVisible(true);
                        postHeader.addClassName(LumoUtility.Border.BOTTOM);
                        commentSection.setVisible(false);
                        commentButton.setIcon(new Icon(VaadinIcon.COMMENT_O));
                    }else{
                        content.setVisible(false);
                        postHeader.setVisible(false);
                        postHeader.addClassName(LumoUtility.Border.NONE);
                        commentSection.setVisible(true);
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

        public void likeClick(Post post, User authUser) {
            if(!postService.getAllUsersLiking(post).contains(authUser)){
                postService.newLike(authUser, post);
            }else{
                postService.dislike(authUser, post);
            }
            postService.save(post);
        }

        private void repostClick(Post post, User authUser, Button repostButton) {
            boolean reposted = postService.isReposted(post, authUser);
            repostButton.setEnabled(false);
            //Already reposted?
            if(!reposted) {
                repost(post, authUser, repostButton);
            }else{
                unrepost(post, authUser, repostButton);
            }
        }
        private void repost(Post post, User authUser, Button repostButton) {
            var ref = new Object() {
                boolean repostSuccess = true;
            };
            Notification repostSuccessNoti = new Notification("Reposted correctly.");
            repostSuccessNoti.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            repostSuccessNoti.setDuration(5000);

            //Post allows point investment?
            if(post.getPointed().equals("Y")){

                Notification usePoints = new Notification();
                usePoints.setDuration(-1);
                usePoints.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                Div statusText = new Div(new Text("Use points?"));

                Button yes = new Button("Yes");
                yes.addClickListener(event -> {
                    usePoints.close();

                    //Enough Points?
                    if(authUser.getType2Points().compareTo(BigInteger.valueOf(5)) >= 0){
                        Post newRepost = postService.save(new Post(post, authUser, true));
                        postService.pointDistribution(newRepost);
                        authUser.setType2Points(BigInteger.valueOf(authUser.getType2Points().intValue() - 5));
                        userService.save(authUser);
                        repostSuccessNoti.open();
                    }else{
                        Notification notEnough = new Notification("Not enough points for repost! Wait for next refill :)");
                        notEnough.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        notEnough.open();
                        ref.repostSuccess = false;
                    }
                    repostButton.setEnabled(true);
                });
                Button no = new Button("No");
                no.addClickListener(event -> {
                    usePoints.close();
                    postService.save(new Post(post, authUser, false));
                    repostSuccessNoti.open();
                    repostButton.setEnabled(true);
                });

                HorizontalLayout buttons = new HorizontalLayout(statusText, yes, no);
                buttons.setAlignItems(Alignment.CENTER);
                usePoints.add(buttons);
                usePoints.open();

            }else{
                postService.save(new Post(post, authUser, false));
                repostButton.setEnabled(true);
            }

            if(ref.repostSuccess){
                Icon icon = new Icon(VaadinIcon.RETWEET);
                icon.setColor("springgreen");
                repostButton.setIcon(icon);
            }
        }

        private void unrepost(Post post, User authUser, Button repostButton) {
            Notification deletePostCheck = new Notification();
            deletePostCheck.setDuration(-1);
            deletePostCheck.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            Div statusText = new Div(new Text("Are you sure you want to unrepost? You won't get any used points back :("));

            Button yes = new Button("Yes");
            yes.addClickListener(event -> {
                deletePostCheck.close();
                postService.deleteRepost(authUser, post);
                repostButton.setIcon(new Icon(VaadinIcon.RETWEET));
                Notification.show("Unreposted");
                repostButton.setEnabled(true);
            });
            Button no = new Button("No");
            no.addClickListener(event -> {
                deletePostCheck.close();
                repostButton.setEnabled(true);
            });
            HorizontalLayout buttons = new HorizontalLayout(statusText, yes, no);
            buttons.setAlignItems(Alignment.CENTER);
            deletePostCheck.add(buttons);
            deletePostCheck.open();
        }

    }

    protected class CommentSection extends VerticalLayout{
        MessageInput input;
        MessageList list;
        public CommentSection(String width, Post post){
            this.setWidth(width);
            //this.setHeight("190px");

            this.input = new MessageInput();
            this.list = new MessageList(postService.commentItems(post));
            list.setMaxHeight(Float.parseFloat(content.getHeight().substring(0,content.getHeight().length()-2))-50 + "px");

            this.input.addSubmitListener(submitEvent -> {
                User authUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
                postService.newComment(post, authUser, submitEvent.getValue());
                Notification.show("Commented " + submitEvent.getValue(), 2000, Notification.Position.BOTTOM_STRETCH);
                List<MessageListItem> commentItems = postService.commentItems(post);

                UI ui = UI.getCurrent();
                ui.access(() -> {
                    list.setItems(commentItems);
                    ui.push();
                });
            });
            input.setMaxHeight("70px");

            this.addClassName(LumoUtility.Border.TOP);
            this.addClassName(LumoUtility.BorderColor.CONTRAST_90);
            this.setAlignItems(FlexComponent.Alignment.CENTER);
            this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            setVisible(false);
            this.setSpacing(true);
            this.setPadding(true);
            this.add(list,input);

        }

    }

}
