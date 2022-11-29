package com.smu.service.impl;

import com.smu.dto.Season;
import com.smu.repository.SeasonRepository;
import com.smu.service.SeasonService;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SeasonServiceImpl
 *
 * @author T.W 11/6/22
 */
@Service
public class SeasonServiceImpl implements SeasonService {
    private final SeasonRepository seasonRepository;

    public SeasonServiceImpl(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    @Override
    public List<Season> findSeasonsByStartDate(LocalDate startDate) {
        if (null == startDate) {
            return this.findAllSeasons();
        } else {
            List<Season> seasons = new ArrayList<>();
            Season seasonByStartDateEquals = seasonRepository.findSeasonByStartDateEquals(startDate);
            seasons.add(seasonByStartDateEquals);
            return seasons;
        }
    }

    @Override
    public List<Season> findSeasonsByLeagueName(String leagueName) {
        if (StringUtils.isEmpty(leagueName)) {
            return this.findAllSeasons();
        } else {
            return this.seasonRepository.findSeasonByLeagueName(leagueName);
        }
    }

    @Override
    public List<Season> findAllSeasons() {
        return seasonRepository.findAll();
    }

    @Override
    public String saveSeason(Season season) {
        LocalDate startDate = season.getStartDate();
        LocalDate endDate = season.getEndDate();
        // newStart -- oldStart -- newEnd
        List<Season> startDateBetween = seasonRepository.findSeasonByStartDateBetweenAndLeagueName(startDate, endDate, season.getLeagueName());
        // newStart -- oldEnd -- newEnd
        List<Season> endDateBetween = seasonRepository.findSeasonByEndDateBetweenAndLeagueName(startDate, endDate, season.getLeagueName());
        // oldStart -- newStart -- newEnd -- oldEnd
        List<Season> startDateBeforeAndEndDateAfter = seasonRepository.findSeasonByStartDateBeforeAndEndDateAfterAndLeagueName(startDate, endDate, season.getLeagueName());
        // Season ID identifier
        ObjectId seasonId = season.getId();
        Season duplicateId = seasonRepository.findSeasonByIdEquals(seasonId);
        // Conditions
        if (duplicateId != null) {
            seasonRepository.save(season);
        } else if (!CollectionUtils.isEmpty(startDateBetween) || !CollectionUtils.isEmpty(endDateBetween)) {
            return "[Failed] Start date or end date has already exited in other seasons!";
        } else if (!CollectionUtils.isEmpty(startDateBeforeAndEndDateAfter)) {
            return "[Failed] Season date schedule overlaps another existing season!";
        } else {
            seasonRepository.save(season);
        }
        return "";
    }

    @Override
    public void deleteSeason(Season season) {
        seasonRepository.delete(season);
    }

    @Override
    public Season findById(ObjectId id) {
        Optional<Season> byId = seasonRepository.findById(id);
        return byId.orElseGet(Season::new);
    }

    @Override
    public Boolean updateCurrentDate(LocalDate localDate) {
        return null;
    }

    @Override
    public Season findSeasonByCurrentDateAndLeague(LocalDate currentDate, String leagueName) {
        return seasonRepository.findByStartDateBeforeAndEndDateAfterAndLeagueName(currentDate, currentDate, leagueName);
    }
}
