package com.smu.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * ScoringCriteria
 *
 * @author T.W 11/3/22
 */
@Data
public class ScoringCriteria {
    @MongoId
    private ObjectId id;
    private String seasonId;
    private Double wonPoints;
    private Double drawnPoints;
    private Double lostPoints;
}
