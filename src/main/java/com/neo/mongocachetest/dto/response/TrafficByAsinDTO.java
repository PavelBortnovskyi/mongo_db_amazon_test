package com.neo.mongocachetest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class TrafficByAsinDTO {

    public Integer browserSessions;

    public Integer browserSessionsB2B;

    public Integer mobileAppSessions;

    public Integer mobileAppSessionsB2B;

    public Integer sessions;

    public Integer sessionsB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float browserSessionPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float browserSessionPercentageB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float mobileAppSessionPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float mobileAppSessionPercentageB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float sessionPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float sessionPercentageB2B;

    public Integer browserPageViews;

    public Integer browserPageViewsB2B;

    public Integer mobileAppPageViews;

    public Integer mobileAppPageViewsB2B;

    public Integer pageViews;

    public Integer pageViewsB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float browserPageViewsPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float browserPageViewsPercentageB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float mobileAppPageViewsPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float mobileAppPageViewsPercentageB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float pageViewsPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float pageViewsPercentageB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float buyBoxPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float buyBoxPercentageB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float unitSessionPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float unitSessionPercentageB2B;
}
