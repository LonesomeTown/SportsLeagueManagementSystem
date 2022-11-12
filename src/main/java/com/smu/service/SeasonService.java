package com.smu.service;

import com.smu.dto.Season;
import com.smu.dto.Team;

import java.time.LocalDate;
import java.util.List;

/**
 * SeasonService
 *
 * @author T.W 11/6/22
 */
public interface SeasonService {

    /**
     * @return {@link List}<{@link Season}>
     */
    List<Season> findSeasonsByStartDate(LocalDate startDate);
    /**
     * @return {@link List}<{@link Team}>
     */
    List<Season> findAllSeasons();

    /**
     * @param season season
     */
    String saveSeason(Season season);

    /**
     * @param season season
     */
    void deleteSeason(Season season);
}