package com.smu.ui.team;

import com.smu.dto.League;
import com.smu.dto.Team;
import com.smu.service.LeagueService;
import com.smu.service.TeamService;
import com.smu.ui.MainLayout;
import com.smu.ui.league.InitializeDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.stream.Collectors;

@Route(value = "team", layout = MainLayout.class)
@PageTitle("League | Project Group8")
public class TeamView extends VerticalLayout {
    Grid<Team> grid = new Grid<>(Team.class);
    TextField filterText = new TextField();
    TeamForm form;
    LeagueService leagueService;
    TeamService teamService;

    public TeamView(LeagueService leagueService, TeamService teamService) {
        this.leagueService = leagueService;
        this.teamService = teamService;
        addClassName("team-view");
        setSizeFull();
        configureGrid();
        configureForm();
        closeEditor();
        add(getToolbar(), getContent());

    }

    private void configureGrid() {
        grid.addClassNames("team-grid");
        grid.setSizeFull();
        grid.setColumns("name");
        grid.addColumn(Team::getCity).setHeader("City of Team");
        grid.addColumn(Team::getField).setHeader("Field of Team");
        grid.addColumn(Team::getLeagueName).setHeader("League Name");
        grid.addColumn(Team::getRating).setHeader("Rating");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.updateList();

        grid.asSingleSelect().addValueChangeListener(event ->
                editTeam(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addTeamButton = new Button("Add team");
        addTeamButton.addClickListener(click -> addTeam());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addTeamButton);
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
        form = new TeamForm(leagueService.findAllLeagues("").stream().map(League::getName).collect(Collectors.toList()));
        form.setWidth("25em");
        form.addListener(TeamForm.SaveEvent.class, this::saveTeam);
        form.addListener(TeamForm.DeleteEvent.class, this::deleteTeam);
        form.addListener(TeamForm.CloseEvent.class, e -> closeEditor());
    }

    private void closeEditor() {
        form.setTeam(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    public void editTeam(Team team) {
        if (team == null) {
            closeEditor();
        } else {
            form.setTeam(team);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void addTeam() {
        grid.asSingleSelect().clear();
        Team team = new Team();
        team.setName(filterText.getValue());
        editTeam(team);
    }

    private void saveTeam(TeamForm.SaveEvent event) {
        teamService.saveTeam(event.getTeam());
        closeEditor();
        updateList();
    }

    private void deleteTeam(TeamForm.DeleteEvent event) {
        teamService.deleteTeam(event.getTeam());
        updateList();
        closeEditor();
    }

    private void updateList() {
        grid.setItems(teamService.findAllTeams(filterText.getValue()));
    }
}