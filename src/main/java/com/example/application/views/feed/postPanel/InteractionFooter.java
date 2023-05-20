package com.example.application.views.feed.postPanel;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.exceptions.PostException;
import com.example.application.services.InteractionService;
import com.example.application.services.PostService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class InteractionFooter extends HorizontalLayout {

    private final PostService postService;
    private final InteractionService interactionService;
    private final User authenticatedUser;
    private final PostPanel postPanel;
    private final Post post;

    private final Button likeButton;
    private final Button repostButton;
    private final Button commentButton;

    public InteractionFooter(String width, Post post, PostService postService, InteractionService interactionService, User authenticatedUser, PostPanel postPanel) {
        this.postService = postService;
        this.interactionService = interactionService;
        this.authenticatedUser = authenticatedUser;
        this.postPanel = postPanel;
        this.post = post;

        this.setWidth(width);
        this.setHeight("25px");
        double v = Double.parseDouble(width.substring(0, width.length() - 2)) / (3.5);

        Icon likeIcon;
        if (!interactionService.getAllUsersLiking(post).contains(this.authenticatedUser)) {
            likeIcon = new Icon(VaadinIcon.HEART_O);
        } else {
            likeIcon = new Icon(VaadinIcon.HEART);
            likeIcon.setColor("red");
        }

        likeButton = new Button(post.getLikes()+"", likeIcon);
        likeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
        likeButton.setHeight("25px");
        likeButton.setWidth(v + "px");
        likeButton.addClickListener(click -> {

            likeButton.setEnabled(false);
            likeClick();
            likeButton.setEnabled(true);

        });

        repostButton = new Button(interactionService.getAllReposts(post)+"");

        Icon retweeted = new Icon(VaadinIcon.RETWEET);
        retweeted.setColor("springgreen");
        if (interactionService.isReposted(post, this.authenticatedUser)) {
            repostButton.setIcon(retweeted);
        } else {
            repostButton.setIcon(new Icon(VaadinIcon.RETWEET));
        }

        repostButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
        repostButton.setHeight("25px");
        repostButton.setWidth(v + "px");

        repostButton.addClickListener(click -> {
            repostClick();
        });

        if(post.getUserId() == authenticatedUser.getUserId()){
            repostButton.setEnabled(false);
        }


        commentButton = new Button(interactionService.getAllComments(post)+"",new Icon(VaadinIcon.COMMENT_O));
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
     */
    public void likeClick() {

        if (!interactionService.getAllUsersLiking(post).contains(authenticatedUser)) {
            interactionService.newLike(authenticatedUser, post);
            Icon redHeart = new Icon(VaadinIcon.HEART);
            redHeart.setColor("red");
            likeButton.setIcon(redHeart);
        } else {
            interactionService.dislike(authenticatedUser, post);
            likeButton.setIcon(new Icon(VaadinIcon.HEART_O));
        }
        likeButton.setText(post.getLikes()+"");
        postService.save(post);
    }

    /**
     * Actioned when the repost button has been pressed. This method adds or deletes a repost for the user.
     */
    public void repostClick() {
        boolean reposted = interactionService.isReposted(post, authenticatedUser);
        repostButton.setEnabled(false);
        //Already reposted?
        if (!reposted) {
            repost();
        } else {
            unrepost();
        }
    }

    public void repost() {

        if(post.getPointed().equals("Y")){

            Notification usePoints = new Notification();
            usePoints.setDuration(-1);
            usePoints.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            Div statusText = new Div(new Text("Use points?"));

            Button yes = new Button("Yes");
            yes.addClickListener(event -> {
                usePoints.close();
                try{
                    interactionService.pointedRepost(post, authenticatedUser);
                    postPanel.postHeader.refreshPoints(postService.findById(post.getPostId()).getPoints()); //Post with new points
                    repostSuccess();
                } catch (PostException e) {
                    Notification notEnough = new Notification(e.getMessage());
                    notEnough.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notEnough.setDuration(5000);
                    notEnough.open();
                    return;
                }finally {
                    repostButton.setEnabled(true);
                }
            });
            Button no = new Button("No");
            no.addClickListener(event -> {
                usePoints.close();
                interactionService.notPointedRepost(post, authenticatedUser);
                repostSuccess();
                repostButton.setEnabled(true);
            });

            HorizontalLayout buttons = new HorizontalLayout(statusText, yes, no);
            buttons.setAlignItems(FlexComponent.Alignment.CENTER);
            usePoints.add(buttons);

            usePoints.open();

        }else{
            interactionService.notPointedRepost(post, authenticatedUser);
            repostSuccess();
            repostButton.setEnabled(true);
        }

    }

    public void repostSuccess(){

        Notification notification = new Notification("Successful repost!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setDuration(5000);
        notification.open();

        Icon icon = new Icon(VaadinIcon.RETWEET);
        icon.setColor("springgreen");
        repostButton.setIcon(icon);

        refreshRepostCount();

    }

    private void refreshRepostCount() {
        repostButton.setText(interactionService.getAllReposts(post)+"");
    }

    private void unrepost(){

        Notification deletePostCheck = new Notification();
        deletePostCheck.setDuration(-1);
        deletePostCheck.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        Div statusText = new Div(new Text("Are you sure you want to unrepost? You won't get any used points back :("));

        Button yes = new Button("Yes");
        yes.addClickListener(event -> {

            interactionService.deleteRepost(authenticatedUser, post);
            repostButton.setIcon(new Icon(VaadinIcon.RETWEET));
            refreshRepostCount();
            deletePostCheck.close();
            repostButton.setEnabled(true);

            Notification.show("Unreposted");
        });
        Button no = new Button("No");
        no.addClickListener(event -> {
            deletePostCheck.close();
            repostButton.setEnabled(true);
        });
        HorizontalLayout buttons = new HorizontalLayout(statusText, yes, no);
        buttons.setAlignItems(FlexComponent.Alignment.CENTER);
        deletePostCheck.add(buttons);

        deletePostCheck.open();

    }
}
