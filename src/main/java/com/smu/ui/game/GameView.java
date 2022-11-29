package com.smu.ui.game;

import com.smu.dto.Game;
import com.smu.dto.GameVo;
import com.smu.dto.Season;
import com.smu.service.GameService;
import com.smu.service.SeasonService;
import com.smu.service.TeamService;
import com.smu.ui.MainLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * GameView
 *
 * @author T.W 11/25/22
 */
@Route(value = "game", layout = MainLayout.class)
@PageTitle("Game | Project Group8")
public class GameView extends VerticalLayout {
    Grid<GameVo> grid = new Grid<>(GameVo.class, false);
    private final GameService gameService;
    private final SeasonService seasonService;
    private final TeamService teamService;
    ComboBox<String> homeTeamBox = new ComboBox<>();
    ComboBox<String> visitingTeamBox = new ComboBox<>();

    public GameView(GameService gameService, SeasonService seasonService, TeamService teamService) {
        this.gameService = gameService;
        this.seasonService = seasonService;
        this.teamService = teamService;
        addClassName("game-view");
        setSizeFull();

        add(getToolbar(), configureGrid());
    }

    private HorizontalLayout configureGrid() {
        grid.addClassNames("game-grid");
        grid.setSizeFull();
        grid.addColumn(GameVo::getHomeTeamName).setHeader("Home Team Name");
        grid.addColumn(GameVo::getVisitingTeamName).setHeader("Visiting Team Name");
        grid.addColumn(GameVo::getLeagueName).setHeader("League Name");
        grid.addColumn(GameVo::getGameDate).setHeader("Game Date");
        grid.addColumn(GameVo::getHomeScore).setHeader("Home Team Scores");
        grid.addColumn(GameVo::getVisitingScore).setHeader("Visiting Team Scores");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.updateList();
        HorizontalLayout layout = new HorizontalLayout(grid);
        layout.addClassNames("grid-content");
        layout.setSizeFull();
        return layout;
    }

    private HorizontalLayout getToolbar() {
        homeTeamBox.setPlaceholder("Home team...");
        homeTeamBox.setClearButtonVisible(true);
        homeTeamBox.addValueChangeListener(e -> updateList());
        List<String> allTeamsName = teamService.findAllTeamsName();
        homeTeamBox.setItems(allTeamsName);

        visitingTeamBox.setPlaceholder("Visiting team...");
        visitingTeamBox.setClearButtonVisible(true);
        visitingTeamBox.setItems(allTeamsName);
        visitingTeamBox.addValueChangeListener(e -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(homeTeamBox, visitingTeamBox);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateList() {
        List<Game> allGames = gameService.findAllGames(homeTeamBox.getValue(), visitingTeamBox.getValue());
        List<GameVo> gameVos = new ArrayList<>();
        for (Game game : allGames) {
            GameVo gameVo = new GameVo();
            BeanUtils.copyProperties(game, gameVo);
            Season season = seasonService.findById(game.getSeasonId());
            gameVo.setLeagueName(season.getLeagueName());
            gameVos.add(gameVo);
        }
        grid.setItems(gameVos);
    }
}
