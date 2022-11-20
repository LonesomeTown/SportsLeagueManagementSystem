package com.smu.service;

import com.smu.dto.League;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * LeagueService
 *
 * @author T.W 11/4/22
 */
@Service
public interface LeagueService {
    /**
     * @return {@link Long}
     */
    Long countLeagues();

    /**
     * @param leagueName leagueName
     * @return {@link List}<{@link League}>
     */
    List<League> findAllLeagues(String leagueName);

    /**
     * @param league league
     */
    void saveLeague(League league);

    /**
     * @param league league
     */
    void deleteLeague(League league);

    /**
     * @param leagueName leagueName
     * @return {@link Integer}
     */
    Integer findSeasonNums(String leagueName);
}
