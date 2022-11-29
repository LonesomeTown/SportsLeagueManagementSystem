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
     * find season by its id number
     * @param id
     * @return {@link Season}
     */
    Season findSeasonByIdEquals(ObjectId id);

    /**
     * find if any startDate exist in new season duration
     *
     * @return {@link Season}
     */
    @Query(value = "{'startDate':{ $gte: ?0, $lte: ?1},'leagueName':?2}")
    List<Season> findSeasonByStartDateBetweenAndLeagueName(LocalDate startDate, LocalDate endDate, String leagueName);

    /**
     * find if andy endDate exist in new season duration
     *
     * @return {@link Season}
     */
    @Query(value = "{'startDate':{ $gte: ?0, $lte: ?1},'leagueName':?2}}")
    List<Season> findSeasonByEndDateBetweenAndLeagueName(LocalDate startDate, LocalDate endDate, String leagueName);

    /**
     * find the season whose start date is prior to the date input
     * @param startDate
     * @return {@link Season}
     */
    //@Query(value = "{'StartDate' : { $lte: ?0}}")
    List<Season> findSeasonByStartDateBeforeAndEndDateAfterAndLeagueName(LocalDate startDate, LocalDate endDate, String leagueName);

    List<Season> findSeasonByLeagueName(String leagueName);

    Season findByStartDateBeforeAndEndDateAfterAndLeagueName(LocalDate startDate,LocalDate endDate, String leagueName);

    List<Season> findSeasonByStartDateBefore(LocalDate date);

}
