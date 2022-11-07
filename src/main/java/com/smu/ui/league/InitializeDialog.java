package com.smu.ui.league;

import com.smu.dto.InitializeVo;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

/**
 * InitializeDialog
 *
 * @author T.W 11/6/22
 */
public class InitializeDialog extends Dialog {
    InitializeVo initializeVo = new InitializeVo();
    TextField name = new TextField("Team Name");
    TextField city = new TextField("Team City");
    TextField field = new TextField("Team Field");
    DatePicker startDate = new DatePicker("Season Start Date");
    DatePicker endDate = new DatePicker("Season End Date");
    IntegerField gamesNum = new IntegerField("Season Games Number");

    Button saveButton = new Button("Add");
    Button cancelButton = new Button("Cancel");

    Binder<InitializeVo> binder = new BeanValidationBinder<>(InitializeVo.class);

    public InitializeDialog() {
        Dialog dialog = new Dialog();

        addClassName("initialize-dialog");

        dialog.setHeaderTitle("Initialize Team and Season");

        VerticalLayout dialogLayout = createDialogLayout();

        dialog.add(dialogLayout);

        createButtonLayout(dialog);

        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        add(dialog);

        dialog.open();

    }

    private VerticalLayout createDialogLayout() {
        name.setRequired(true);
        city.setRequired(true);
        field.setRequired(true);
        startDate.setRequired(true);
        endDate.setRequired(true);
        startDate.addValueChangeListener(e -> endDate.setMin(e.getValue()));
        endDate.addValueChangeListener(e -> startDate.setMax(e.getValue()));
        gamesNum.setRequiredIndicatorVisible(true);

        binder.bindInstanceFields(this);

        VerticalLayout dialogLayout = new VerticalLayout(name,
                city, field, startDate, endDate, gamesNum);

        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private void createButtonLayout(Dialog dialog) {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.addClickListener(event -> {
            boolean validateResult = validateAndSave();
            if (validateResult) {
                fireEvent(new CloseEvent(this));
                dialog.close();
            }
        });
        cancelButton.addClickListener(event -> {
            fireEvent(new CloseEvent(this));
            dialog.close();
        });
        binder.addStatusChangeListener(e -> saveButton.setEnabled(binder.isValid()));
    }


    // Events
    public abstract static class InitializeDialogEvent extends ComponentEvent<InitializeDialog> {
        private final InitializeVo initializeVo;

        protected InitializeDialogEvent(InitializeDialog source, InitializeVo initializeVo) {
            super(source, false);
            this.initializeVo = initializeVo;
        }

        public InitializeVo getInitializeVo() {
            return initializeVo;
        }
    }

    public void setInitializeVo(InitializeVo initializeVo) {
        this.initializeVo = initializeVo;
        binder.readBean(initializeVo);
    }

    public static class SaveEvent extends InitializeDialogEvent {
        SaveEvent(InitializeDialog source, InitializeVo initializeVo) {
            super(source, initializeVo);
        }
    }

    public static class CloseEvent extends InitializeDialogEvent {
        CloseEvent(InitializeDialog source) {
            super(source, null);
        }
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    private boolean validateAndSave() {
        try {
            binder.writeBean(initializeVo);
            fireEvent(new SaveEvent(this, initializeVo));
            return true;
        } catch (ValidationException e) {
            e.printStackTrace();
            return false;
        }
    }
}
