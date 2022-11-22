package com.smu.ui.season;

import com.smu.dto.Game;
import com.smu.dto.ScoringCriteria;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

/**
 * ScoringCriteriaDialog
 *
 * @author T.W 11/21/22
 */
public class ScoringCriteriaDialog extends Dialog {
    NumberField wonPoints = new NumberField("Won Points");
    NumberField drawnPoints = new NumberField("Drawn Points");
    NumberField lostPoints = new NumberField("Loss Points");
    Button saveButton = new Button("Save");
    Button cancelButton = new Button("Cancel");
    ScoringCriteria scoringCriteria = new ScoringCriteria();
    Binder<ScoringCriteria> binder = new BeanValidationBinder<>(ScoringCriteria.class);

    public ScoringCriteriaDialog(ScoringCriteria scoringCriteria) {
        Dialog dialog = new Dialog();

        addClassName("scoring-criteria-dialog");

        dialog.setHeaderTitle("Scoring Criteria");

        VerticalLayout dialogLayout = createDialogLayout(scoringCriteria);

        dialog.add(dialogLayout);

        dialog.setModal(false);

        createButtonLayout(dialog);

        dialog.getFooter().add(saveButton);
        dialog.getFooter().add(cancelButton);

        add(dialog);

        dialog.open();
    }

    private VerticalLayout createDialogLayout(ScoringCriteria scoringCriteria) {
        wonPoints.setRequiredIndicatorVisible(true);
        wonPoints.setValue(null == scoringCriteria ? 1 : scoringCriteria.getWonPoints());
        wonPoints.setWidthFull();
        drawnPoints.setRequiredIndicatorVisible(true);
        drawnPoints.setValue(null == scoringCriteria ? 0 : scoringCriteria.getDrawnPoints());
        drawnPoints.setWidthFull();
        lostPoints.setRequiredIndicatorVisible(true);
        lostPoints.setValue(null == scoringCriteria ? -1 : scoringCriteria.getLostPoints());
        lostPoints.setWidthFull();
        binder.bindInstanceFields(this);

        VerticalLayout dialogLayout = new VerticalLayout(wonPoints, drawnPoints, lostPoints);

        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        return dialogLayout;
    }

    private void createButtonLayout(Dialog dialog) {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.addClickListener(event -> validateAndSave());
        cancelButton.addClickListener(event -> {
            fireEvent(new CloseEvent(this));
            dialog.close();
        });
        binder.addStatusChangeListener(e -> saveButton.setEnabled(binder.isValid()));
    }

    // Events
    public abstract static class ScoringCriteriaDialogEvent extends ComponentEvent<ScoringCriteriaDialog> {
        private final ScoringCriteria scoringCriteria;

        protected ScoringCriteriaDialogEvent(ScoringCriteriaDialog source, ScoringCriteria scoringCriteria) {
            super(source, false);
            this.scoringCriteria = scoringCriteria;
        }

        public ScoringCriteria getScoringCriteria() {
            return scoringCriteria;
        }
    }

    public void setScoringCriteria(ScoringCriteria scoringCriteria) {
        this.scoringCriteria = scoringCriteria;
        binder.readBean(scoringCriteria);
    }

    public static class SaveEvent extends ScoringCriteriaDialogEvent {
        SaveEvent(ScoringCriteriaDialog source, ScoringCriteria scoringCriteria) {
            super(source, scoringCriteria);
        }
    }

    public static class CloseEvent extends ScoringCriteriaDialogEvent {
        CloseEvent(ScoringCriteriaDialog source) {
            super(source, null);
        }
    }

    public static class GenerateEvent extends GamesDialog.GameDialogEvent {
        GenerateEvent(GamesDialog source, Game game) {
            super(source, game);
        }
    }


    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(scoringCriteria);
            fireEvent(new SaveEvent(this, scoringCriteria));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
}
