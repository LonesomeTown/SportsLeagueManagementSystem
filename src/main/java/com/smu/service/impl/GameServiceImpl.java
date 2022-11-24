package com.smu.service.impl;

import com.smu.constant.GameResultEnum;
import com.smu.dto.Game;
import com.smu.dto.ScoringCriteria;
import com.smu.dto.Season;
import com.smu.dto.TeamGameRecordVo;
import com.smu.repository.GameRepository;
import com.smu.service.GameService;
import com.smu.service.ScoringCriteriaService;
import com.smu.service.SeasonService;
import com.smu.service.TeamService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ScoringCriteriaService scoringCriteriaService;

    private Random random;

    {
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public GameServiceImpl(GameRepository gameRepository, SeasonService seasonService, TeamService teamService, ScoringCriteriaService scoringCriteriaService) {
        this.gameRepository = gameRepository;
        this.seasonService = seasonService;
        this.teamService = teamService;
        this.scoringCriteriaService = scoringCriteriaService;
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
    public List<Game> findGamesBySeasonAndTeam(ObjectId seasonId, String teamName) {
        return gameRepository.findGamesByHomeTeamNameAndSeasonIdOrVisitingTeamNameAndSeasonId(teamName, seasonId, teamName, seasonId);
    }

    @Override
    public String saveGame(Game game) {
        ObjectId seasonId = game.getSeasonId();
        Season season = seasonService.findById(seasonId);
        Integer gamesNum = season.getGamesNum();
        if (this.ifDuplicateGameInfo(game)) {
            return "[Failed] Game conflict detected!";
        }
        List<Game> gameBySeasonId = gameRepository.findGameBySeasonIdEquals(seasonId);
        if (gameBySeasonId.size() < gamesNum || null != game.getId()) {
            if (null != game.getHomeScore() && null != game.getVisitingScore()) {
                if (game.getHomeScore() > game.getVisitingScore()) {
                    game.setGameResult(game.getHomeTeamName());
                } else if (game.getHomeScore() < game.getVisitingScore()) {
                    game.setGameResult(game.getVisitingTeamName());
                } else {
                    game.setGameResult(GameResultEnum.DRAWN.name());
                }
            }
            gameRepository.save(game);
            return "";
        } else {
            return "[Failed] Games in this season are already full!";
        }
    }

    @Override
    public String autoGenerateGamesBySeason(ObjectId seasonId) {
        Season season = seasonService.findById(seasonId);
        Integer gamesNum = season.getGamesNum();
        List<Game> games = new ArrayList<>();
        List<String> allTeamsName = teamService.findTeamNamesByLeagueName(season.getLeagueName());
        if (CollectionUtils.isEmpty(allTeamsName)) {
            return null;
        }
        List<Game> gameBySeasonId = gameRepository.findGameBySeasonIdEquals(seasonId);
        if (!CollectionUtils.isEmpty(gameBySeasonId)) {
            // Games num is equal to the require games number - games already exist
            gamesNum = gamesNum - gameBySeasonId.size();
        }
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
            String field = teamService.findFieldByTeamName(allTeamsName.get(randomHomeTeamNameIndex));

            //localDate date
            long seasonLength = season.getEndDate().toEpochDay() - season.getStartDate().toEpochDay();
            long randomSpan = random.nextInt((int) seasonLength);
            LocalDate gameDate = season.getStartDate().plusDays(randomSpan);
            //List<String> duplicateHomeAndVisitTeams = gameRepository.findGameByGameDateEqualsAndHomeTeamNameEqualsAndVisitingTeamNameEquals()
            game.setHomeTeamName(allTeamsName.get(randomHomeTeamNameIndex));
            game.setVisitingTeamName(allTeamsName.get(randomVisitingTeamNameIndex));
            game.setLocation(field);
            game.setGameDate(gameDate);
            if (this.ifDuplicateGameInfo(game)) {
                return "[Failed] Auto-generated game conflict detected!";
            }
            games.add(game);
        }
        // Save games
        gameRepository.saveAll(games);
        return "";
    }

    @Override
    public void removeGame(Game game) {
        if (null == game || null == game.getId()) {
            return;
        }
        this.gameRepository.delete(game);
    }

    @Override
    public List<TeamGameRecordVo> findGameRecordsByTeam(String teamName) {
        List<TeamGameRecordVo> results = new ArrayList<>();
        List<Game> games = this.gameRepository.findGamesByHomeTeamNameOrVisitingTeamName(teamName, teamName);
        if (CollectionUtils.isEmpty(games)) {
            return results;
        }
        //filter all games of 'teamName' and group by season
        Map<ObjectId, List<Game>> gamesGroupBySeason = games.stream().collect(Collectors.groupingBy(Game::getSeasonId));
        for (Map.Entry<ObjectId, List<Game>> objectIdListEntry : gamesGroupBySeason.entrySet()) {
            //calculate the scores and points of each season
            TeamGameRecordVo recordVo = findGameRecordsByTeamInSeason(teamName, objectIdListEntry.getKey(), objectIdListEntry.getValue());
            if (null != recordVo) {
                results.add(recordVo);
            }
        }
        return results;
    }

    @Override
    public TeamGameRecordVo findGameRecordsByTeamInSeason(String teamName, ObjectId seasonId, List<Game> gamesInSeason) {
        gamesInSeason.removeIf(game -> null == game.getHomeScore() || null == game.getVisitingScore());
        if (CollectionUtils.isEmpty(gamesInSeason)) {
            return null;
        }
        //calculate the scores and points of each season
        TeamGameRecordVo recordVo = new TeamGameRecordVo();
        recordVo.setTeamName(teamName);
        //find the games that have played in this season
        long gamePlayed = gamesInSeason.size();
        recordVo.setSeasonId(seasonId);
        recordVo.setGamesPlayed(gamePlayed);
        //find the season duration
        Season season = seasonService.findById(seasonId);
        recordVo.setSeasonDuration(season.getStartDate().format(DateTimeFormatter.BASIC_ISO_DATE) + "~" + season.getEndDate().format(DateTimeFormatter.BASIC_ISO_DATE));
        //find the games records of this team which is as a home team in this season and calculate the score
        List<Game> asHomeTeam = gamesInSeason.stream().filter(game -> teamName.equals(game.getHomeTeamName())).collect(Collectors.toList());
        double sumHomeScore = asHomeTeam.stream().mapToDouble(Game::getHomeScore).sum();
        double sumOpponentVisitingScore = asHomeTeam.stream().mapToDouble(Game::getVisitingScore).sum();
        //find the games records of this team which is as a visiting team in this season and calculate the score
        List<Game> asVisitingTeam = gamesInSeason.stream().filter(game -> teamName.equals(game.getVisitingTeamName())).collect(Collectors.toList());
        double sumVisitingScore = asVisitingTeam.stream().mapToDouble(Game::getVisitingScore).sum();
        double sumOpponentHomeScore = asVisitingTeam.stream().mapToDouble(Game::getHomeScore).sum();
        //the sum scores will be the score of the team as a home team + the score of the team as a visiting team
        recordVo.setSumScores(sumHomeScore + sumVisitingScore);
        //the opponentScores is the same thing
        recordVo.setSumOpponentScores(sumOpponentVisitingScore + sumOpponentHomeScore);
        //find the won games number of this team
        long wonGamesNum = gamesInSeason.stream().filter(game -> teamName.equals(game.getGameResult())).count();
        recordVo.setNumsWon(wonGamesNum);
        //find the drawn games number of this team
        long drawnGamesNum = gamesInSeason.stream().filter(game -> GameResultEnum.DRAWN.name().equals(game.getGameResult())).count();
        //calculate the loss games number of this team
        long lossGamesNum = gamePlayed - wonGamesNum - drawnGamesNum;
        recordVo.setNumsLoss(lossGamesNum);
        //calculate the total points of this season by the scoring criteria
        ScoringCriteria scoringCriteria = scoringCriteriaService.findBySeasonId(seasonId);
        double wonPoints = null == scoringCriteria.getWonPoints() ? GameResultEnum.WON.getPoints() : scoringCriteria.getWonPoints();
        double drawnPoints = null == scoringCriteria.getDrawnPoints() ? GameResultEnum.DRAWN.getPoints() : scoringCriteria.getDrawnPoints();
        double lossPoints = null == scoringCriteria.getDrawnPoints() ? GameResultEnum.LOST.getPoints() : scoringCriteria.getLostPoints();
        recordVo.setSumTotalPoints(wonPoints * wonGamesNum + drawnPoints * drawnGamesNum + lossPoints * lossGamesNum);
        return recordVo;
    }


    private boolean ifDuplicateGameInfo(Game game) {
        List<Game> duplicateHomeAndVisitTeams = gameRepository.findGameByGameDateEqualsAndHomeTeamNameEqualsAndVisitingTeamNameEquals(
                game.getGameDate(),
                game.getHomeTeamName(),
                game.getVisitingTeamName()
        );
        if (!CollectionUtils.isEmpty(duplicateHomeAndVisitTeams)) {
            duplicateHomeAndVisitTeams.removeIf(g -> game.getId().equals(g.getId()));
        }
        return !CollectionUtils.isEmpty(duplicateHomeAndVisitTeams);
    }

}
