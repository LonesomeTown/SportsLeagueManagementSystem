package com.smu.repository;

import com.smu.dto.Game;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Game repository
 *
 * @author Z.S 11/9/2022
 */
public interface GameRepository extends MongoRepository<Game, String> {

    /** Find the game by the teams that played the game
     *  @param homeTeamName
     *  @param visitingTeamName
     *  @return: a list of games that were played between the two teams
     *           including the details of each game
     */
    List<Game> findGameByHomeTeamNameEqualsAndVisitingTeamNameEquals(String homeTeamName, String visitingTeamName);

    /** Find all games within the same season
     *  @param seasonId: the object id of the season input
     *  @return: a list of games that are scheduled in the same season
     */
    List<Game> findGameBySeasonIdEquals(ObjectId seasonId);

    /** Find all games held on the game date input
     *  @param gameDate
     *  @return: all the games that were held on the game date specified
     */
    List<Game> findGameByGameDateEqualsAndHomeTeamNameEqualsAndVisitingTeamNameEquals(LocalDate gameDate, String homeTeamName, String visitingTeamName);
}
