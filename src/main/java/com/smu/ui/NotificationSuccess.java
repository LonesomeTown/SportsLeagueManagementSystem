package com.smu.ui;

import com.smu.util.VaadinUtils;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.Route;

@Route("notification-success")
public class NotificationSuccess extends Div {

    public NotificationSuccess(String message) {
        show(message);
    }

    public void show(String message) {
        VaadinUtils vaadinUtils = new VaadinUtils();
        vaadinUtils.builtNotification(message, NotificationVariant.LUMO_SUCCESS);
    }

}