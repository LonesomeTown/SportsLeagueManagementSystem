package com.smu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate gameDate;
    @NotEmpty
    private String location;
    @NotEmpty
    private String homeTeamName;
    @NotEmpty
    private String visitTeamName;
    @NotNull
    private float homeTeamScore;
    @NotNull
    private float visitTeamScore;

}
