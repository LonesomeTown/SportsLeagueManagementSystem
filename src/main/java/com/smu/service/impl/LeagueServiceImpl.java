package com.smu.service.impl;

import com.smu.dto.League;
import com.smu.repository.LeagueRepository;
import com.smu.service.LeagueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * LeagueServiceImpl
 *
 * @author T.W 11/4/22
 */
@Service
public class LeagueServiceImpl implements LeagueService {
    private final LeagueRepository leagueRepository;

    public LeagueServiceImpl(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
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
            return leagueRepository.findLeaguesByNameContains(leagueName);
        }
    }

    @Override
    public void saveLeague(League league) {
        leagueRepository.save(league);
    }

    @Override
    public void deleteLeague(League league) {
        leagueRepository.delete(league);
    }
}
