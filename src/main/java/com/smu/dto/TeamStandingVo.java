package com.smu.dto;

import lombok.Data;

/**
 * TeamStandingVo
 *
 * @author T.W 11/25/22
 */
@Data
public class TeamStandingVo {
    /**
     * team current standing according to the points in season
     */
    private Integer standing;
    /**
     * team name
     */
    private String teamName;
    /**
     * team total points in season
     */
    private Double points;
}
