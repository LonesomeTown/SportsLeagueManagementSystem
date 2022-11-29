package com.smu.ui.season;

import com.smu.dto.Season;
import com.smu.dto.Team;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;
import java.util.List;

/**
 * LeagueForm
 *
 * @author T.W 11/4/22
 */
public class SeasonForm extends FormLayout {
    ComboBox<String> leagueName = new ComboBox<>("League Name");
    DatePicker startDate = new DatePicker("Start Date");
    DatePicker endDate = new DatePicker("End Date");
    IntegerField gamesNum = new IntegerField("Numbers of Game");


    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Season> binder = new BeanValidationBinder<>(Season.class);

    Season season;

    public SeasonForm(List<String> leaguesName) {
        addClassName("season-form");

        leagueName.setRequired(true);
        leagueName.setItems(leaguesName);
        startDate.setRequiredIndicatorVisible(true);
        startDate.setErrorMessage("This field is required");
        startDate.setMin(LocalDate.now());
        endDate.setRequiredIndicatorVisible(true);
        endDate.setErrorMessage("This field is required");
        startDate.addValueChangeListener(e -> endDate.setMin(e.getValue()));
        endDate.addValueChangeListener(e -> startDate.setMax(e.getValue()));
        gamesNum.setRequiredIndicatorVisible(true);
        gamesNum.setErrorMessage("This field is required");

        binder.bindInstanceFields(this);

        add(leagueName,
                startDate,
                endDate,
                gamesNum,
                createButtonsLayout());
    }

    // Events
    public abstract static class SeasonFormEvent extends ComponentEvent<SeasonForm> {
        private final Season season;

        protected SeasonFormEvent(SeasonForm source, Season season) {
            super(source, false);
            this.season = season;
        }

        public Season getSeason() {
            return season;
        }
    }

    public void setSeason(Season season) {
        this.season = season;
        binder.readBean(season);
    }

    public static class SaveEvent extends SeasonFormEvent {
        SaveEvent(SeasonForm source, Season season) {
            super(source, season);
        }
    }

    public static class DeleteEvent extends SeasonFormEvent {
        DeleteEvent(SeasonForm source, Season season) {
            super(source, season);
        }

    }

    public static class CloseEvent extends SeasonFormEvent {
        CloseEvent(SeasonForm source) {
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
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, season)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(season);
            fireEvent(new SaveEvent(this, season));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
}
