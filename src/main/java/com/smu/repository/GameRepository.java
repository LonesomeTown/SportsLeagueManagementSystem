package com.smu.repository;

import com.smu.dto.Game;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Game repository
 *
 * @author Z.S 11/9/2022
 */
public interface GameRepository extends MongoRepository<Game, String> {

    /**
     * Find the game by the teams that played the game
     *
     * @param homeTeamName
     * @param visitingTeamName
     * @return: a list of games that were played between the two teams
     * including the details of each game
     */
    List<Game> findGameByHomeTeamNameEqualsAndVisitingTeamNameEquals(String homeTeamName, String visitingTeamName);

    /**
     * Find all games within the same season
     *
     * @param seasonId: the object id of the season input
     * @return: a list of games that are scheduled in the same season
     */
    List<Game> findGameBySeasonIdEquals(ObjectId seasonId);

    /**
     * Find all games held on the game date input
     *
     * @param gameDate
     * @return: all the games that were held on the game date specified
     */
    List<Game> findGameByGameDateEqualsAndHomeTeamNameEqualsAndVisitingTeamNameEquals(LocalDate gameDate, String homeTeamName, String visitingTeamName);

    /**
     * @param teamName1 teamName1
     * @param teamName2 teamName2
     * @return {@link List}<{@link Game}>
     */
    List<Game> findGamesByHomeTeamNameOrVisitingTeamName(String teamName1, String teamName2);

    /**
     * @param homeTeamName
     * @param visitingName
     * @return {@link List}<{@link Game}>
     */
//    @Query(value = "{'$and':['$or':[{'homeTeamName':?0},'{visitingTeamName':?1}],'seasonId':{'$oid':?2}]}")
    List<Game> findGamesByHomeTeamNameAndSeasonIdOrVisitingTeamNameAndSeasonId(String homeTeamName, ObjectId seasonId1, String visitingName, ObjectId seasonId2);

    List<Game> findGamesByGameDateBefore(LocalDate date);

    void removeAllBySeasonId(ObjectId seasonId);

    void removeAllByHomeTeamNameOrVisitingTeamName(String homeTeam, String visitingTeam);
}
