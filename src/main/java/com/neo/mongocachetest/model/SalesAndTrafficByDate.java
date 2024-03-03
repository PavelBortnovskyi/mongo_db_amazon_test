package com.neo.mongocachetest.model;

import com.neo.mongocachetest.annotation.CascadeSave;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class SalesAndTrafficByDate extends BaseDocument {

    @Indexed
    public Date date;

    @DBRef
    @CascadeSave
    public SaleByDate salesByDate;

    @DBRef
    @CascadeSave
    public TrafficByDate trafficByDate;
}
