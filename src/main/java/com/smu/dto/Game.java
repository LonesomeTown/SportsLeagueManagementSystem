package com.smu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

/**
 * Game
 *
 * @author S.Z 11/11/22
 * @author T.W 11/3/22
 */
@Data
public class Game {
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotEmpty
    private LocalDate gameDate;
    @NotEmpty
    private String location;
    @NotEmpty
    private String homeTeam;
    @NotEmpty
    private String visitTeam;
    @NotEmpty
    private String score;

}
