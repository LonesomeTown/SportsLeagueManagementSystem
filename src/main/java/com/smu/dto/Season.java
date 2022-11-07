package com.smu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.util.Date;

/**
 * Season
 *
 * @author T.W 11/3/22
 */
@Data
public class Season implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    @MongoId
    private ObjectId id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private Integer gamesNum;

}
