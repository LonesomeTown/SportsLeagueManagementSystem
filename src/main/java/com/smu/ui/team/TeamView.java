package com.smu.ui.team;

import com.smu.dto.League;
import com.smu.dto.Team;
import com.smu.dto.TeamGameRecordVo;
import com.smu.service.GameService;
import com.smu.service.LeagueService;
import com.smu.service.TeamService;
import com.smu.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "team", layout = MainLayout.class)
@PageTitle("League | Project Group8")
public class TeamView extends VerticalLayout {
    Grid<Team> upperGrid = new Grid<>(Team.class);
    Grid<TeamGameRecordVo> lowerGrid = new Grid<>(TeamGameRecordVo.class, false);
    TextField upperFilterText = new TextField();
    ComboBox<String> teamNameComboBox = new ComboBox<>();
    TeamForm form;
    LeagueService leagueService;
    TeamService teamService;
    GameService gameService;

    public TeamView(LeagueService leagueService, TeamService teamService, GameService gameService) {
        this.gameService = gameService;
        this.leagueService = leagueService;
        this.teamService = teamService;
        addClassName("team-view");
        setSizeFull();
        configureUpperGrid();
        configureForm();
        closeEditor();
        add(getUpperToolbar(), getContent(), getLowerToolbar(), configureLowerGrid());

    }

    private void configureUpperGrid() {
        upperGrid.addClassNames("team-upper-grid");
        upperGrid.setSizeFull();
        upperGrid.setColumns("name");
        upperGrid.addColumn(Team::getCity).setHeader("City of Team");
        upperGrid.addColumn(Team::getField).setHeader("Field of Team");
        upperGrid.addColumn(Team::getLeagueName).setHeader("League Name");
        upperGrid.addColumn(Team::getRating).setHeader("Rating").setSortable(true);
        upperGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.updateUpperGridList();

        upperGrid.asSingleSelect().addValueChangeListener(event ->
                editTeam(event.getValue()));
    }

    private HorizontalLayout configureLowerGrid() {
        lowerGrid.addClassNames("team-lower-grid");
        lowerGrid.setSizeFull();
        lowerGrid.addColumn(TeamGameRecordVo::getSeasonDuration).setHeader("Season Duration");
        lowerGrid.addColumn(TeamGameRecordVo::getGamesPlayed).setHeader("Played Games");
        lowerGrid.addColumn(TeamGameRecordVo::getNumsWon).setHeader("Number of Won");
        lowerGrid.addColumn(TeamGameRecordVo::getNumsLoss).setHeader("Number of Loss");
        lowerGrid.addColumn(TeamGameRecordVo::getSumScores).setHeader("Sum Scores").setSortable(true);
        lowerGrid.addColumn(TeamGameRecordVo::getSumOpponentScores).setHeader("Sum Scores of Opponent Team").setSortable(true);
        lowerGrid.addColumn(TeamGameRecordVo::getSumTotalPoints).setHeader("Sum Total Points").setSortable(true);
        lowerGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.updateLowerGridList();

        HorizontalLayout layout = new HorizontalLayout(lowerGrid);
        layout.addClassNames("content");
        layout.setSizeFull();
        return layout;
    }

    private HorizontalLayout getUpperToolbar() {
        upperFilterText.setPlaceholder("Filter by name...");
        upperFilterText.setClearButtonVisible(true);
        upperFilterText.setValueChangeMode(ValueChangeMode.LAZY);
        upperFilterText.addValueChangeListener(e -> updateUpperGridList());

        Button addTeamButton = new Button("Add team");
        addTeamButton.addClickListener(click -> addTeam());

        HorizontalLayout toolbar = new HorizontalLayout(upperFilterText, addTeamButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private HorizontalLayout getLowerToolbar() {
        List<String> allTeamsName = teamService.findAllTeamsName();
        teamNameComboBox.setItems(allTeamsName);
        teamNameComboBox.setPlaceholder("Search records...");
        teamNameComboBox.setClearButtonVisible(true);
        teamNameComboBox.addValueChangeListener(e -> this.updateLowerGridList());

        HorizontalLayout toolbar = new HorizontalLayout(teamNameComboBox);
        toolbar.addClassName("lower-toolbar");
        return toolbar;
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(upperGrid, form);
        content.setFlexGrow(2, upperGrid);
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
        upperGrid.asSingleSelect().clear();
        Team team = new Team();
        team.setName(upperFilterText.getValue());
        editTeam(team);
    }

    private void saveTeam(TeamForm.SaveEvent event) {
        teamService.saveTeam(event.getTeam());
        closeEditor();
        updateUpperGridList();
    }

    private void deleteTeam(TeamForm.DeleteEvent event) {
        teamService.deleteTeam(event.getTeam());
        updateUpperGridList();
        closeEditor();
    }

    private void updateUpperGridList() {
        upperGrid.setItems(teamService.findAllTeams(upperFilterText.getValue()));
    }

    private void updateLowerGridList() {
        lowerGrid.setItems(gameService.findGameRecordsByTeam(teamNameComboBox.getValue()));
    }
}