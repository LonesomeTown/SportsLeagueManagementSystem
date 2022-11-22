package com.smu.dto;


import com.smu.constant.GameResultEnum;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Game
 *
 * @author S.Z 11/11/22
 * @author T.W 11/3/22
 */
@Data
public class Game implements Serializable {
    // Data Fields
    @MongoId
    private ObjectId id;
    @NotNull
    private ObjectId seasonId;
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
    private String gameResult;
}
