package com.smu.service.impl;

import com.smu.dto.Game;
import com.smu.dto.Season;
import com.smu.repository.GameRepository;
import com.smu.service.GameService;
import com.smu.service.SeasonService;
import com.smu.service.TeamService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
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
    public String autoGenerateGamesBySeason(ObjectId seasonId){
        Season season = seasonService.findById(seasonId);
        Integer gamesNum = season.getGamesNum();
        List<Game> games = new ArrayList<>();
        List<String> allTeamsName = teamService.findAllTeamsName();
        if(CollectionUtils.isEmpty(allTeamsName)){
            return null;
        }
        Random random = new Random();
        for (int i = 0; i < gamesNum; i++) {
            Game game = new Game();
            game.setSeasonId(seasonId);
            // Set the name for home team and visiting team
            int randomHomeTeamNameIndex = random.nextInt(allTeamsName.size());
            int randomVisitingTeamNameIndex = random.nextInt(allTeamsName.size());
            while (randomVisitingTeamNameIndex == randomHomeTeamNameIndex) {
                randomVisitingTeamNameIndex = random.nextInt(allTeamsName.size());
            }

            // Set locations
            String field= teamService.findFieldByTeamName(allTeamsName.get(randomHomeTeamNameIndex));

            //localDate date
            long seasonLength = season.getEndDate().toEpochDay() - season.getStartDate().toEpochDay();
            long randomSpan = random.nextInt((int)seasonLength);
            LocalDate gameDate = season.getStartDate().plusDays(randomSpan);
            //List<String> duplicateHomeAndVisitTeams = gameRepository.findGameByGameDateEqualsAndHomeTeamNameEqualsAndVisitingTeamNameEquals()
            game.setHomeTeamName(allTeamsName.get(randomHomeTeamNameIndex));
            game.setVisitingTeamName(allTeamsName.get(randomVisitingTeamNameIndex));
            game.setLocation(field);
            game.setGameDate(gameDate);
            List<Game> duplicateHomeAndVisitTeams = gameRepository.findGameByGameDateEqualsAndHomeTeamNameEqualsAndVisitingTeamNameEquals(
                    game.getGameDate(),
                    game.getHomeTeamName(),
                    game.getVisitingTeamName()
            );
            if (!duplicateHomeAndVisitTeams.isEmpty()) {
                 return "[Failed] Auto-generated game conflict detected!";
            }
            games.add(game);
        }
        // Save games
        gameRepository.saveAll(games);
        return "";
    }

}
