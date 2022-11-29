package com.smu.service.impl;

import com.smu.dto.*;
import com.smu.repository.LeagueRepository;
import com.smu.repository.TeamRepository;
import com.smu.service.GameService;
import com.smu.service.LeagueService;
import com.smu.service.SeasonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LeagueServiceImpl
 *
 * @author T.W 11/4/22
 */
@Service
public class LeagueServiceImpl implements LeagueService {
    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final GameService gameService;
    private final SeasonService seasonService;

    public LeagueServiceImpl(LeagueRepository leagueRepository, TeamRepository teamRepository, GameService gameService, SeasonService seasonService) {
        this.leagueRepository = leagueRepository;
        this.teamRepository = teamRepository;
        this.gameService = gameService;
        this.seasonService = seasonService;
    }

    @Override
    public Long countLeagues() {
        return leagueRepository.count();
    }

    @Override
    public List<League> findAllLeagues(String leagueName) {
        if (StringUtils.isEmpty(leagueName)) {
            return leagueRepository.findAll();
        } else {
            return leagueRepository.findLeaguesByName(leagueName);
        }
    }

    @Override
    public String saveLeague(League league) {
        List<League> byCommissionerSsn = leagueRepository.findByCommissionerSsn(league.getCommissionerSsn());
        if (!CollectionUtils.isEmpty(byCommissionerSsn) && !league.getCommissionerName().equals(byCommissionerSsn.get(0).getCommissionerName())) {
            return "[Failed] SSN should be unique for each person!";
        }
        leagueRepository.save(league);
        return "";
    }

    @Override
    public void deleteLeague(League league) {
        leagueRepository.delete(league);
    }

    @Override
    public Integer findSeasonNums(String leagueName) {
        List<Team> teamsByLeagueName = teamRepository.findTeamsByLeagueName(leagueName);


        return null;
    }

    @Override
    public List<ChampionVo> findChampions(String leagueName) {
        if (StringUtils.isEmpty(leagueName)) {
            return new ArrayList<>();
        }
        List<ChampionVo> championVos = new ArrayList<>();
        List<Season> seasonsByLeagueName = seasonService.findSeasonsByLeagueName(leagueName);
        for (Season season : seasonsByLeagueName) {
            ChampionVo championVo = new ChampionVo();
            List<Game> gamesBySeason = gameService.findGamesBySeason(season.getId());
            List<String> homeTeam = gamesBySeason.stream().map(Game::getHomeTeamName).collect(Collectors.toList());
            List<String> visitingTeam = gamesBySeason.stream().map(Game::getVisitingTeamName).collect(Collectors.toList());
            homeTeam.addAll(visitingTeam);
            List<String> allTeams = homeTeam.stream().distinct().collect(Collectors.toList());
            List<TeamGameRecordVo> recordVos = new ArrayList<>();
            for (String team : allTeams) {
                TeamGameRecordVo gameRecordsByTeam = gameService.findGameRecordsByTeamInSeason(team, season.getId(), gamesBySeason);
                if (null != gameRecordsByTeam) {
                    recordVos.add(gameRecordsByTeam);
                }
            }
            recordVos.sort(Comparator.comparing(TeamGameRecordVo::getSumTotalPoints).reversed());
            for (int i = 0; i < recordVos.size(); i++) {
                if (i == 0 || (recordVos.get(i) == recordVos.get(i - 1))) {
                    TeamGameRecordVo topPointsTeam = recordVos.get(i);
                    championVo.setTeamName(topPointsTeam.getTeamName());
                    championVo.setSeasonId(season.getId());
                    championVo.setSeasonDuration(topPointsTeam.getSeasonDuration());
                    championVo.setPoints(topPointsTeam.getSumTotalPoints());
                    championVos.add(championVo);
                } else {
                    break;
                }
            }
        }
        return championVos;
    }
}
