package com.smu.repository;

import com.smu.dto.Game;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Game repository
 *
 * @author Z.S 11/9/2022
 */
public interface GameRepository extends MongoRepository<Game, String> {

    /** Find the game by the teams that played the game
     *  @param homeTeam
     *  @param visitTeam
     *  @return: a list of games that were played between the two teams
     *           including the details of each game
     */
    List<Game> findGameByHomeTeamNameEqualsAndVisitingTeamNameEquals(String homeTeam, String visitTeam);

    /** Find all games within the same season
     * @param seasonId: the object id of the season input
     * @return: a list of games that are scheduled in the same season
     */
    List<Game> findGameBySeasonIdEquals(ObjectId seasonId);
}
