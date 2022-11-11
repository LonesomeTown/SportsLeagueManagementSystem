package com.smu.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * InitializeVo
 *
 * @author T.W 11/6/22
 */
@Data
public class InitializeVo implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    @NotEmpty
    private String name;
    @NotEmpty
    private String city;
    @NotEmpty
    private String field;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private Integer gamesNum;

}
