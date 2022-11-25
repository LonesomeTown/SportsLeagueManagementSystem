package com.smu.dto;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * GameVo
 *
 * @author T.W 11/25/22
 */
@Data
public class GameVo {
    // Data Fields
    @MongoId
    private ObjectId id;
    @NotNull
    private String leagueName;
    @NotEmpty
    private String homeTeamName;
    @NotEmpty
    private String visitingTeamName;
    @NotEmpty
    private String location;
    @NotNull
    private LocalDate gameDate;
    private Double homeScore;
    private Double visitingScore;
}
