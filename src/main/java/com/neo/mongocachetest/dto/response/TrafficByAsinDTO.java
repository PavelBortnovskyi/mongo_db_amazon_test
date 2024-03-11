package com.neo.mongocachetest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neo.mongocachetest.model.BaseDocument;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
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
    public Double browserSessionPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Double browserSessionPercentageB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Double mobileAppSessionPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Double mobileAppSessionPercentageB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Double sessionPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Double sessionPercentageB2B;

    public Integer browserPageViews;

    public Integer browserPageViewsB2B;

    public Integer mobileAppPageViews;

    public Integer mobileAppPageViewsB2B;

    public Integer pageViews;

    public Integer pageViewsB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Double browserPageViewsPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Double browserPageViewsPercentageB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Double mobileAppPageViewsPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Double mobileAppPageViewsPercentageB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Double pageViewsPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Double pageViewsPercentageB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Double buyBoxPercentage;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Double buyBoxPercentageB2B;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Double unitSessionPercentage;

    public Double unitSessionPercentageB2B;
}
