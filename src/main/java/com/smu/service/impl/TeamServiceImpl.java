package com.smu.service.impl;

import com.smu.dto.Team;
import com.smu.repository.TeamRepository;
import com.smu.service.TeamService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TeamServiceImpl
 *
 * @author T.W 11/6/22
 */
@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;

    public TeamServiceImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public List<Team> findAllTeams(String teamName) {
        return null;
    }

    @Override
    public void saveTeam(Team team) {

    }

    @Override
    public void deleteTeam(Team team) {

    }

    @Override
    public Long countTeamsByLeague(String leagueName) {
        return teamRepository.countTeamByLeagueName(leagueName);
    }
}
