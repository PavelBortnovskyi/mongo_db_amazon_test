package com.neo.mongocachetest.dto;

import com.neo.mongocachetest.model.BaseDocument;
import lombok.Data;

@Data
public class TrafficByAsinDTO {

    public Integer browserSessions;

    public Integer browserSessionsB2B;

    public Integer mobileAppSessions;

    public Integer mobileAppSessionsB2B;

    public Integer sessions;

    public Integer sessionsB2B;

    public Double browserSessionPercentage;

    public Double browserSessionPercentageB2B;

    public Double mobileAppSessionPercentage;

    public Double mobileAppSessionPercentageB2B;

    public Double sessionPercentage;

    public Double sessionPercentageB2B;

    public Integer browserPageViews;

    public Integer browserPageViewsB2B;

    public Integer mobileAppPageViews;

    public Integer mobileAppPageViewsB2B;

    public Integer pageViews;

    public Integer pageViewsB2B;

    public Double browserPageViewsPercentage;

    public Double browserPageViewsPercentageB2B;

    public Double mobileAppPageViewsPercentage;

    public Double mobileAppPageViewsPercentageB2B;

    public Double pageViewsPercentage;

    public Double pageViewsPercentageB2B;

    public Double buyBoxPercentage;

    public Double buyBoxPercentageB2B;

    public Double unitSessionPercentage;

    public Double unitSessionPercentageB2B;
}
