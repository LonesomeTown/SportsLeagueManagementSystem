package com.smu.service.impl;

import com.smu.constant.GameResultEnum;
import com.smu.dto.*;
import com.smu.repository.GameRepository;
import com.smu.repository.LeagueRepository;
import com.smu.repository.SeasonRepository;
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
import java.time.temporal.ChronoUnit;
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
    private final SeasonRepository seasonRepository;
    private final LeagueRepository leagueRepository;

    private Random random;

    {
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public GameServiceImpl(GameRepository gameRepository, SeasonService seasonService, TeamService teamService, ScoringCriteriaService scoringCriteriaService, SeasonRepository seasonRepository, LeagueRepository leagueRepository) {
        this.gameRepository = gameRepository;
        this.seasonService = seasonService;
        this.teamService = teamService;
        this.scoringCriteriaService = scoringCriteriaService;
        this.seasonRepository = seasonRepository;
        this.leagueRepository = leagueRepository;
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
            long randomSpan = random.nextInt(seasonLength == 0 ? 1 : (int) seasonLength);
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


    @Override
    public String updateCurrentDate(LocalDate currentDate) {
        List<Season> all = seasonRepository.findAll();
        all.sort(Comparator.comparing(Season::getEndDate).reversed());
        Season latestSeason = all.get(0);
        LocalDate latestDay = latestSeason.getEndDate();

        if (currentDate.isAfter(latestDay)) {
            //generate all the games result before current date
            List<Game> gamesByGameDateBefore = gameRepository.findGamesByGameDateBefore(currentDate);
            for (Game game : gamesByGameDateBefore) {
                if (null == game.getHomeScore() || null == game.getVisitingScore()) {
                    game.setHomeScore((double) random.nextInt(50));
                    game.setVisitingScore((double) random.nextInt(50));
                    gameRepository.save(game);
                }
            }

            // Generate a season between today and the date set
            Season season = new Season();
            int gap = (int) latestDay.until(currentDate, ChronoUnit.DAYS);
            season.setGamesNum(gap);
            season.setStartDate(latestDay.plusDays(1));
            List<String> leagueNames = leagueRepository.findAll().stream().map(League::getName).collect(Collectors.toList());
            season.setLeagueName(leagueNames.get(0));
            season.setEndDate(currentDate);
            Integer num = season.getGamesNum();

            // Save the season and check to assure tha the season schedule has no conflict with other seasons
            if (!"".equals(seasonService.saveSeason(season))) {
                updateCurrentDate(currentDate);
            }

            // Automatically generate games and load it to the season
            while (num > 0) {
                String autoGen = autoGenerateGamesBySeason(season.getId());
                if ("".equals(autoGen)) {
                    num--;
                }
            }

            return "Automatically generate games and season!";
        }
        // If the set date is in the past
        if (currentDate.isBefore(latestDay)) {
            // Here would generate a list that contains all the seasons before the set date
            List<Game> gameDateBefore = gameRepository.findGamesByGameDateBefore(currentDate);
            // Return a season whose start date were before the set date
            List<Season> seasonDateBefore = seasonRepository.findSeasonByStartDateBefore(currentDate);
            return "";
        }
        return "";
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
