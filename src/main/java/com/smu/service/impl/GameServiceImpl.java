package com.smu.service.impl;

import com.smu.dto.Game;
import com.smu.dto.Season;
import com.smu.repository.GameRepository;
import com.smu.service.GameService;
import com.smu.service.SeasonService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public GameServiceImpl(GameRepository gameRepository, SeasonService seasonService) {
        this.gameRepository = gameRepository;
        this.seasonService = seasonService;
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

    }

}
