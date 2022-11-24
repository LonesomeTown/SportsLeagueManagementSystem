package com.smu.dto;

import lombok.Data;
import org.bson.types.ObjectId;

/**
 * TeamGameRecordsVo
 *
 * @author T.W 11/21/22
 */
@Data
public class TeamGameRecordVo {
    private String teamName;
    private ObjectId seasonId;
    private String seasonDuration;
    private Long gamesPlayed;
    private Long numsWon;
    private Long numsLoss;
    private Double sumScores;
    private Double sumOpponentScores;
    private Double sumTotalPoints;
}
