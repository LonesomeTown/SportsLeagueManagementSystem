package com.smu.dto;

import lombok.Data;
import org.bson.types.ObjectId;

/**
 * ChampionVo
 *
 * @author T.W 11/21/22
 */
@Data
public class ChampionVo {
    private ObjectId seasonId;
    private String teamName;
    private String seasonDuration;
    private Double points;
}
