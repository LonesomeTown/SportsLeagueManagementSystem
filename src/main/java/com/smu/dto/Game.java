package com.smu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotEmpty;

/**
 * Game
 *
 * @author T.W 11/3/22
 * @author Z.S 11/9/22
 *
 */
@Data
public class Game {
    @MongoId
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.util.Date Date;
    @NotEmpty
    private String location;
    @NotEmpty
    private String homeTeam;
    @NotEmpty
    private String visitTeam;
    @NotEmpty
    private String score;
}
