package com.smu.ui.league;

import com.smu.dto.League;
import com.smu.service.LeagueService;
import com.smu.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("League | Project Group8")
public class LeagueView extends VerticalLayout {
    Grid<League> grid = new Grid<>(League.class);
    TextField filterText = new TextField();
    LeagueForm form;
    LeagueService service;

    public LeagueView(LeagueService service) {
        this.service = service;
        addClassName("league-view");
        setSizeFull();
        configureGrid();
        configureForm();
        closeEditor();
        add(getToolbar(), getContent());
    }

    private void configureGrid() {
        grid.addClassNames("league-grid");
        grid.setSizeFull();
        grid.setColumns("name");
        grid.addColumn(League::getCommissionerName).setHeader("Commissioner Name");
        grid.addColumn(League::getCommissionerSsn).setHeader("Commissioner SSN");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.updateList();

        grid.asSingleSelect().addValueChangeListener(event ->
                editLeague(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add league");
        addContactButton.addClickListener(click -> addLeague());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new LeagueForm();
        form.setWidth("25em");
        form.addListener(LeagueForm.SaveEvent.class, this::saveLeague);
        form.addListener(LeagueForm.DeleteEvent.class, this::deleteLeague);
        form.addListener(LeagueForm.CloseEvent.class, e -> closeEditor());
    }

    private void closeEditor() {
        form.setLeague(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    public void editLeague(League league) {
        if (league == null) {
            closeEditor();
        } else {
            form.setLeague(league);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void addLeague() {
        grid.asSingleSelect().clear();
        League league = new League();
        league.setName(filterText.getValue());
        editLeague(league);
    }

    private void saveLeague(LeagueForm.SaveEvent event) {
        service.saveLeague(event.getLeague());
        updateList();
        closeEditor();
    }

    private void deleteLeague(LeagueForm.DeleteEvent event) {
        service.deleteLeague(event.getLeague());
        updateList();
        closeEditor();
    }

    private void updateList() {
        grid.setItems(service.findAllLeagues(filterText.getValue()));
    }
}