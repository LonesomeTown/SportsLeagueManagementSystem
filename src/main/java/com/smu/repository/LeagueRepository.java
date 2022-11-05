package com.smu.repository;

import com.smu.dto.League;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * LeagueRepository
 *
 * @author T.W 11/4/22
 */
public interface LeagueRepository extends MongoRepository<League, String> {
    List<League> findLeaguesByNameContains(String name);
}
