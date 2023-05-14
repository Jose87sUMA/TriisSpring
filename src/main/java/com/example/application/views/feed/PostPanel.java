package com.example.application.views.feed;
import com.example.application.data.entities.PostsPointLog;
import com.example.application.data.entities.Role;
import com.example.application.data.repositories.PostPointLogRepository;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.services.PostService;
import com.example.application.data.services.UserService;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NegativeOrZero;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Executor;

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
            profileName.setWidth("270px");
            profileName.setHeight("30px");
            profileName.addClickListener(e ->
                    profileName.getUI().ifPresent(ui ->
                            ui.navigate("profile/" + poster.getUsername()))
            );

            this.addClassName(LumoUtility.Border.BOTTOM);
            this.addClassName(LumoUtility.BorderColor.CONTRAST_90);
            this.addClassName(LumoUtility.Padding.Top.NONE);
            this.addClassName(LumoUtility.AlignItems.CENTER);


            this.setSpacing(true);
            this.setPadding(true);

            this.add(profileAvatar, profileName, createPostMenuLayout());

        }
    }


    private MenuBar createPostMenuLayout(){
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        MenuItem options = menuBar.addItem(new H3("..."));
        SubMenu subItems = options.getSubMenu();
        if(poster.equals(authenticatedUser) || authenticatedUser.getRoles().contains(Role.ADMIN)){
            MenuItem delete = subItems.addItem("Delete");
            MenuItem statistics = subItems.addItem("View statistics");

            delete.addClickListener(e -> {
                    createConfirmDelete().open();
            });

            statistics.addClickListener(e -> {
                createStatisticsLayout().open();
            });

        }else{
            MenuItem report = subItems.addItem("Report");
            report.addClickListener(e -> {
                createReportLayout().open();
            });


        }

        return menuBar;
    }

    private ConfirmDialog createReportLayout(){
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setWidth("475px");
        dialog.setHeader("Report?");

        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setLabel("Reason for reporting");
        radioGroup.setItems("Violence", "Sexual content", "Discriminatory content", "Other (write your reason)");
        TextField other = new TextField("Other");
        dialog.add(radioGroup, other);

        dialog.setCancelable(true);
        dialog.setConfirmText("Report");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(e -> {
            String reason;
            if(radioGroup.getValue().equals("Other (write your reason)")){
                reason = other.getValue();
            }else{
                reason = radioGroup.getValue();
            }
            postService.newReport(authenticatedUser, post, reason);
            Notification.show("Report successful");
        });
        return dialog;

    }

    private ConfirmDialog createConfirmDelete(){

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete?");
        dialog.setText(
                "Are you sure you want to permanently delete this item?");

        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> {
            //postService.deletePost(post);
            this.removeFromParent();
        });
        return dialog;
    }

    private ConfirmDialog createStatisticsLayout(){

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setWidth("475px");
        dialog.setHeader("Point Statistics");
        dialog.setCancelable(true);
        dialog.setConfirmText("Ok");
        dialog.setCancelText("Close");

        VerticalLayout directRepostersLayout = new VerticalLayout();

        int directPoints = 0;
        int indirectPoints = 0;

        List<PostsPointLog> postLogs = postService.findAllLogsByPost(post);

        for(PostsPointLog postLog : postLogs){

            if(postLog.isDirect()){

                String username = userService.findById(postLog.getUserId()).getUsername();
                Label profileName = new Label(username + " - " + 15);
                directPoints += postLog.getPoints().intValue();
                directRepostersLayout.add(profileName);

            }else{
                indirectPoints += postLog.getPoints().intValue();
            }

        }


        Notification directPointsInfoNot = new Notification("Points from people that reposted you directly");
        Notification indirectPointsInfoNot = new Notification("Points from people that reposted your direct reposters");

        Icon directPointsInfoIcon = new Icon(VaadinIcon.QUESTION_CIRCLE_O);
        directPointsInfoIcon.setSize("17px");

        Icon indirectPointsInfoIcon = new Icon(VaadinIcon.QUESTION_CIRCLE_O);
        indirectPointsInfoIcon.setSize("17px");

        directPointsInfoIcon.getElement().addEventListener("mouseover", e -> directPointsInfoNot.open());
        indirectPointsInfoIcon.getElement().addEventListener("mouseover", e -> indirectPointsInfoNot.open());

        directPointsInfoIcon.getElement().addEventListener("mouseout", e -> directPointsInfoNot.close());
        indirectPointsInfoIcon.getElement().addEventListener("mouseout", e -> indirectPointsInfoNot.close());

        HorizontalLayout directLayout = new HorizontalLayout(new Details("Direct Points: " + directPoints, directRepostersLayout), directPointsInfoIcon);
        directLayout.setAlignItems(Alignment.BASELINE);

        HorizontalLayout indirectLayout = new HorizontalLayout(new Label("Indirect Points: " + indirectPoints), indirectPointsInfoIcon);
        directLayout.setAlignItems(Alignment.BASELINE);

        dialog.add(directLayout, indirectLayout, directPointsInfoNot, indirectPointsInfoNot);
        return dialog;

    }


    protected class InteractionFooter extends HorizontalLayout {

        public InteractionFooter(String width, Post post) {

            this.setWidth(width);
            this.setHeight("25px");
            double v = Double.parseDouble(width.substring(0, width.length() - 2)) / (3.5);

            Icon likeIcon;
            if (!postService.getAllUsersLiking(post).contains(authenticatedUser)) {
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
                likeClick(post, authenticatedUser, likeButton);
                likeButton.setEnabled(true);

            });

            Button repostButton = new Button();

            Icon retweeted = new Icon(VaadinIcon.RETWEET);
            retweeted.setColor("springgreen");
            if (postService.isReposted(post, authenticatedUser)) {
                repostButton.setIcon(retweeted);
            } else {
                repostButton.setIcon(new Icon(VaadinIcon.RETWEET));
            }

            repostButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
            repostButton.setHeight("25px");
            repostButton.setWidth(v + "px");

            repostButton.addClickListener(click -> {
                repostClick(post, authenticatedUser, repostButton);
            });

            Button commentButton = new Button(new Icon(VaadinIcon.COMMENT_O));
            commentButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
            commentButton.setHeight("25px");
            commentButton.setWidth(v + "px");
            commentButton.addClickListener(click -> {

                if (commentSection.isVisible()) {
                    content.setVisible(true);
                    postHeader.setVisible(true);
                    postHeader.addClassName(LumoUtility.Border.BOTTOM);
                    commentSection.setVisible(false);
                    commentButton.setIcon(new Icon(VaadinIcon.COMMENT_O));
                } else {
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
        private void repostClick(Post post, User authUser, Button repostButton) {
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


    /**
     * The comment section is conformed by MessageList and a MessageInput, both are added to a Vertical layout.
     * The MessageInput includes the text field and a send button. The MessageList needs a list of MessageListItems to be filled,
     * which is retrieved using the commentItems method from the postService.
     */
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
                User authenticatedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
                postService.newComment(post, authenticatedUser, submitEvent.getValue());
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
