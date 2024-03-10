package com.neo.mongocachetest.service;

import com.mongodb.BasicDBObjectBuilder;
import com.neo.mongocachetest.dto.response.*;
import com.neo.mongocachetest.enums.CurrencyCode;
import com.neo.mongocachetest.enums.Granularity;
import com.neo.mongocachetest.enums.ReportType;
import com.neo.mongocachetest.model.SalesAndTrafficByDate;
import com.neo.mongocachetest.repository.SalesAndTrafficByAsinRepository;
import com.neo.mongocachetest.repository.SalesAndTrafficByDateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
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
        SalesAndTrafficByDate summary = this.getSalesAndTrafficSummary();
        report.setSalesAndTrafficByDate(List.of(mm.map(summary, SalesAndTrafficByDateDTO.class)));
//        reportSpecification.setDataStartTime(report.getSalesAndTrafficByDate().get(0).getDate());
//        reportSpecification.setDataEndTime(report.getSalesAndTrafficByDate().get(report.getSalesAndTrafficByDate().size() - 1).getDate());
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

    public SalesAndTrafficByDate getSalesAndTrafficSummary() {
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
                .sum("$lookupTrafficByDate.receivedNegativeFeedbackRate").as("totalReceivedNegativeFeedbackRate");

        AggregationOperation projection = Aggregation. project()
                //.and("date").as(null)
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
                                orderedProductSale.put("currencyCode", CurrencyCode.USD);
                                salesByDate.put("orderedProductSales", orderedProductSale);

                                Document orderedProductSaleB2B = new Document();
                                orderedProductSaleB2B.put("amount", "$totalOrderedProductSalesB2B");
                                orderedProductSaleB2B.put("currencyCode", CurrencyCode.USD);
                                salesByDate.put("orderedProductSalesB2B", orderedProductSaleB2B);

                                Document averageSalesPerOrderItem = new Document();
                                averageSalesPerOrderItem.put("amount", "$totalAverageSalesPerOrderItem");
                                averageSalesPerOrderItem.put("currencyCode", CurrencyCode.USD);
                                salesByDate.put("averageSalesPerOrderItem", averageSalesPerOrderItem);

                                Document averageSalesPerOrderItemB2B = new Document();
                                averageSalesPerOrderItemB2B.put("amount", "$totalAverageSalesPerOrderItemB2B");
                                averageSalesPerOrderItemB2B.put("currencyCode", CurrencyCode.USD);
                                salesByDate.put("averageSalesPerOrderItemB2B", averageSalesPerOrderItemB2B);

                                Document averageSellingPrice = new Document();
                                averageSellingPrice.put("amount", "$totalAverageSellingPrice");
                                averageSellingPrice.put("currencyCode", CurrencyCode.USD);
                                salesByDate.put("averageSellingPrice", averageSellingPrice);

                                Document averageSellingPriceB2B = new Document();
                                averageSellingPriceB2B.put("amount", "$totalAverageSellingPriceB2B");
                                averageSellingPriceB2B.put("currencyCode", CurrencyCode.USD);
                                salesByDate.put("totalAverageSellingPriceB2B", averageSellingPriceB2B);

                                Document claimsAmount = new Document();
                                claimsAmount.put("amount", "$totalClaimsAmount");
                                claimsAmount.put("currencyCode", CurrencyCode.USD);
                                salesByDate.put("claimsAmount", claimsAmount);

                                Document shippedProductSales = new Document();
                                shippedProductSales.put("amount", "$totalShippedProductSales");
                                shippedProductSales.put("currencyCode", CurrencyCode.USD);
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
                        .and("receivedNegativeFeedbackRate", "$totalReceivedNegativeFeedbackRate"));

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
}
