package com.smu.service;

import com.smu.dto.Game;
import com.smu.dto.TeamGameRecordVo;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Game Service
 *
 * @author Z.S. 11/11/2022
 */
public interface GameService {
    /**
     * Find all games played by the two teams input
     *
     * @param homeTeam
     * @param visitTeam
     * @return: {@link List} <{@link Game}>
     */
    List<Game> findAllGames(String homeTeam, String visitTeam);

    List<Game> findGamesBySeason(ObjectId seasonId);

    /**
     * @param seasonId seasonId
     * @param teamName teamName
     * @return {@link List}<{@link Game}>
     */
    List<Game> findGamesBySeasonAndTeam(ObjectId seasonId, String teamName);

    /**
     * @param game game
     */
    String saveGame(Game game);

    /**
     * @param seasonId seasonId
     * @return
     */
    String autoGenerateGamesBySeason(ObjectId seasonId);

    void removeGame(Game game);

    List<TeamGameRecordVo> findGameRecordsByTeam(String teamName);

    /**
     * @param teamName      teamName
     * @param seasonId      seasonId
     * @param gamesInSeason gamesInSeason
     * @return {@link TeamGameRecordVo}
     */
    TeamGameRecordVo findGameRecordsByTeamInSeason(String teamName, ObjectId seasonId, List<Game> gamesInSeason);


}
