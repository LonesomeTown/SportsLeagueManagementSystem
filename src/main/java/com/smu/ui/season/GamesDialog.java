package com.smu.ui.season;

import com.smu.dto.Game;
import com.smu.dto.InitializeVo;
import com.smu.dto.Season;
import com.smu.dto.Team;
import com.smu.service.GameService;
import com.smu.service.TeamService;
import com.smu.ui.MainLayout;
import com.smu.ui.league.InitializeDialog;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
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

    Grid<Game> grid = new Grid<>(Game.class, false);
    Editor<Game> editor = grid.getEditor();

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
        Grid.Column<Game> homeTeamNameColumn = grid
                .addColumn(Game::getHomeTeamName).setHeader("Home Team Name")
                .setAutoWidth(true).setFlexGrow(0);
        Grid.Column<Game> visitingTeamNameColumn = grid
                .addColumn(Game::getVisitingTeamName).setHeader("Visiting Team Name")
                .setAutoWidth(true).setFlexGrow(0);
        Grid.Column<Game> locationColumn = grid
                .addColumn(Game::getLocation).setHeader("Game Location")
                .setAutoWidth(true).setFlexGrow(0);
        Grid.Column<Game> gameDateColumn = grid
                .addColumn(Game::getGameDate).setHeader("Game Date")
                .setAutoWidth(true).setFlexGrow(0);
        Grid.Column<Game> homeScoreColumn = grid
                .addColumn(Game::getHomeScore).setHeader("Home Score")
                .setAutoWidth(true).setFlexGrow(0);
        Grid.Column<Game> visitingScoreColumn = grid
                .addColumn(Game::getVisitingScore).setHeader("Visiting Score")
                .setAutoWidth(true).setFlexGrow(0);

        Binder<Game> gridBinder = new Binder<>(Game.class);
        editor.setBinder(gridBinder);

        ComboBox<String> gridHomeTeam = new ComboBox<>();
        gridHomeTeam.setWidthFull();
        binder.forField(gridHomeTeam)
                .asRequired("Home team name must not be empty")
                .bind(Game::getHomeTeamName, Game::setHomeTeamName);
        homeTeamNameColumn.setEditorComponent(gridHomeTeam);

        grid.addItemDoubleClickListener(e -> {
            editor.editItem(e.getItem());
            Component editorComponent = e.getColumn().getEditorComponent();
            if (editorComponent instanceof Focusable) {
                ((Focusable) editorComponent).focus();
            }
        });

        grid.setPageSize(5);
        updateGameGridList(season.getId());

    }

    public void updateGameGridList(ObjectId id) {
        List<Game> gamesBySeason = gameService.findGamesBySeason(id);
        grid.setItems(gamesBySeason);
    }

    private static void addCloseHandler(Component textField,
                                        Editor<Game> editor) {
        textField.getElement().addEventListener("keydown", e -> editor.cancel())
                .setFilter("event.code === 'Escape'");
    }

    private VerticalLayout createDialogLayout(Season season) {

        homeTeamName.setRequired(true);
        visitingTeamName.setRequired(true);
        List<String> allTeamsName = teamService.findAllTeamsName();
        if (!CollectionUtils.isEmpty(allTeamsName)) {
            homeTeamName.setItems(allTeamsName);
            visitingTeamName.setItems(allTeamsName);
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
        dialogLayout.setSizeFull();
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "650px").set("max-width", "100%");

        return dialogLayout;
    }

    private void createButtonLayout(Dialog dialog) {
        generateButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        generateButton.addClickListener(event -> fireEvent(new GenerateEvent(this, game)));
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

    public static class GenerateEvent extends GameDialogEvent {
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
            binder.writeBean(game);
            fireEvent(new SaveEvent(this, game));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

}
