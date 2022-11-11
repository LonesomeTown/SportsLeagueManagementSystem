package com.smu.repository;

import com.smu.dto.Season;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * SeasonRepository
 *
 * @author T.W 11/6/22
 */
public interface SeasonRepository extends MongoRepository<Season, ObjectId> {
    /**
     * @param startDate startDate
     * @return {@link Season}
     */
    Season findSeasonByStartDateEquals(LocalDate startDate);

    /**
     * find if any startDate exist in new season duration
     *
     * @return {@link Season}
     */
    @Query(value = "{'startDate':{ $gte: ?0, $lte: ?1}}")
    List<Season> findSeasonByStartDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * find if andy endDate exist in new season duration
     *
     * @return {@link Season}
     */
    @Query(value = "{'startDate':{ $gte: ?0, $lte: ?1}}")
    List<Season> findSeasonByEndDateBetween(LocalDate startDate, LocalDate endDate);
}
