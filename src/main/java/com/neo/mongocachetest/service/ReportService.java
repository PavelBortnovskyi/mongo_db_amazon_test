package com.neo.mongocachetest.service;

import com.neo.mongocachetest.dto.response.*;
import com.neo.mongocachetest.enums.Granularity;
import com.neo.mongocachetest.enums.ReportType;
import com.neo.mongocachetest.model.SalesAndTrafficByAsin;
import com.neo.mongocachetest.model.SalesAndTrafficByDate;
import com.neo.mongocachetest.repository.SalesAndTrafficByAsinRepository;
import com.neo.mongocachetest.repository.SalesAndTrafficByDateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportService {

    private final SalesAndTrafficByDateRepository salesAndTrafficByDateRepository;
    private final SalesAndTrafficByAsinRepository salesAndTrafficByAsinRepository;
    private final ModelMapper mm;
    private final MongoTemplate mongoTemplate;

    public ReportDTO getTotalReport() {
        ReportDTO report = this.getReportWithHeader();
        ReportSpecificationDTO reportSpecification = report.getReportSpecification();
        report.setSalesAndTrafficByDate(salesAndTrafficByDateRepository.findAll()
                .stream()
                .map(s -> mm.map(s, SalesAndTrafficByDateDTO.class))
                .collect(Collectors.toList()));
        report.setSalesAndTrafficByAsin(salesAndTrafficByAsinRepository.findAll()
                .stream()
                .map(s -> mm.map(s, SalesAndTrafficByAsinDTO.class))
                .collect(Collectors.toList()));
        reportSpecification.setDataStartTime(report.getSalesAndTrafficByDate().get(0).getDate());
        reportSpecification.setDataEndTime(report.getSalesAndTrafficByDate().get(report.getSalesAndTrafficByDate().size() - 1).getDate());
        report.setReportSpecification(reportSpecification);
        return report;
    }

    public ReportDTO getSummaryReportsByDate() {
        ReportDTO report = this.getReportWithHeader();
        ReportSpecificationDTO reportSpecification = report.getReportSpecification();
        Pair<Date, Date> datePair = this.findEarliestAndLatestDate();
        reportSpecification.setDataStartTime(datePair.getFirst());
        reportSpecification.setDataEndTime(datePair.getSecond());
        SalesAndTrafficByDate summary = this.getSalesAndTrafficByDateSummary();
        report.setSalesAndTrafficByDate(List.of(mm.map(summary, SalesAndTrafficByDateDTO.class)));
        report.setReportSpecification(reportSpecification);
        return report;
    }

    public ReportDTO getSummaryReportsByAsin() {
        ReportDTO report = this.getReportWithHeader();
        ReportSpecificationDTO reportSpecification = report.getReportSpecification();
        SalesAndTrafficByAsin summary = this.getSalesAndTrafficByAsinSummary();
        report.setSalesAndTrafficByAsin(List.of(mm.map(summary, SalesAndTrafficByAsinDTO.class)));
        report.setReportSpecification(reportSpecification);
        return report;
    }

    public ReportDTO getReportByDate(Date date) {
        ReportDTO report = this.getReportWithHeader();
        report.getReportSpecification().setDataStartTime(date);
        report.getReportSpecification().setDataEndTime(date);
        report.setSalesAndTrafficByDate(new ArrayList<>() {{
            add(salesAndTrafficByDateRepository.findByDate(date)
                    .map(d -> mm.map(d, SalesAndTrafficByDateDTO.class))
                    .orElse(null));
        }});
        return report;
    }

    public ReportDTO getReportByDateRange(Date startDate, Date endDate) {
        ReportDTO report = this.getReportWithHeader();

        report.getReportSpecification().setDataStartTime(startDate);
        report.getReportSpecification().setDataEndTime(endDate);

        LocalDate startDateInclusive = Instant.ofEpochMilli(startDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .minusDays(1);
        LocalDate endDateInclusive = Instant.ofEpochMilli(endDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusDays(1);
        startDate = Date.from(startDateInclusive.atStartOfDay(ZoneId.systemDefault()).toInstant());
        endDate = Date.from(endDateInclusive.atStartOfDay(ZoneId.systemDefault()).toInstant());

        report.setSalesAndTrafficByDate(salesAndTrafficByDateRepository.findByDateBetween(startDate, endDate)
                .stream()
                .map((d) -> mm.map(d, SalesAndTrafficByDateDTO.class))
                .collect(Collectors.toList()));
        return report;
    }

    public ReportDTO getAllReportByAsin() {
        ReportDTO report = this.getReportWithHeader();
        ReportSpecificationDTO reportSpecification = report.getReportSpecification();
        report.setSalesAndTrafficByAsin(salesAndTrafficByAsinRepository.findAll()
                .stream()
                .map(s -> mm.map(s, SalesAndTrafficByAsinDTO.class))
                .collect(Collectors.toList()));
        report.setReportSpecification(reportSpecification);
        return report;
    }

    public ReportDTO getReportByAsin(String ASIN) {
        ReportDTO report = this.getReportWithHeader();
        report.setSalesAndTrafficByAsin(new ArrayList<>() {{
            add(salesAndTrafficByAsinRepository.findByParentAsin(ASIN)
                    .map(d -> mm.map(d, SalesAndTrafficByAsinDTO.class))
                    .orElse(null));
        }});
        return report;
    }

    public ReportDTO getReportByAsinList(List<String> ASINs) {
        ReportDTO report = this.getReportWithHeader();
        report.setSalesAndTrafficByAsin(salesAndTrafficByAsinRepository.findByParentAsinIn(ASINs)
                .stream().map(d -> mm.map(d, SalesAndTrafficByAsinDTO.class)).collect(Collectors.toList()));
        return report;
    }

    public ReportDTO getReportWithHeader() {
        ReportDTO report = new ReportDTO();
        ReportSpecificationDTO reportSpecification = new ReportSpecificationDTO();
        ReportOptionsDTO reportOptions = new ReportOptionsDTO();
        reportOptions.setAsinGranularity("PARENT");
        reportOptions.setDateGranularity(Granularity.DAY);
        reportSpecification.setReportOptions(reportOptions);
        reportSpecification.setReportType(ReportType.GET_SALES_AND_TRAFFIC_REPORT);
        reportSpecification.setMarketplaceIds(List.of("ATVPDKIKX0DER"));
        report.setReportSpecification(reportSpecification);
        return report;
    }

    public SalesAndTrafficByDate getSalesAndTrafficByDateSummary() {
        AggregationOperation lookupSalesByDate = Aggregation.lookup("saleByDate", "salesByDate.$id", "_id", "lookupSalesByDate");
        AggregationOperation unwindSalesByDate = Aggregation.unwind("$lookupSalesByDate");

        AggregationOperation lookupOrderedProductSales = Aggregation.lookup("productSale", "lookupSalesByDate.orderedProductSales.$id", "_id", "lookupOrderedProductSales");
        AggregationOperation unwindOrderedProductSales = Aggregation.unwind("$lookupOrderedProductSales");

        AggregationOperation lookupOrderedProductSalesB2B = Aggregation.lookup("productSale", "lookupSalesByDate.orderedProductSalesB2B.$id", "_id", "lookupOrderedProductSalesB2B");
        AggregationOperation unwindOrderedProductSalesB2B = Aggregation.unwind("$lookupOrderedProductSalesB2B");

        AggregationOperation lookupAverageSalesPerOrderItem = Aggregation.lookup("productSale", "lookupSalesByDate.averageSalesPerOrderItem.$id", "_id", "lookupAverageSalesPerOrderItem");
        AggregationOperation unwindAverageSalesPerOrderItem = Aggregation.unwind("$lookupAverageSalesPerOrderItem");

        AggregationOperation lookupAverageSalesPerOrderItemB2B = Aggregation.lookup("productSale", "lookupSalesByDate.averageSalesPerOrderItemB2B.$id", "_id", "lookupAverageSalesPerOrderItemB2B");
        AggregationOperation unwindAverageSalesPerOrderItemB2B = Aggregation.unwind("$lookupAverageSalesPerOrderItemB2B");

        AggregationOperation lookupAverageSellingPrice = Aggregation.lookup("productSale", "lookupSalesByDate.averageSellingPrice.$id", "_id", "lookupAverageSellingPrice");
        AggregationOperation unwindAverageSellingPrice = Aggregation.unwind("$lookupAverageSellingPrice");

        AggregationOperation lookupAverageSellingPriceB2B = Aggregation.lookup("productSale", "lookupSalesByDate.averageSellingPriceB2B.$id", "_id", "lookupAverageSellingPriceB2B");
        AggregationOperation unwindAverageSellingPriceB2B = Aggregation.unwind("$lookupAverageSellingPriceB2B");

        AggregationOperation lookupClaimsAmount = Aggregation.lookup("productSale", "lookupSalesByDate.claimsAmount.$id", "_id", "lookupClaimsAmount");
        AggregationOperation unwindClaimsAmount = Aggregation.unwind("$lookupClaimsAmount");

        AggregationOperation lookupShippedProductSales = Aggregation.lookup("productSale", "lookupSalesByDate.shippedProductSales.$id", "_id", "lookupShippedProductSales");
        AggregationOperation unwindShippedProductSales = Aggregation.unwind("$lookupShippedProductSales");

        AggregationOperation lookupTrafficByDate = Aggregation.lookup("trafficByDate", "trafficByDate.$id", "_id", "lookupTrafficByDate");
        AggregationOperation unwindTrafficByDate = Aggregation.unwind("$lookupTrafficByDate");

        AggregationOperation group = Aggregation
                .group()
                .sum("$lookupOrderedProductSales.amount").as("totalOrderedProductSales")
                .sum("$lookupOrderedProductSalesB2B.amount").as("totalOrderedProductSalesB2B")
                .sum("$lookupSalesByDate.unitsOrdered").as("totalUnitsOrdered")
                .sum("$lookupSalesByDate.unitsOrderedB2B").as("totalUnitsOrderedB2B")
                .sum("$lookupSalesByDate.totalOrderItems").as("totalOrderItems")
                .sum("$lookupSalesByDate.totalOrderItemsB2B").as("totalOrderItemsB2B")
                .avg("$lookupAverageSalesPerOrderItem.amount").as("totalAverageSalesPerOrderItem")
                .avg("$lookupAverageSalesPerOrderItemB2B.amount").as("totalAverageSalesPerOrderItemB2B")
                .avg("$lookupSalesByDate.averageUnitsPerOrderItem").as("totalAverageUnitsPerOrderItem")
                .avg("$lookupSalesByDate.averageUnitsPerOrderItemB2B").as("totalAverageUnitsPerOrderItemB2B")
                .avg("$lookupAverageSellingPrice.amount").as("totalAverageSellingPrice")
                .avg("$lookupAverageSellingPriceB2B.amount").as("totalAverageSellingPriceB2B")
                .sum("$lookupSalesByDate.unitsRefunded").as("totalUnitsRefunded")
                .avg("$lookupSalesByDate.refundRate").as("totalRefundRate")                         //recalculate
                .sum("$lookupSalesByDate.claimsGranted").as("totalClaimsGranted")
                .sum("$lookupClaimsAmount.amount").as("totalClaimsAmount")
                .sum("$lookupShippedProductSales.amount").as("totalShippedProductSales")
                .sum("$lookupSalesByDate.unitsShipped").as("totalUnitsShipped")
                .sum("$lookupSalesByDate.ordersShipped").as("totalOrdersShipped")
                .sum("$lookupTrafficByDate.browserPageViews").as("totalBrowserPageViews")
                .sum("$lookupTrafficByDate.browserPageViewsB2B").as("totalBrowserPageViewsB2B")
                .sum("$lookupTrafficByDate.mobileAppPageViews").as("totalMobileAppPageViews")
                .sum("$lookupTrafficByDate.mobileAppPageViewsB2B").as("totalMobileAppPageViewsB2B")
                .sum("$lookupTrafficByDate.pageViews").as("totalPageViews")
                .sum("$lookupTrafficByDate.pageViewsB2B").as("totalPageViewsB2B")
                .sum("$lookupTrafficByDate.browserSessions").as("totalBrowserSessions")
                .sum("$lookupTrafficByDate.browserSessionsB2B").as("totalBrowserSessionsB2B")
                .sum("$lookupTrafficByDate.mobileAppSessions").as("totalMobileAppSessions")
                .sum("$lookupTrafficByDate.mobileAppSessionsB2B").as("totalMobileAppSessionsB2B")
                .sum("$lookupTrafficByDate.sessions").as("totalSessions")
                .sum("$lookupTrafficByDate.sessionsB2B").as("totalSessionsB2B")
                .avg("$lookupTrafficByDate.buyBoxPercentage").as("totalBuyBoxPercentage")            //recalculate
                .avg("$lookupTrafficByDate.buyBoxPercentageB2B").as("totalBuyBoxPercentageB2B")      //recalculate
                .sum("$lookupTrafficByDate.orderItemSessionPercentage").as("totalOrderItemSessionPercentage")
                .sum("$lookupTrafficByDate.orderItemSessionPercentageB2B").as("totalOrderItemSessionPercentageB2B")
                .sum("$lookupTrafficByDate.unitSessionPercentage").as("totalUnitSessionPercentage")
                .sum("$lookupTrafficByDate.unitSessionPercentageB2B").as("totalUnitSessionPercentageB2B")
                .avg("$lookupTrafficByDate.averageOfferCount").as("totalAverageOfferCount")
                .avg("$lookupTrafficByDate.averageParentItems").as("totalAverageParentItems")
                .sum("$lookupTrafficByDate.feedbackReceived").as("totalFeedbackReceived")
                .sum("$lookupTrafficByDate.negativeFeedbackReceived").as("totalNegativeFeedbackReceived")
                .sum("$lookupTrafficByDate.receivedNegativeFeedbackRate").as("totalReceivedNegativeFeedbackRate")
                .addToSet("$lookupOrderedProductSales.currencyCode").as("OrderedProductSalesCurrencyCode")
                .addToSet("$lookupOrderedProductSalesB2B.currencyCode").as("OrderedProductSalesB2BCurrencyCode")
                .addToSet("$lookupAverageSalesPerOrderItem.currencyCode").as("AverageSalesPerOrderItemCurrencyCode")
                .addToSet("$lookupAverageSalesPerOrderItemB2B.currencyCode").as("AverageSalesPerOrderItemB2BCurrencyCode")
                .addToSet("$lookupAverageSellingPrice.currencyCode").as("AverageSellingPriceCurrencyCode")
                .addToSet("$lookupAverageSellingPriceB2B.currencyCode").as("AverageSellingPriceB2BCurrencyCode")
                .addToSet("$lookupClaimsAmount.currencyCode").as("ClaimsAmountCurrencyCode")
                .addToSet("$lookupShippedProductSales.currencyCode").as("ShippedProductSalesCurrencyCode");

        AggregationOperation projection = Aggregation.project()
                .and(
                        new AggregationExpression() {
                            @Override
                            public Document toDocument(AggregationOperationContext context) {
                                Document salesByDate = new Document();
                                salesByDate.put("unitsOrdered", "$totalUnitsOrdered");
                                salesByDate.put("unitsOrderedB2B", "$totalUnitsOrderedB2B");
                                salesByDate.put("totalOrderItems", "$totalOrderItems");
                                salesByDate.put("totalOrderItemsB2B", "$totalOrderItemsB2B");
                                salesByDate.put("averageUnitsPerOrderItem", "$totalAverageUnitsPerOrderItem");
                                salesByDate.put("averageUnitsPerOrderItemB2B", "$totalAverageUnitsPerOrderItemB2B");
                                salesByDate.put("unitsRefunded", "$totalUnitsRefunded");
                                salesByDate.put("refundRate", "$totalRefundRate");
                                salesByDate.put("claimsGranted", "$totalClaimsGranted");
                                salesByDate.put("unitsShipped", "$totalUnitsShipped");
                                salesByDate.put("ordersShipped", "$totalOrdersShipped");

                                Document orderedProductSale = new Document();
                                orderedProductSale.put("amount", "$totalOrderedProductSales");
                                orderedProductSale.put("currencyCode", new Document("$arrayElemAt", Arrays.asList("$OrderedProductSalesCurrencyCode", 0)));
                                salesByDate.put("orderedProductSales", orderedProductSale);

                                Document orderedProductSaleB2B = new Document();
                                orderedProductSaleB2B.put("amount", "$totalOrderedProductSalesB2B");
                                orderedProductSaleB2B.put("currencyCode", new Document("$arrayElemAt", Arrays.asList("$OrderedProductSalesB2BCurrencyCode", 0)));
                                salesByDate.put("orderedProductSalesB2B", orderedProductSaleB2B);

                                Document averageSalesPerOrderItem = new Document();
                                averageSalesPerOrderItem.put("amount", "$totalAverageSalesPerOrderItem");
                                averageSalesPerOrderItem.put("currencyCode", new Document("$arrayElemAt", Arrays.asList("$AverageSalesPerOrderItemCurrencyCode", 0)));
                                salesByDate.put("averageSalesPerOrderItem", averageSalesPerOrderItem);

                                Document averageSalesPerOrderItemB2B = new Document();
                                averageSalesPerOrderItemB2B.put("amount", "$totalAverageSalesPerOrderItemB2B");
                                averageSalesPerOrderItemB2B.put("currencyCode", new Document("$arrayElemAt", Arrays.asList("$AverageSalesPerOrderItemB2BCurrencyCode", 0)));
                                salesByDate.put("averageSalesPerOrderItemB2B", averageSalesPerOrderItemB2B);

                                Document averageSellingPrice = new Document();
                                averageSellingPrice.put("amount", "$totalAverageSellingPrice");
                                averageSellingPrice.put("currencyCode", new Document("$arrayElemAt", Arrays.asList("$AverageSellingPriceCurrencyCode", 0)));
                                salesByDate.put("averageSellingPrice", averageSellingPrice);

                                Document averageSellingPriceB2B = new Document();
                                averageSellingPriceB2B.put("amount", "$totalAverageSellingPriceB2B");
                                averageSellingPriceB2B.put("currencyCode", new Document("$arrayElemAt", Arrays.asList("$AverageSellingPriceB2BCurrencyCode", 0)));
                                salesByDate.put("averageSellingPriceB2B", averageSellingPriceB2B);

                                Document claimsAmount = new Document();
                                claimsAmount.put("amount", "$totalClaimsAmount");
                                claimsAmount.put("currencyCode", new Document("$arrayElemAt", Arrays.asList("$ClaimsAmountCurrencyCode", 0)));
                                salesByDate.put("claimsAmount", claimsAmount);

                                Document shippedProductSales = new Document();
                                shippedProductSales.put("amount", "$totalShippedProductSales");
                                shippedProductSales.put("currencyCode", new Document("$arrayElemAt", Arrays.asList("$ShippedProductSalesCurrencyCode", 0)));
                                salesByDate.put("shippedProductSales", shippedProductSales);
                                return salesByDate;
                            }
                        }
                ).as("salesByDate")
                .and("trafficByDate")
                .nested(bind("browserPageViews", "$totalBrowserPageViews")
                        .and("browserPageViewsB2B", "$totalBrowserPageViewsB2B")
                        .and("mobileAppPageViews", "$totalMobileAppPageViews")
                        .and("mobileAppPageViewsB2B", "$totalMobileAppPageViewsB2B")
                        .and("pageViews", "$totalPageViews")
                        .and("pageViewsB2B", "$totalPageViewsB2B")
                        .and("browserSessions", "$totalBrowserSessions")
                        .and("browserSessionsB2B", "$totalBrowserSessionsB2B")
                        .and("mobileAppSessions", "$totalMobileAppSessions")
                        .and("mobileAppSessionsB2B", "$totalMobileAppSessionsB2B")
                        .and("sessions", "$totalSessions")
                        .and("sessionsB2B", "$totalSessionsB2B")
                        .and("buyBoxPercentage", "$totalBuyBoxPercentage")
                        .and("buyBoxPercentageB2B", "$totalBuyBoxPercentageB2B")
                        .and("orderItemSessionPercentage", "$totalOrderItemSessionPercentage")
                        .and("orderItemSessionPercentageB2B", "$totalOrderItemSessionPercentageB2B")
                        .and("unitSessionPercentage", "$totalUnitSessionPercentage")
                        .and("unitSessionPercentageB2B", "$totalUnitSessionPercentageB2B")
                        .and("averageOfferCount", "$totalAverageOfferCount")
                        .and("averageParentItems", "$totalAverageParentItems")
                        .and("feedbackReceived", "$totalFeedbackReceived")
                        .and("negativeFeedbackReceived", "$totalNegativeFeedbackReceived")
                        .and("receivedNegativeFeedbackRate", "$totalFeedbackReceived / $totalNegativeFeedbackReceived"));

        TypedAggregation<SalesAndTrafficByDate> aggregation = Aggregation.newAggregation(
                SalesAndTrafficByDate.class,
                lookupSalesByDate,
                unwindSalesByDate,
                lookupOrderedProductSales,
                unwindOrderedProductSales,
                lookupOrderedProductSalesB2B,
                unwindOrderedProductSalesB2B,
                lookupAverageSalesPerOrderItem,
                unwindAverageSalesPerOrderItem,
                lookupAverageSalesPerOrderItemB2B,
                unwindAverageSalesPerOrderItemB2B,
                lookupAverageSellingPrice,
                unwindAverageSellingPrice,
                lookupAverageSellingPriceB2B,
                unwindAverageSellingPriceB2B,
                lookupClaimsAmount,
                unwindClaimsAmount,
                lookupShippedProductSales,
                unwindShippedProductSales,
                lookupTrafficByDate,
                unwindTrafficByDate,
                group,
                projection
        );

        AggregationResults<SalesAndTrafficByDate> results = mongoTemplate.aggregate(aggregation,
                "salesAndTrafficByDate", SalesAndTrafficByDate.class);

        return results.getUniqueMappedResult();
    }

    private Pair<Date, Date> findEarliestAndLatestDate() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group().min("date").as("startDate").max("date").as("endDate")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "salesAndTrafficByDate", Document.class);
        Document aggregationResults = results.getUniqueMappedResult();

        Date earliestDate = aggregationResults.getDate("startDate");
        Date latestDate = aggregationResults.getDate("endDate");

        return Pair.of(earliestDate, latestDate);
    }

    public SalesAndTrafficByAsin getSalesAndTrafficByAsinSummary() {
        AggregationOperation lookupSalesByAsin = Aggregation.lookup("saleByAsin", "salesByAsin.$id", "_id", "lookupSalesByAsin");
        AggregationOperation unwindSalesByAsin = Aggregation.unwind("$lookupSalesByAsin");

        AggregationOperation lookupOrderedProductSales = Aggregation.lookup("productSale", "lookupSalesByAsin.orderedProductSales.$id", "_id", "lookupOrderedProductSales");
        AggregationOperation unwindOrderedProductSales = Aggregation.unwind("$lookupOrderedProductSales");

        AggregationOperation lookupOrderedProductSalesB2B = Aggregation.lookup("productSale", "lookupSalesByAsin.orderedProductSalesB2B.$id", "_id", "lookupOrderedProductSalesB2B");
        AggregationOperation unwindOrderedProductSalesB2B = Aggregation.unwind("$lookupOrderedProductSalesB2B");

        AggregationOperation lookupTrafficByAsin = Aggregation.lookup("trafficByAsin", "trafficByAsin.$id", "_id", "lookupTrafficByAsin");
        AggregationOperation unwindTrafficByAsin = Aggregation.unwind("$lookupTrafficByAsin");

        AggregationOperation group = Aggregation
                .group()
                .sum("$lookupSalesByAsin.unitsOrdered").as("totalUnitsOrdered")
                .sum("$lookupSalesByAsin.unitsOrderedB2B").as("totalUnitsOrderedB2B")
                .sum("$lookupOrderedProductSales.amount").as("totalOrderedProductSales")
                .sum("$lookupOrderedProductSalesB2B.amount").as("totalOrderedProductSalesB2B")
                .sum("$lookupSalesByAsin.totalOrderItems").as("totalOrderItems")
                .sum("$lookupSalesByAsin.totalOrderItemsB2B").as("totalOrderItemsB2B")
                .sum("$lookupTrafficByAsin.browserSessions").as("totalBrowserSessions")
                .sum("$lookupTrafficByAsin.browserSessionsB2B").as("totalBrowserSessionsB2B")
                .sum("$lookupTrafficByAsin.mobileAppSessions").as("totalMobileAppSessions")
                .sum("$lookupTrafficByAsin.mobileAppSessionsB2B").as("totalMobileAppSessionsB2B")
                .sum("$lookupTrafficByAsin.sessions").as("totalSessions")
                .sum("$lookupTrafficByAsin.sessionsB2B").as("totalSessionsB2B")
                .avg("$lookupTrafficByAsin.browserSessionPercentage").as("totalBrowserSessionPercentage")
                .avg("$lookupTrafficByAsin.browserSessionPercentageB2B").as("totalBrowserSessionPercentageB2B")
                .avg("$lookupTrafficByAsin.mobileAppSessionPercentage").as("totalMobileAppSessionPercentage")
                .avg("$lookupTrafficByAsin.mobileAppSessionPercentageB2B").as("totalMobileAppSessionPercentageB2B")
                .avg("$lookupTrafficByAsin.sessionPercentage").as("totalSessionPercentage")
                .avg("$lookupTrafficByAsin.sessionPercentageB2B").as("totalSessionPercentageB2B")
                .sum("$lookupTrafficByAsin.browserPageViews").as("totalBrowserPageViews")
                .sum("$lookupTrafficByAsin.browserPageViewsB2B").as("totalBrowserPageViewsB2B")
                .sum("$lookupTrafficByAsin.mobileAppPageViews").as("totalMobileAppPageViews")
                .sum("$lookupTrafficByAsin.mobileAppPageViewsB2B").as("totalMobileAppPageViewsB2B")
                .sum("$lookupTrafficByAsin.pageViews").as("totalPageViews")
                .sum("$lookupTrafficByAsin.pageViewsB2B").as("totalPageViewsB2B")
                .avg("$lookupTrafficByAsin.browserPageViewsPercentage").as("totalBrowserPageViewsPercentage")
                .avg("$lookupTrafficByAsin.browserPageViewsPercentageB2B").as("totalBrowserPageViewsPercentageB2B")
                .avg("$lookupTrafficByAsin.mobileAppPageViewsPercentage").as("totalMobileAppPageViewsPercentage")
                .avg("$lookupTrafficByAsin.mobileAppPageViewsPercentageB2B").as("totalMobileAppPageViewsPercentageB2B")
                .avg("$lookupTrafficByAsin.pageViewsPercentage").as("totalPageViewsPercentage")
                .avg("$lookupTrafficByAsin.pageViewsPercentageB2B").as("totalPageViewsPercentageB2B")
                .avg("$lookupTrafficByAsin.buyBoxPercentage").as("totalBuyBoxPercentage")            //recalculate
                .avg("$lookupTrafficByAsin.buyBoxPercentageB2B").as("totalBuyBoxPercentageB2B")      //recalculate
                .avg("$lookupTrafficByAsin.unitSessionPercentage").as("totalUnitSessionPercentage")
                .avg("$lookupTrafficByAsin.unitSessionPercentageB2B").as("totalUnitSessionPercentageB2B")
                .addToSet("$lookupOrderedProductSales.currencyCode").as("OrderedProductSalesCurrencyCode")
                .addToSet("$lookupOrderedProductSalesB2B.currencyCode").as("OrderedProductSalesB2BCurrencyCode");

        AggregationOperation projection = Aggregation.project()
                .and(
                        new AggregationExpression() {
                            @Override
                            public Document toDocument(AggregationOperationContext context) {
                                Document salesByDate = new Document();
                                salesByDate.put("unitsOrdered", "$totalUnitsOrdered");
                                salesByDate.put("unitsOrderedB2B", "$totalUnitsOrderedB2B");
                                salesByDate.put("totalOrderItems", "$totalOrderItems");
                                salesByDate.put("totalOrderItemsB2B", "$totalOrderItemsB2B");

                                Document orderedProductSale = new Document();
                                orderedProductSale.put("amount", "$totalOrderedProductSales");
                                orderedProductSale.put("currencyCode", new Document("$arrayElemAt", Arrays.asList("$OrderedProductSalesCurrencyCode", 0)));
                                salesByDate.put("orderedProductSales", orderedProductSale);

                                Document orderedProductSaleB2B = new Document();
                                orderedProductSaleB2B.put("amount", "$totalOrderedProductSalesB2B");
                                orderedProductSaleB2B.put("currencyCode", new Document("$arrayElemAt", Arrays.asList("$OrderedProductSalesB2BCurrencyCode", 0)));
                                salesByDate.put("orderedProductSalesB2B", orderedProductSaleB2B);

                                return salesByDate;
                            }
                        }
                ).as("salesByAsin")
                .and("trafficByAsin")
                .nested(bind("browserSessions", "$totalBrowserSessions")
                        .and("browserSessionsB2B", "$totalBrowserSessionsB2B")
                        .and("mobileAppSessions", "$totalMobileAppSessions")
                        .and("mobileAppSessionsB2B", "$totalMobileAppSessionsB2B")
                        .and("sessions", "$totalSessions")
                        .and("sessionsB2B", "$totalSessionsB2B")
                        .and("browserSessionPercentage", "$totalBrowserSessionPercentage")
                        .and("browserSessionPercentageB2B", "$totalBrowserSessionPercentageB2B")
                        .and("mobileAppSessionPercentage", "$totalMobileAppSessionPercentage")
                        .and("mobileAppSessionPercentageB2B", "$totalMobileAppSessionPercentageB2B")
                        .and("sessionPercentage", "$totalSessionPercentage")
                        .and("sessionPercentageB2B", "$totalSessionPercentageB2B")
                        .and("browserPageViews", "$totalBrowserPageViews")
                        .and("browserPageViewsB2B", "$totalBrowserPageViewsB2B")
                        .and("mobileAppPageViews", "$totalMobileAppPageViews")
                        .and("mobileAppPageViewsB2B", "$totalMobileAppPageViewsB2B")
                        .and("pageViews", "$totalPageViews")
                        .and("pageViewsB2B", "$totalPageViewsB2B")
                        .and("browserPageViewsPercentage", "$totalBrowserPageViewsPercentage")
                        .and("browserPageViewsPercentageB2B", "$totalBrowserPageViewsPercentageB2B")
                        .and("mobileAppPageViewsPercentage", "$totalMobileAppPageViewsPercentage")
                        .and("mobileAppPageViewsPercentageB2B", "$totalMobileAppPageViewsPercentageB2B")
                        .and("pageViewsPercentage", "$totalPageViewsPercentage")
                        .and("pageViewsPercentageB2B", "$totalPageViewsPercentageB2B")
                        .and("buyBoxPercentage", "$totalBuyBoxPercentage")
                        .and("buyBoxPercentageB2B", "$totalBuyBoxPercentageB2B")
                        .and("unitSessionPercentage", "$totalUnitSessionPercentage")
                        .and("unitSessionPercentageB2B", "$totalUnitSessionPercentageB2B"));

        TypedAggregation<SalesAndTrafficByAsin> aggregation = Aggregation.newAggregation(
                SalesAndTrafficByAsin.class,
                lookupSalesByAsin,
                unwindSalesByAsin,
                lookupOrderedProductSales,
                unwindOrderedProductSales,
                lookupOrderedProductSalesB2B,
                unwindOrderedProductSalesB2B,
                lookupTrafficByAsin,
                unwindTrafficByAsin,
                group,
                projection
        );

        AggregationResults<SalesAndTrafficByAsin> results = mongoTemplate.aggregate(aggregation,
                "salesAndTrafficByAsin", SalesAndTrafficByAsin.class);

        return results.getUniqueMappedResult();
    }
}
