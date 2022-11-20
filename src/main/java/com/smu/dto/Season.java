package com.smu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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
    @NotEmpty
    private String leagueName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate endDate;
    @NotNull
    private Integer gamesNum;

}
