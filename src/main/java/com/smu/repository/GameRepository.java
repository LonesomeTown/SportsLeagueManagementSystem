package com.smu.repository;

import com.smu.dto.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * Game repository
 *
 * @author Z.S 11/9/2022
 */
public interface GameRepository extends MongoRepository<Game, String> {
    List<Game> findGameByDate(Date date);
}
