package com.smu.ui.game;

import com.smu.dto.Game;
import com.smu.service.GameService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import org.bson.types.ObjectId;

/**
 * GamesGrid
 *
 * @author T.W 11/22/22
 */
public class GameRecordsDialog extends Dialog {

    private final GameService gameService;
    Grid<Game> grid = new Grid<>(Game.class, false);

    public GameRecordsDialog(GameService gameService, ObjectId seasonId, String teamName) {
        this.gameService = gameService;
        Dialog dialog = new Dialog();
        addClassName("games-records-dialog");
        dialog.setHeaderTitle("Game Records");

        grid.addClassNames("game-records-grid");
        grid.setSizeFull();
        grid.addColumn(Game::getHomeTeamName).setHeader("Home Team");
        grid.addColumn(Game::getVisitingTeamName).setHeader("Visiting Team");
        grid.addColumn(Game::getGameDate).setHeader("Game Date");
        grid.addColumn(Game::getLocation).setHeader("Location");
        grid.addColumn(Game::getHomeScore).setHeader("Home Team Scores");
        grid.addColumn(Game::getVisitingScore).setHeader("Visiting Team Scores");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.updateList(seasonId, teamName);

        Div div = new Div();
        div.add(grid);
        dialog.add(div);

        Button closeButton = new Button("Close");
        closeButton.addClickListener(event -> {
            fireEvent(new CloseEvent(this));
            dialog.close();
        });

        dialog.setModal(false);
        dialog.setWidth("50%");
        dialog.getFooter().add(closeButton);

        add(dialog);

        dialog.open();
    }

    // Events
    public abstract static class GameDialogEvent extends ComponentEvent<GameRecordsDialog> {
        private final Game game;

        protected GameDialogEvent(GameRecordsDialog source, Game game) {
            super(source, false);
            this.game = game;
        }

        public Game getGame() {
            return game;
        }
    }

    public static class CloseEvent extends GameDialogEvent {
        CloseEvent(GameRecordsDialog source) {
            super(source, null);
        }
    }


    private void updateList(ObjectId seasonId, String teamName) {
        grid.setItems(gameService.findGamesBySeasonAndTeam(seasonId, teamName));
    }
}
