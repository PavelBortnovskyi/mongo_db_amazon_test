package com.neo.mongocachetest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neo.mongocachetest.annotation.CascadeSave;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class SalesAndTrafficByDate extends BaseDocument {

    @Indexed(unique = true)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    public Date date;

    @DBRef
    @CascadeSave
    public SaleByDate salesByDate;

    @DBRef
    @CascadeSave
    public TrafficByDate trafficByDate;
}
