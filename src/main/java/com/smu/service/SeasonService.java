package com.smu.service;

import com.smu.dto.Season;
import com.smu.dto.Team;
import org.bson.types.ObjectId;

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
     * @return {@link List}<{@link Season}>
     */
    List<Season> findSeasonsByLeagueName(String leagueName);
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

    /**
     * @param id id
     * @return {@link Season}
     */
    Season findById(ObjectId id);

    Boolean updateCurrentDate(LocalDate localDate);

    /**
     * @param currentDate currentDate
     * @param leagueName leagueName
     * @return {@link List}<{@link Season}>
     */
    Season findSeasonByCurrentDateAndLeague(LocalDate currentDate, String leagueName);

}
