package com.smu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private static final long serialVersionUID = 1905122041950251207L;
    @MongoId
    private ObjectId id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotEmpty
    private LocalDate gameDate;
    @NotEmpty
    private String location;
    @NotEmpty
    private String homeTeam;
    @NotEmpty
    private String visitTeam;
    @NotNull
    private float score;

}
