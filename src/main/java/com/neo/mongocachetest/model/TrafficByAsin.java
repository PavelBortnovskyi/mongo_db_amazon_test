package com.neo.mongocachetest.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class TrafficByAsin extends BaseDocument{

    public Integer browserSessions;

    public Integer browserSessionsB2B;

    public Integer mobileAppSessions;

    public Integer mobileAppSessionsB2B;

    public Integer sessions;

    public Integer sessionsB2B;

    public Float browserSessionPercentage;

    public Float browserSessionPercentageB2B;

    public Float mobileAppSessionPercentage;

    public Float mobileAppSessionPercentageB2B;

    public Float sessionPercentage;

    public Float sessionPercentageB2B;

    public Integer browserPageViews;

    public Integer browserPageViewsB2B;

    public Integer mobileAppPageViews;

    public Integer mobileAppPageViewsB2B;

    public Integer pageViews;

    public Integer pageViewsB2B;

    public Float browserPageViewsPercentage;

    public Float browserPageViewsPercentageB2B;

    public Float mobileAppPageViewsPercentage;

    public Float mobileAppPageViewsPercentageB2B;

    public Float pageViewsPercentage;

    public Float pageViewsPercentageB2B;

    public Float buyBoxPercentage;

    public Float buyBoxPercentageB2B;

    public Float unitSessionPercentage;

    public Float unitSessionPercentageB2B;
}
