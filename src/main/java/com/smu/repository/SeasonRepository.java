package com.smu.repository;

import com.smu.dto.Season;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * SeasonRepository
 *
 * @author T.W 11/6/22
 */
public interface SeasonRepository extends MongoRepository<Season, ObjectId> {
}
