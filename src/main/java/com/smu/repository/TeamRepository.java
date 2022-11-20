package com.smu.repository;

import com.smu.dto.Team;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * TeamRepository
 *
 * @author T.W 11/6/22
 */
public interface TeamRepository extends MongoRepository<Team, String> {
    Long countTeamByLeagueName(String leagueName);

    List<Team> findTeamsByNameContains(String name);

    List<Team> findTeamsByNameEquals(String name);

    List<Team> findTeamsByLeagueName(String leagueName);

}
