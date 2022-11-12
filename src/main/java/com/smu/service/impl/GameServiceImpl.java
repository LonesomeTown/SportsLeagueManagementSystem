package com.smu.service.impl;

import com.smu.dto.Game;
import com.smu.repository.GameRepository;
import com.smu.service.GameService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

/** Game Service Implementation
 *
 *  @author Z.S. 11/12/2022
 *
 */
@Service
public class GameServiceImpl implements GameService {
    // Data fields
    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public List<Game> findAllGames(String homeTeam, String visitTeam) {
        return gameRepository.findGameByHomeTeamNameEqualsAndVisitingNameEquals(homeTeam, visitTeam);
    }

    @Override
    public List<Game> findGamesBySeason(ObjectId seasonId) {
        return gameRepository.findGameBySeasonIdEquals(seasonId);
    }


    // Constructor

}
