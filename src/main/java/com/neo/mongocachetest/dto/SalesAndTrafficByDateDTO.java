package com.neo.mongocachetest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SalesAndTrafficByDateDTO {

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    public Date date;

    public SaleByDateDTO salesByDate;

    public TrafficByDateDTO trafficByDate;
}
