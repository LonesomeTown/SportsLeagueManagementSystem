package com.smu.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;

/**
 * Team
 *
 * @author T.W 11/3/22
 */
@Data
public class Team implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    @MongoId
    private String name;
    private String city;
    private String field;
    private String leagueName;
    private Float rating;
}
