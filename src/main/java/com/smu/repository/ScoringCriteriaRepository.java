package com.smu.repository;

import com.smu.dto.ScoringCriteria;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * ScoringCriteriaRepository
 *
 * @author T.W 11/21/22
 */
public interface ScoringCriteriaRepository extends MongoRepository<ScoringCriteria, ObjectId> {
    ScoringCriteria findBySeasonId(ObjectId seasonId);
}
