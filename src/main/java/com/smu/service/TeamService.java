package com.smu.service;

import com.smu.dto.Team;

import java.util.List;

/**
 * TeamService
 *
 * @author T.W 11/6/22
 */
public interface TeamService {

    Team findByTeamName(String teamName);
    /**
     * @param teamName teamName
     * @return {@link List}<{@link Team}>
     */
    List<Team> findAllTeams(String teamName);

    /**
     * @param team team
     */
    String saveTeam(Team team);

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

    /**
     * @return {@link List}<{@link String}>
     */
    List<String> findAllTeamsName();

    /**
     *  @param team team name
     *  @param leagueName leagueName
     */
    String moveTeam(Team team, String leagueName);

    /**
     * @param leagueName leagueName
     * @return {@link List}<{@link String}>
     */
    List<String> findTeamNamesByLeagueName(String leagueName);

}
