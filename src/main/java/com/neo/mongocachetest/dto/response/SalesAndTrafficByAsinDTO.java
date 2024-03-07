package com.neo.mongocachetest.dto.response;

import lombok.Data;


@Data
public class SalesAndTrafficByAsinDTO {

    public String parentAsin;

    public SaleByAsinDTO salesByAsin;

    public TrafficByAsinDTO trafficByAsin;
}
