package com.smu.dto;

import cn.hutool.core.lang.ObjectId;
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
    @NotEmpty
    private String homeTeamName;
    @NotEmpty
    private String visitingTeamName;
    @NotEmpty
    private String location;
    @NotNull
    private LocalDate gameDate;
    @NotNull
    private Float homeScore;
    @NotNull
    private Float visitingScore;
    @NotNull
    private ObjectId seasonId;

}
