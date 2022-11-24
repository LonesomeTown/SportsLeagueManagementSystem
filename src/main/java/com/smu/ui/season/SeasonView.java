package com.smu.ui.season;

import com.smu.dto.Game;
import com.smu.dto.League;
import com.smu.dto.ScoringCriteria;
import com.smu.dto.Season;
import com.smu.service.*;
import com.smu.ui.MainLayout;
import com.smu.ui.NotificationError;
import com.smu.ui.NotificationSuccess;
import com.smu.ui.game.GamesDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "season", layout = MainLayout.class)
@PageTitle("League | Project Group8")
public class SeasonView extends VerticalLayout {
    Grid<Season> grid = new Grid<>(Season.class);
    ComboBox<String> comboBox = new ComboBox<>();
    DatePicker datePicker = new DatePicker();
    SeasonForm form;
    GamesDialog gameDialog;
    ScoringCriteriaDialog scoringCriteriaDialog;
    private final LeagueService leagueService;
    private final SeasonService seasonService;
    private final GameService gameService;
    private final TeamService teamService;
    private final ScoringCriteriaService scoringCriteriaService;

    public SeasonView(LeagueService leagueService, SeasonService seasonService, GameService gameService, TeamService teamService, ScoringCriteriaService scoringCriteriaService) {
        this.leagueService = leagueService;
        this.seasonService = seasonService;
        this.gameService = gameService;
        this.teamService = teamService;
        this.scoringCriteriaService = scoringCriteriaService;
        addClassName("team-view");
        setSizeFull();
        configureGrid();
        configureForm();
        closeEditor();
        add(getToolbar(), getContent());

    }

    private void configureGrid() {
        grid.addClassNames("season-grid");
        grid.setSizeFull();
        grid.setColumns("leagueName");
        grid.addColumn(Season::getStartDate).setHeader("Start Date");
        grid.addColumn(Season::getEndDate).setHeader("End Date");
        grid.addColumn(Season::getGamesNum).setHeader("Numbers of Game");
        grid.addComponentColumn(t -> createInlineButtonComponent(t.getId()));
        grid.addComponentColumn(t -> createSecondInlineButtonComponent(t.getId()));

        grid.setDetailsVisibleOnClick(false);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.updateList();

        grid.asSingleSelect().addValueChangeListener(event ->
                editSeason(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        List<League> allLeagues = leagueService.findAllLeagues("");
        comboBox.setItems(allLeagues.stream().map(League::getName).collect(Collectors.toList()));
        comboBox.setPlaceholder("Filter by league name...");
        comboBox.setClearButtonVisible(true);
        comboBox.addValueChangeListener(e -> updateList());

        Button addSeasonButton = new Button("Add season");
        addSeasonButton.addClickListener(click -> addSeason());

        datePicker.setPlaceholder("Current date");
        datePicker.setClearButtonVisible(true);

        Button setCurrentDateButton = new Button("Set as current date");
        setCurrentDateButton.addClickListener(click -> this.updateCurrentDate());

        HorizontalLayout toolbar = new HorizontalLayout(comboBox, addSeasonButton, datePicker, setCurrentDateButton);
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

    private Button createInlineButtonComponent(ObjectId seasonId) {
        Button tertiaryInlineButton = new Button("Game Details");
        tertiaryInlineButton
                .addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        tertiaryInlineButton.addClickListener(e -> configureGameDialog(seasonId));
        return tertiaryInlineButton;
    }

    private Button createSecondInlineButtonComponent(ObjectId seasonId) {
        Button tertiaryInlineButton = new Button("Scoring Criteria");
        tertiaryInlineButton
                .addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        tertiaryInlineButton.addClickListener(e -> configureScoringDialog(seasonId));
        return tertiaryInlineButton;
    }

    private void configureGameDialog(ObjectId seasonId) {
        Season season = seasonService.findById(seasonId);
        gameDialog = new GamesDialog(season, gameService, teamService);
        gameDialog.addListener(GamesDialog.SaveEvent.class, e -> saveGames(e, season.getId()));
        gameDialog.addListener(GamesDialog.CloseEvent.class, e -> closeGameDialog());
        gameDialog.addListener(GamesDialog.GenerateEvent.class, e -> autoGenerateGames(season.getId()));
        gameDialog.open();
    }

    private void configureScoringDialog(ObjectId seasonId) {
        ScoringCriteria scoringCriteria = scoringCriteriaService.findBySeasonId(seasonId);
        scoringCriteriaDialog = new ScoringCriteriaDialog(scoringCriteria);
        scoringCriteriaDialog.addListener(ScoringCriteriaDialog.SaveEvent.class, e -> saveScoringCriteria(e, seasonId));
        scoringCriteriaDialog.addListener(ScoringCriteriaDialog.CloseEvent.class, e -> closeScoringDialog());
        scoringCriteriaDialog.open();
    }

    private void configureForm() {
        List<League> allLeagues = leagueService.findAllLeagues("");
        form = new SeasonForm(allLeagues.stream().map(League::getName).collect(Collectors.toList()));
        form.setWidth("25em");
        form.addListener(SeasonForm.SaveEvent.class, this::saveSeason);
        form.addListener(SeasonForm.DeleteEvent.class, this::deleteSeason);
        form.addListener(SeasonForm.CloseEvent.class, e -> closeEditor());
    }

    private void closeEditor() {
        form.setSeason(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    public void editSeason(Season season) {
        if (season == null) {
            closeEditor();
        } else {
            form.setSeason(season);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void addSeason() {
        grid.asSingleSelect().clear();
        Season season = new Season();
        season.setLeagueName(comboBox.getValue());
        editSeason(season);
    }

    private void saveSeason(SeasonForm.SaveEvent event) {
        String msg = seasonService.saveSeason(event.getSeason());
        if (StringUtils.isNotBlank(msg)) {
            new NotificationError(msg);
        }
        closeEditor();
        updateList();
    }

    private void deleteSeason(SeasonForm.DeleteEvent event) {
        seasonService.deleteSeason(event.getSeason());
        updateList();
        closeEditor();
    }

    private void saveGames(GamesDialog.SaveEvent event, ObjectId seasonId) {
        Game game = event.getGame();
        game.setSeasonId(seasonId);
        gameDialog.setGame(game);
        String msg = gameService.saveGame(game);
        if (StringUtils.isNotBlank(msg)) {
            new NotificationError(msg);
        }
        gameDialog.updateGameGridList(seasonId);
    }

    private void saveScoringCriteria(ScoringCriteriaDialog.SaveEvent event, ObjectId seasonId) {
        ScoringCriteria scoringCriteria = event.getScoringCriteria();
        scoringCriteria.setSeasonId(seasonId);
        scoringCriteriaDialog.setScoringCriteria(scoringCriteria);
        scoringCriteriaService.save(scoringCriteria);
        new NotificationSuccess("Saved Successfully");
        closeScoringDialog();
    }

    private void autoGenerateGames(ObjectId seasonId) {
        gameService.autoGenerateGamesBySeason(seasonId);
        gameDialog.updateGameGridList(seasonId);
        new NotificationSuccess("Generated Successfully!");
    }

    private void closeGameDialog() {
        gameDialog.close();
    }

    private void closeScoringDialog() {
        scoringCriteriaDialog.close();
    }

    private void updateList() {
        grid.setItems(seasonService.findSeasonsByLeagueName(comboBox.getValue()));
    }

    private void updateCurrentDate() {

    }
}