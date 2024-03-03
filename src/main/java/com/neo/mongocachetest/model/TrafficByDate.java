package com.neo.mongocachetest.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class TrafficByDate extends BaseDocument{

    public Integer browserPageViews;

    public Integer browserPageViewsB2B;

    public Integer mobileAppPageViews;

    public Integer mobileAppPageViewsB2B;

    public Integer pageViews;

    public Integer pageViewsB2B;

    public Integer browserSessions;

    public Integer browserSessionsB2B;

    public Integer mobileAppSessions;

    public Integer mobileAppSessionsB2B;

    public Integer sessions;

    public Integer sessionsB2B;

    public Double buyBoxPercentage;

    public Double buyBoxPercentageB2B;

    public Double orderItemSessionPercentage;

    public Double orderItemSessionPercentageB2B;

    public Double unitSessionPercentage;

    public Double unitSessionPercentageB2B;

    public Integer averageOfferCount;

    public Integer averageParentItems;

    public Integer feedbackReceived;

    public Integer negativeFeedbackReceived;

    public Integer receivedNegativeFeedbackRate;
}
