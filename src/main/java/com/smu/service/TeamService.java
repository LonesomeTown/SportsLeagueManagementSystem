package com.smu.service;

import com.smu.dto.League;
import com.smu.dto.Team;

import java.util.List;

/**
 * TeamService
 *
 * @author T.W 11/6/22
 */
public interface TeamService {
    /**
     * @param teamName teamName
     * @return {@link List}<{@link Team}>
     */
    List<Team> findAllTeams(String teamName);

    /**
     * @param team team
     */
    void saveTeam(Team team);

    /**
     * @param team team
     */
    void deleteTeam(Team team);

    /**
     * @param leagueName leagueName
     * @return {@link Long}
     */
    Long countTeamsByLeague(String leagueName);

    /**
     * @param teamName teamName
     * @return {@link String}
     */
    String findFieldByTeamName(String teamName);
}
