package com.smu.ui.league;

import com.smu.dto.League;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

/**
 * LeagueForm
 *
 * @author T.W 11/4/22
 */
public class LeagueForm extends FormLayout {
    TextField name = new TextField("League Name");
    TextField commissionerName = new TextField("Commissioner name");
    TextField commissionerSsn = new TextField("Commissioner SSN");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<League> binder = new BeanValidationBinder<>(League.class);

    League league;

    public LeagueForm() {
        addClassName("contact-form");

        name.setRequiredIndicatorVisible(true);
        name.setErrorMessage("This field is required");
        commissionerName.setRequiredIndicatorVisible(true);
        commissionerName.setErrorMessage("This field is required");
        commissionerSsn.setRequiredIndicatorVisible(true);
        commissionerSsn.setErrorMessage("This field is required");

        binder.bindInstanceFields(this);

        add(name,
                commissionerName,
                commissionerSsn,
                createButtonsLayout());
    }

    // Events
    public abstract static class ContactFormEvent extends ComponentEvent<LeagueForm> {
        private final League league;

        protected ContactFormEvent(LeagueForm source, League league) {
            super(source, false);
            this.league = league;
        }

        public League getLeague() {
            return league;
        }
    }

    public void setLeague(League league) {
        this.league = league;
        binder.readBean(league);
    }

    public static class SaveEvent extends ContactFormEvent {
        SaveEvent(LeagueForm source, League league) {
            super(source, league);
        }
    }

    public static class DeleteEvent extends ContactFormEvent {
        DeleteEvent(LeagueForm source, League league) {
            super(source, league);
        }

    }

    public static class CloseEvent extends ContactFormEvent {
        CloseEvent(LeagueForm source) {
            super(source, null);
        }
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, league)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(league);
            fireEvent(new SaveEvent(this, league));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
}
