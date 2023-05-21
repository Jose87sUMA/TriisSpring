package com.example.application.views.about;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@PermitAll
public class AboutView extends VerticalLayout {

    public AboutView() {
        setSpacing(false);


        Label p1 = new Label("Type 1 points: \n" +
                                        "Earned by having other users repost your idea.\n" +
                                        "Used on creating posts. (One post costs #Followers * 3)\n" +
                                        "Possibility to spend on other things in the future (monetization).\n");

        Label p2 = new Label("Type 2 points: \n" +
                                        "Used to repost. Each repost gives the Original Poster 15 type 1 points, the person you reposted directly 10 points and everyone inbetween 5 points\n" +
                                        "Earned weekly to prevent addiction. You have 10 pointed reposts per week!\n");

        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        add(new H1("Point System"), p1, p2);

    }

}
