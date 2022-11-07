package com.smu.service;

import com.smu.dto.Season;
import com.smu.dto.Team;

import java.util.List;

/**
 * SeasonService
 *
 * @author T.W 11/6/22
 */
public interface SeasonService {
    /**
     * @return {@link List}<{@link Team}>
     */
    List<Season> findAllTeams();

    /**
     * @param season season
     */
    void saveSeason(Season season);

    /**
     * @param season season
     */
    void deleteSeason(Season season);
}
