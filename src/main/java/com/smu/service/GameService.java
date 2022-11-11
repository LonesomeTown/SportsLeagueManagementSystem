package com.smu.service;

import com.smu.dto.Game;

import java.util.List;

/**
 *  Game Service
 *
 *  @author Z.S. 11/11/2022
 */
public interface GameService {
    /** Find all games played by the two teams input
     * @param homeTeam
     * @param visitTeam
     * @return: {@link List} <{@link Game}>
     */
    List<Game> findAllGames(String homeTeam, String visitTeam);
}
