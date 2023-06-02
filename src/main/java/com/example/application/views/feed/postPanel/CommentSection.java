package com.example.application.views.feed.postPanel;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.services.InteractionService;
import com.example.application.services.PostService;
import com.example.application.services.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * The comment section is conformed by MessageList and a MessageInput, both are added to a Vertical layout.
 * The MessageInput includes the text field and a send button. The MessageList needs a list of MessageListItems to be filled,
 * which is retrieved using the commentItems method from the postService.
 *
 * @author Daniel de los Ríos García
 */
public class CommentSection extends VerticalLayout {
    private MessageInput input;
    private MessageList list;

    private final PostService postService;
    private final UserService userService;
    private final PostPanel postPanel;
    private final InteractionService interactionService;

    /**
     *
     * @param width
     * @param post
     * @param postService
     * @param userService
     * @param postPanel
     * @param interactionService
     */
    public CommentSection(String width, Post post, PostService postService, UserService userService, PostPanel postPanel, InteractionService interactionService){

        this.postService = postService;
        this.userService = userService;
        this.postPanel = postPanel;
        this.interactionService = interactionService;
        this.setWidth(width);
        //this.setHeight("190px");

        this.input = new MessageInput();
        this.list = new MessageList(this.interactionService.commentItems(post));
        list.setMaxHeight(Float.parseFloat(postPanel.getContent().getHeight().substring(0, postPanel.getContent().getHeight().length()-2))-50 + "px");

        this.input.addSubmitListener(submitEvent -> {

            User authenticatedUser = this.userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            this.interactionService.newComment(post, authenticatedUser, submitEvent.getValue());
            Notification.show("Commented " + submitEvent.getValue(), 2000, Notification.Position.BOTTOM_STRETCH);
            List<MessageListItem> commentItems = this.interactionService.commentItems(post);

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