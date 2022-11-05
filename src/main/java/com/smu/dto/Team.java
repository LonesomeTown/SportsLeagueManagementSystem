package com.smu.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * Team
 *
 * @author T.W 11/3/22
 */
@Data
public class Team {
    @MongoId
    private String name;
    private String city;
    private String field;
    private League league;
    private Float rating;
}
