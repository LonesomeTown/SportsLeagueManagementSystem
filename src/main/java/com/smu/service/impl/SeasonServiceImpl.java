package com.smu.service.impl;

import com.smu.dto.Season;
import com.smu.service.SeasonService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SeasonServiceImpl
 *
 * @author T.W 11/6/22
 */
@Service
public class SeasonServiceImpl implements SeasonService {
    @Override
    public List<Season> findAllTeams() {
        return null;
    }

    @Override
    public void saveSeason(Season season) {

    }

    @Override
    public void deleteSeason(Season season) {

    }
}
