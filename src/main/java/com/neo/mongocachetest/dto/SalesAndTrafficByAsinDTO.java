package com.neo.mongocachetest.dto;

import com.neo.mongocachetest.annotation.CascadeSave;
import com.neo.mongocachetest.model.SaleByAsin;
import com.neo.mongocachetest.model.TrafficByAsin;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
public class SalesAndTrafficByAsinDTO {

    public String parentAsin;

    public SaleByAsinDTO salesByAsin;

    public TrafficByAsinDTO trafficByAsin;
}
