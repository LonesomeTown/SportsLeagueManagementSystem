package com.smu.service;

import com.smu.dto.ScoringCriteria;
import org.bson.types.ObjectId;

/**
 * ScoringCriteriaService
 *
 * @author T.W 11/21/22
 */
public interface ScoringCriteriaService {
    /**
     * @param seasonId seasonId
     * @return {@link ScoringCriteria}
     */
    ScoringCriteria findBySeasonId(ObjectId seasonId);

    void save(ScoringCriteria scoringCriteria);
}
