package com.smu.ui.season;

import com.smu.dto.Game;
import com.smu.dto.Season;
import com.smu.service.GameService;
import com.smu.service.SeasonService;
import com.smu.service.TeamService;
import com.smu.ui.MainLayout;
import com.smu.ui.NotificationError;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

@Route(value = "season", layout = MainLayout.class)
@PageTitle("League | Project Group8")
public class SeasonView extends VerticalLayout {
    Grid<Season> grid = new Grid<>(Season.class);
    DatePicker datePicker = new DatePicker();
    SeasonForm form;
    GamesDialog dialog;
    private final SeasonService seasonService;
    private final GameService gameService;
    private final TeamService teamService;

    public SeasonView(SeasonService seasonService, GameService gameService, TeamService teamService) {
        this.seasonService = seasonService;
        this.gameService = gameService;
        this.teamService = teamService;
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
        grid.setColumns("startDate");
        grid.addColumn(Season::getEndDate).setHeader("End Date");
        grid.addColumn(Season::getGamesNum).setHeader("Numbers of Game");
        grid.addComponentColumn(t -> createInlineButtonComponent(t.getId()));

        grid.setDetailsVisibleOnClick(false);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.updateList();

        grid.asSingleSelect().addValueChangeListener(event ->
                editSeason(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        datePicker.setPlaceholder("Filter by start date...");
        datePicker.setClearButtonVisible(true);
        datePicker.addValueChangeListener(e -> updateList());

        Button addSeasonButton = new Button("Add season");
        addSeasonButton.addClickListener(click -> addSeason());

        HorizontalLayout toolbar = new HorizontalLayout(datePicker, addSeasonButton);
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
        Button tertiaryInlineButton = new Button("Details");
        tertiaryInlineButton
                .addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        tertiaryInlineButton.addClickListener(e -> configureDialog(seasonId));
        return tertiaryInlineButton;
    }

    private void configureDialog(ObjectId seasonId) {
        Season season = seasonService.findById(seasonId);
        dialog = new GamesDialog(season, gameService, teamService);
        dialog.addListener(GamesDialog.SaveEvent.class, e -> saveGames(e, season.getId()));
        dialog.addListener(GamesDialog.CloseEvent.class, e -> closeGameDialog());
        dialog.open();
    }

    private void configureForm() {
        form = new com.smu.ui.season.SeasonForm();
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
        season.setStartDate(datePicker.getValue());
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
        dialog.setGame(game);
        String msg = gameService.saveGame(game);
        if (StringUtils.isNotBlank(msg)) {
            new NotificationError(msg);
        }
        dialog.updateGameGridList(seasonId);
    }

    private void closeGameDialog() {
        dialog.close();
    }

    private void updateList() {
        grid.setItems(seasonService.findSeasonsByStartDate(datePicker.getValue()));
    }
}