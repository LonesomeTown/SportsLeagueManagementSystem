package com.smu.service.impl;

import com.smu.dto.Season;
import com.smu.dto.Team;
import com.smu.repository.SeasonRepository;
import com.smu.repository.TeamRepository;
import com.smu.service.TeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TeamServiceImpl
 *
 * @author T.W 11/6/22
 */
@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final SeasonRepository seasonRepository;

    public TeamServiceImpl(TeamRepository teamRepository, SeasonRepository seasonRepository) {
        this.teamRepository = teamRepository;
        this.seasonRepository = seasonRepository;
    }

    @Override
    public List<Team> findAllTeams(String teamName) {
        if (StringUtils.isEmpty(teamName)) {
            return teamRepository.findAll();
        } else {
            return teamRepository.findTeamsByNameContains(teamName);
        }
    }

    @Override
    public void saveTeam(Team team) {
        Optional<Team> oldTeam = teamRepository.findById(team.getName());
        if (oldTeam.isPresent() && !oldTeam.get().getLeagueName().equals(team.getLeagueName())) {
            this.moveTeam(team, team.getLeagueName());
        } else {
            teamRepository.save(team);
        }
    }

    @Override
    public void deleteTeam(Team team) {
        teamRepository.delete(team);
    }

    @Override
    public Long countTeamsByLeague(String leagueName) {
        return teamRepository.countTeamByLeagueName(leagueName);
    }

    @Override
    public String findFieldByTeamName(String teamName) {
        List<Team> teamsByNameEquals = teamRepository.findTeamsByNameEquals(teamName);
        if (!CollectionUtils.isEmpty(teamsByNameEquals)) {
            return teamsByNameEquals.get(0).getField();
        }
        return null;
    }

    @Override
    public List<String> findAllTeamsName() {
        List<Team> allTeams = this.findAllTeams("");
        if (CollectionUtils.isEmpty(allTeams)) {
            return new ArrayList<>();
        } else {
            return allTeams.stream().map(Team::getName).collect(Collectors.toList());
        }
    }

    @Override
    public String moveTeam(Team team, String leagueName) {
        // Get current date
        LocalDate current = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        current = LocalDate.parse(current.format(formatter));
        // Compare the date with season schedule
        // Violation case:
        //    seasonStart -- current -- seasonEnd
        List<Season> seasonsByStartDateBeforeAndEndDateAfter = seasonRepository.findSeasonByStartDateBeforeAndEndDateAfter(current, current);
        if (!seasonsByStartDateBeforeAndEndDateAfter.isEmpty()) {
            team.setLeagueName(leagueName);
            teamRepository.save(team);
            return "";
        } else {
            return "[Failed] Unable to change league due to season date conflict!";
        }
    }


}
