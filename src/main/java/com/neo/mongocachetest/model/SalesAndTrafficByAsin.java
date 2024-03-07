package com.neo.mongocachetest.model;

import com.neo.mongocachetest.annotation.CascadeSave;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document
public class SalesAndTrafficByAsin extends BaseDocument{

    @Indexed(unique = true)
    public String parentAsin;

    @DBRef
    @CascadeSave
    public SaleByAsin salesByAsin;

    @DBRef
    @CascadeSave
    public TrafficByAsin trafficByAsin;
}
