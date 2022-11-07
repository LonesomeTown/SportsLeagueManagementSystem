package com.smu.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Game
 *
 * @author T.W 11/3/22
 */
@Data
public class Game {
    @NotEmpty
    private String homeTeamName;
    @NotEmpty
    private String visitingTeamName;
    @NotEmpty
    private String location;
    @NotNull
    private Date gameDate;
    @NotNull
    private Float homeScore;
    @NotNull
    private Float visitingScore;

}
