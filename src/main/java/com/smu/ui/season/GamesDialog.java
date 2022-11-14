package com.smu.ui.season;

import com.smu.dto.Game;
import com.smu.dto.InitializeVo;
import com.smu.dto.Season;
import com.smu.dto.Team;
import com.smu.service.GameService;
import com.smu.service.TeamService;
import com.smu.ui.MainLayout;
import com.smu.ui.league.InitializeDialog;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GamesDialog
 *
 * @author T.W 11/11/22
 */
public class GamesDialog extends Dialog {
    ComboBox<String> homeTeamName = new ComboBox<>("Home Team");
    ComboBox<String> visitingTeamName = new ComboBox<>("Visiting Team");
    ComboBox<String> location = new ComboBox<>("Location");
    DatePicker gameDate = new DatePicker("Game Date");

    Button saveButton = new Button("Add");
    Button cancelButton = new Button("Cancel");
    Button generateButton = new Button("Generate Randomly");

    Grid<Game> grid = new Grid<>(Game.class);

    Binder<Game> binder = new BeanValidationBinder<>(Game.class);

    Game game = new Game();

    GameService gameService;
    TeamService teamService;

    public GamesDialog(Season season, GameService gameService, TeamService teamService) {
        this.gameService = gameService;
        this.teamService = teamService;

        Dialog dialog = new Dialog();

        addClassName("initialize-dialog");

        dialog.setHeaderTitle("Game Details");

        configureGrid(season);
        Div div = new Div();
        div.add(grid);
        dialog.add(div);

        VerticalLayout dialogLayout = createDialogLayout(season);

        dialog.add(dialogLayout);

        dialog.setModal(false);
        dialog.setWidth("650px");

        createButtonLayout(dialog);

        dialog.getFooter().add(generateButton);
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        add(dialog);

        dialog.open();

    }

    private void configureGrid(Season season) {
        grid.addClassNames("game-grid");
        grid.setColumns("homeTeamName");
        grid.addColumn(Game::getVisitingTeamName).setHeader("Visiting Team Name");
        grid.addColumn(Game::getLocation).setHeader("Game Location");
        grid.addColumn(Game::getGameDate).setHeader("Game Date");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setPageSize(5);
        updateGameGridList(season.getId());
    }

    public void updateGameGridList(ObjectId id) {
        List<Game> gamesBySeason = gameService.findGamesBySeason(id);
        grid.setItems(gamesBySeason);
    }

    private VerticalLayout createDialogLayout(Season season) {

        homeTeamName.setRequired(true);
        visitingTeamName.setRequired(true);
        List<Team> allTeams = teamService.findAllTeams("");
        if (!CollectionUtils.isEmpty(allTeams)) {
            List<String> teamNames = allTeams.stream().map(Team::getName).collect(Collectors.toList());
            homeTeamName.setItems(teamNames);
            visitingTeamName.setItems(teamNames);
        }
        List<String> locations = new ArrayList<>();
        homeTeamName.addValueChangeListener(e -> {
            String fieldByTeamName = teamService.findFieldByTeamName(e.getValue());
            if (StringUtils.isNotEmpty(fieldByTeamName)) {
                locations.add(fieldByTeamName);
            }
            if (!CollectionUtils.isEmpty(locations)) {
                location.setItems(locations);
            }
        });
        visitingTeamName.addValueChangeListener(e -> {
            String fieldByTeamName = teamService.findFieldByTeamName(e.getValue());
            if (StringUtils.isNotEmpty(fieldByTeamName)) {
                locations.add(fieldByTeamName);
            }
            if (!CollectionUtils.isEmpty(locations)) {
                location.setItems(locations);
            }
        });
        location.setRequired(true);
        gameDate.setRequired(true);
        gameDate.setMax(season.getEndDate());
        gameDate.setMin(season.getStartDate());

        binder.bindInstanceFields(this);

        HorizontalLayout firstLineHorizontalLayout = new HorizontalLayout(homeTeamName, visitingTeamName);
        HorizontalLayout secondLineHorizontalLayout = new HorizontalLayout(location, gameDate);

        VerticalLayout dialogLayout = new VerticalLayout(firstLineHorizontalLayout, secondLineHorizontalLayout);

        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "650px").set("max-width", "100%");

        return dialogLayout;
    }

    private void createButtonLayout(Dialog dialog) {
        generateButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.addClickListener(event -> {
            validateAndSave();
        });
        cancelButton.addClickListener(event -> {
            fireEvent(new CloseEvent(this));
            dialog.close();
        });
        binder.addStatusChangeListener(e -> saveButton.setEnabled(binder.isValid()));
    }

    // Events
    public abstract static class GameDialogEvent extends ComponentEvent<GamesDialog> {
        private final Game game;

        protected GameDialogEvent(GamesDialog source, Game game) {
            super(source, false);
            this.game = game;
        }

        public Game getGame() {
            return game;
        }
    }

    public void setGame(Game game) {
        this.game = game;
        binder.readBean(game);
    }

    public static class SaveEvent extends GameDialogEvent {
        SaveEvent(GamesDialog source, Game game) {
            super(source, game);
        }
    }

    public static class CloseEvent extends GameDialogEvent {
        CloseEvent(GamesDialog source) {
            super(source, null);
        }
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(game);
            fireEvent(new SaveEvent(this, game));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

}
