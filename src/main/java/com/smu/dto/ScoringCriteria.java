package com.smu.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotNull;

/**
 * ScoringCriteria
 *
 * @author T.W 11/3/22
 */
@Data
public class ScoringCriteria {
    @MongoId
    private ObjectId id;
    private ObjectId seasonId;
    @NotNull
    private Double wonPoints;
    @NotNull
    private Double drawnPoints;
    @NotNull
    private Double lostPoints;
}
