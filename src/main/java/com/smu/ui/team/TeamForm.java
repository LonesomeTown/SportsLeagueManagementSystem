package com.smu.ui.team;

import com.smu.dto.Team;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.util.List;

/**
 * LeagueForm
 *
 * @author T.W 11/4/22
 */
public class TeamForm extends FormLayout {
    TextField name = new TextField("Team Name");
    TextField city = new TextField("City of Team");
    TextField field = new TextField("Field of team");
    ComboBox<String> leagueName = new ComboBox<>("League Name");
    NumberField rating = new NumberField("Rating");


    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Team> binder = new BeanValidationBinder<>(Team.class);

    Team team;

    public TeamForm(List<String> leagues) {
        addClassName("team-form");

        name.setRequiredIndicatorVisible(true);
        name.setErrorMessage("This field is required");
        city.setRequiredIndicatorVisible(true);
        city.setErrorMessage("This field is required");
        leagueName.setRequiredIndicatorVisible(true);
        leagueName.setErrorMessage("This field is required");
        leagueName.setItems(leagues);
        rating.setStep(0.5);
        rating.setValue(0.0);
        rating.setHasControls(true);

        binder.bindInstanceFields(this);

        add(name,
                city,
                field,
                leagueName,
                rating,
                createButtonsLayout());
    }

    // Events
    public abstract static class TeamFormEvent extends ComponentEvent<TeamForm> {
        private final Team team;

        protected TeamFormEvent(TeamForm source, Team team) {
            super(source, false);
            this.team = team;
        }

        public Team getTeam() {
            return team;
        }
    }

    public void setTeam(Team team) {
        this.team = team;
        binder.readBean(team);
    }

    public static class SaveEvent extends TeamFormEvent {
        SaveEvent(TeamForm source, Team team) {
            super(source, team);
        }
    }

    public static class DeleteEvent extends TeamFormEvent {
        DeleteEvent(TeamForm source, Team team) {
            super(source, team);
        }

    }

    public static class CloseEvent extends TeamFormEvent {
        CloseEvent(TeamForm source) {
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
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, team)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(team);
            fireEvent(new SaveEvent(this, team));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
}
