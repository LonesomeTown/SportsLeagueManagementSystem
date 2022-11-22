package com.smu.service.impl;

import com.smu.dto.ScoringCriteria;
import com.smu.repository.ScoringCriteriaRepository;
import com.smu.service.ScoringCriteriaService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

/**
 * ScoringCriteriaServiceImpl
 *
 * @author T.W 11/21/22
 */
@Service
public class ScoringCriteriaServiceImpl implements ScoringCriteriaService {
    private final ScoringCriteriaRepository scoringCriteriaRepository;

    public ScoringCriteriaServiceImpl(ScoringCriteriaRepository scoringCriteriaRepository) {
        this.scoringCriteriaRepository = scoringCriteriaRepository;
    }

    @Override
    public ScoringCriteria findBySeasonId(ObjectId seasonId) {
        return this.scoringCriteriaRepository.findBySeasonId(seasonId);
    }

    @Override
    public void save(ScoringCriteria scoringCriteria) {
         scoringCriteriaRepository.save(scoringCriteria);
    }
}
