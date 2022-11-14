package com.smu.service.impl;

import com.smu.dto.Game;
import com.smu.dto.Season;
import com.smu.dto.Team;
import com.smu.repository.GameRepository;
import com.smu.service.GameService;
import com.smu.service.SeasonService;
import com.smu.service.TeamService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Game Service Implementation
 *
 * @author Z.S. 11/12/2022
 */
@Service
public class GameServiceImpl implements GameService {
    // Data fields
    private final GameRepository gameRepository;
    private final SeasonService seasonService;
    private final TeamService teamService;

    public GameServiceImpl(GameRepository gameRepository, SeasonService seasonService, TeamService teamService) {
        this.gameRepository = gameRepository;
        this.seasonService = seasonService;
        this.teamService = teamService;
    }

    @Override
    public List<Game> findAllGames(String homeTeam, String visitTeam) {
        return gameRepository.findGameByHomeTeamNameEqualsAndVisitingTeamNameEquals(homeTeam, visitTeam);
    }

    @Override
    public List<Game> findGamesBySeason(ObjectId seasonId) {
        return gameRepository.findGameBySeasonIdEquals(seasonId);
    }

    @Override
    public String saveGame(Game game) {
        ObjectId seasonId = game.getSeasonId();
        Season season = seasonService.findById(seasonId);
        Integer gamesNum = season.getGamesNum();
        List<Game> gameBySeasonId = gameRepository.findGameBySeasonIdEquals(seasonId);
        if (gameBySeasonId.size() < gamesNum) {
            gameRepository.save(game);
            return "";
        }else {
            return "Games in this season are already full!";
        }
    }

    @Override
    public void autoGenerateGamesBySeason(ObjectId seasonId) {
        Season season = seasonService.findById(seasonId);
        Integer gamesNum = season.getGamesNum();
        List<Game> games = new ArrayList<>();
        List<String> allTeamsName = teamService.findAllTeamsName();
        Random random = new Random();
        for (int i = 0; i < gamesNum; i++) {
            int randomHomeTeamNameIndex = random.nextInt(allTeamsName.size());
//            do{
//                int randomVisitingTeamNameIndex = random.nextInt(allTeamsName.size());
//            }while (randomVisitingTeamNameIndex == randomHomeTeamNameIndex);
//            Game game = new Game();
//            game.setHomeTeamName(allTeamsName.get(randomHomeTeamNameIndex));
//            game.setVisitingTeamName();
//            game.setLocation();
//            game.setGameDate();
        }
    }

}
