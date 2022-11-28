package com.smu.ui.season;

import com.smu.dto.*;
import com.smu.service.*;
import com.smu.ui.MainLayout;
import com.smu.ui.NotificationError;
import com.smu.ui.NotificationSuccess;
import com.smu.ui.game.GamesDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.util.CollectionUtils;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Route(value = "season", layout = MainLayout.class)
@PageTitle("League | Project Group8")
public class SeasonView extends VerticalLayout {
    Grid<Season> upperGrid = new Grid<>(Season.class);
    ComboBox<String> comboBox = new ComboBox<>();
    DatePicker datePicker = new DatePicker();
    SeasonForm form;
    GamesDialog gameDialog;
    TeamStandingDialog teamStandingDialog;
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
        upperGrid.addClassNames("season-grid");
        upperGrid.setSizeFull();
        upperGrid.setColumns("leagueName");
        upperGrid.addColumn(Season::getStartDate).setHeader("Start Date");
        upperGrid.addColumn(Season::getEndDate).setHeader("End Date");
        upperGrid.addColumn(Season::getGamesNum).setHeader("Numbers of Game");
        upperGrid.addComponentColumn(t -> createInlineButtonComponent(t.getId()));
        upperGrid.addComponentColumn(t -> createSecondInlineButtonComponent(t.getId()));
        upperGrid.addComponentColumn(t -> createThirdInlineButtonComponent(t.getId()));

        upperGrid.setDetailsVisibleOnClick(false);
        upperGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.updateList();

        upperGrid.asSingleSelect().addValueChangeListener(event ->
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
        HorizontalLayout content = new HorizontalLayout(upperGrid, form);
        content.setFlexGrow(2, upperGrid);
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

    private Button createThirdInlineButtonComponent(ObjectId seasonId) {
        Button tertiaryInlineButton = new Button("Team Standing");
        tertiaryInlineButton
                .addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        tertiaryInlineButton.addClickListener(e -> configureTeamStandingDialog(seasonId));
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

    private void configureTeamStandingDialog(ObjectId seasonId) {
        teamStandingDialog = new TeamStandingDialog(seasonId, gameService);
        teamStandingDialog.addListener(TeamStandingDialog.CloseEvent.class, e -> teamStandingDialog.close());
        teamStandingDialog.open();
    }

    public static class TeamStandingDialog extends Dialog {
        Grid<TeamStandingVo> grid = new Grid<>(TeamStandingVo.class, false);
        Button closeButton = new Button("Close");
        GameService gameService;

        public TeamStandingDialog(ObjectId seasonId, GameService gameService) {
            this.gameService = gameService;
            Dialog dialog = new Dialog();

            addClassName("team-standing-dialog");

            dialog.setHeaderTitle("Team Standing");

            grid.addColumn(TeamStandingVo::getStanding).setHeader("Standing");
            grid.addColumn(TeamStandingVo::getTeamName).setHeader("Team Name");
            grid.addColumn(TeamStandingVo::getPoints).setHeader("Team Points");
            grid.getColumns().forEach(column -> column.setAutoWidth(true));
            grid.setPageSize(5);

            Div div = new Div();
            div.add(grid);
            updateList(seasonId);
            dialog.add(div);

            dialog.setModal(false);
            dialog.setWidth("40%");

            closeButton.addClickListener(event -> {
                fireEvent(new CloseEvent(this));
                dialog.close();
            });

//            VerticalLayout dialogLayout = createDialogLayout(scoringCriteria);
//
//            dialog.add(dialogLayout);

            dialog.setModal(false);
            dialog.getFooter().add(closeButton);

            add(dialog);

            dialog.open();
        }

        public void updateList(ObjectId seasonId) {
            List<TeamStandingVo> teamStandingVos = new ArrayList<>();
            List<Game> gamesBySeason = gameService.findGamesBySeason(seasonId);
            List<String> allTeamsName = new ArrayList<>();
            List<String> homeTeamName = gamesBySeason.stream().map(Game::getHomeTeamName).distinct().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(homeTeamName)) {
                allTeamsName.addAll(homeTeamName);
            }
            List<String> visitingTeamName = gamesBySeason.stream().map(Game::getVisitingTeamName).distinct().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(visitingTeamName)) {
                allTeamsName.addAll(visitingTeamName);
            }
            allTeamsName = allTeamsName.stream().distinct().collect(Collectors.toList());
            for (String teamName : allTeamsName) {
                TeamStandingVo teamStandingVo = new TeamStandingVo();
                TeamGameRecordVo gameRecordsByTeamInSeason = gameService.findGameRecordsByTeamInSeason(teamName, seasonId, gamesBySeason);
                if (null == gameRecordsByTeamInSeason) {
                    continue;
                }
                teamStandingVo.setTeamName(teamName);
                teamStandingVo.setPoints(gameRecordsByTeamInSeason.getSumTotalPoints());
                teamStandingVos.add(teamStandingVo);
            }
            for (int i = 0; i < teamStandingVos.size(); i++) {
                TeamStandingVo teamStandingVo = teamStandingVos.get(i);
                if (i == 0) {
                    teamStandingVo.setStanding(1);
                } else if (Objects.equals(teamStandingVo.getPoints(), teamStandingVos.get(i - 1).getPoints())) {
                    teamStandingVo.setStanding(teamStandingVos.get(i - 1).getStanding());
                } else {
                    teamStandingVo.setStanding(teamStandingVos.get(i - 1).getStanding() + 1);
                }
            }
            grid.setItems(teamStandingVos);
        }

        // Events
        public abstract static class TeamStandingDialogEvent extends ComponentEvent<TeamStandingDialog> {
            private final TeamStandingVo teamStandingVo;

            protected TeamStandingDialogEvent(TeamStandingDialog source, TeamStandingVo teamStandingVo) {
                super(source, false);
                this.teamStandingVo = teamStandingVo;
            }

            public TeamStandingVo getTeamStandingVo() {
                return teamStandingVo;
            }
        }

        public static class CloseEvent extends TeamStandingDialog.TeamStandingDialogEvent {
            CloseEvent(TeamStandingDialog source) {
                super(source, null);
            }
        }

        @Override
        public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                      ComponentEventListener<T> listener) {
            return getEventBus().addListener(eventType, listener);
        }
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
        upperGrid.asSingleSelect().clear();
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
        upperGrid.setItems(seasonService.findSeasonsByLeagueName(comboBox.getValue()));
    }

    private void updateCurrentDate() {
        if (null != datePicker.getValue()) {
            String msg = gameService.updateCurrentDate(datePicker.getValue());
            new NotificationSuccess(msg);
            updateList();
        }
    }
}