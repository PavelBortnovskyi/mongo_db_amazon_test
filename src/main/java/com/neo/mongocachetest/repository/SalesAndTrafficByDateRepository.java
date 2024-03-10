package com.neo.mongocachetest.repository;

import com.neo.mongocachetest.model.SalesAndTrafficByDate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Repository
public interface SalesAndTrafficByDateRepository extends MongoRepository<SalesAndTrafficByDate, String> {

    Optional<SalesAndTrafficByDate> findByDate(Date date);

    List<SalesAndTrafficByDate> findByDateBetween(Date startDate, Date endDate);

    };

//    @Query(value = "{$group: { _id: null, "
//            + "salesByDate: {"
//            + "orderedProductSales: { $push: \"$salesByDate.orderedProductSales.amount\" }, "
//            + "orderedProductSalesB2B: { $push: \"$salesByDate.orderedProductSalesB2B.amount\" }, "
//            + "unitsOrdered: { $sum: \"$salesByDate.unitsOrdered\" }, "
//            + "unitsOrderedB2B: { $sum: \"$salesByDate.unitsOrderedB2B\" }, "
//            + "orderItems: { $sum: \"$salesByDate.orderItems\" }, "
//            + "orderItemsB2B: { $sum: \"$salesByDate.unitsOrderedB2B\" }, "
//            + "averageSalesPerOrderItem: { $push: \"$salesByDate.averageSalesPerOrderItem.amount\" }, "
//            + "averageSalesPerOrderItemB2B: { $push: \"$salesByDate.averageSalesPerOrderItemB2B.amount\" }, "
//            + "averageUnitsPerOrderItem: { $sum: \"$salesByDate.averageUnitsPerOrderItem\" }, "
//            + "averageUnitsPerOrderItemB2B: { $sum: \"$salesByDate.averageUnitsPerOrderItemB2B\" }, "
//            + "averageSellingPrice: { $push: \"$salesByDate.averageSellingPrice.amount\" }, "
//            + "averageSellingPriceB2B: { $push: \"$salesByDate.averageSellingPriceB2B.amount\" }, "
//            + "unitsRefunded: { $sum: \"$salesByDate.unitsRefunded\" }, "
//            + "refundRate: { $sum: \"$salesByDate.refundRate\" }, "
//            + "claimsGranted: { $sum: \"$salesByDate.claimsGranted\" }, "
//            + "claimsAmount: { $push: \"$salesByDate.claimsAmount.amount\" }, "
//            + "shippedProductSales: { $push: \"$salesByDate.shippedProductSales.amount\" }, "
//            + "unitsShipped: { $sum: \"$salesByDate.unitsShipped\" }, "
//            + "ordersShipped: { $sum: \"$salesByDate.ordersShipped\" }, "
//            + "}, "
//            + "trafficByDate: {"
//            + "browserPageViews: { $sum: \"$trafficByDate.browserPageViews\" }, "
//            + "browserPageViewsB2B: { $sum: \"$trafficByDate.browserPageViewsB2B\" }, "
//            + "mobileAppPageViews: { $sum: \"$trafficByDate.mobileAppPageViews\" }, "
//            + "mobileAppPageViewsB2B: { $sum: \"$trafficByDate.mobileAppPageViewsB2B\" }, "
//            + "pageViews: { $sum: \"$trafficByDate.pageViews\" }, "
//            + "pageViewsB2B: { $sum: \"$trafficByDate.pageViewsB2B\" }, "
//            + "browserSessions: { $sum: \"$trafficByDate.browserSessions\" }, "
//            + "browserSessionsB2B: { $sum: \"$trafficByDate.browserSessionsB2B\" }, "
//            + "mobileAppSessions: { $sum: \"$trafficByDate.mobileAppSessions\" }, "
//            + "mobileAppSessionsB2B: { $sum: \"$trafficByDate.mobileAppSessionsB2B\" }, "
//            + "sessions: { $sum: \"$trafficByDate.sessions\" }, "
//            + "sessionsB2B: { $sum: \"$trafficByDate.sessionsB2B\" }, "
//            + "buyBoxPercentage: { $sum: \"$trafficByDate.buyBoxPercentage\" }, "
//            + "buyBoxPercentageB2B: { $sum: \"$trafficByDate.buyBoxPercentageB2B\" }, "
//            + "orderItemSessionPercentage: { $sum: \"$trafficByDate.orderItemSessionPercentage\" }, "
//            + "orderItemSessionPercentageB2B: { $sum: \"$trafficByDate.orderItemSessionPercentageB2B\" }, "
//            + "unitSessionPercentage: { $sum: \"$trafficByDate.unitSessionPercentage\" }, "
//            + "unitSessionPercentageB2B: { $sum: \"$trafficByDate.unitSessionPercentageB2B\" }, "
//            + "averageOfferCount: { $sum: \"$trafficByDate.averageOfferCount\" }, "
//            + "averageOfferCountB2B: { $sum: \"$trafficByDate.averageOfferCountB2B\" }, "
//            + "feedbackReceived: { $sum: \"$trafficByDate.feedbackReceived\" }, "
//            + "negativeFeedbackReceived: { $sum: \"$trafficByDate.negativeFeedbackReceived\" }, "
//            + "receivedNegativeFeedbackRate: { $sum: \"$trafficByDate.receivedNegativeFeedbackRate\" }, "
//            + "}"
//            + "}}"
//    )


