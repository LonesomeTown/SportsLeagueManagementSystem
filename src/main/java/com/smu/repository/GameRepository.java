//package com.smu.repository;
//
//import com.smu.dto.Game;
//import org.springframework.data.mongodb.repository.MongoRepository;
//
//import java.util.List;
//
///**
// * Game repository
// *
// * @author Z.S 11/9/2022
// */
//public interface GameRepository extends MongoRepository<Game, String> {
//
//    /** Find the game by the teams that played the game
//     *  @param homeTeam
//     *  @param visitTeam
//     *  @return: a list of games that were played between the two teams
//     */
//    List<Game> findGameByNamesContains(String homeTeam, String visitTeam);
//}
