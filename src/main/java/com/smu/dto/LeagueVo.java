package com.smu.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * LeagueVo
 *
 * @author T.W 11/7/22
 */
@Data
public class LeagueVo implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    @NotEmpty
    private String name;
    @NotEmpty
    private String commissionerName;
    @NotEmpty
    private String commissionerSsn;
    private Integer seasonsNum;
}
