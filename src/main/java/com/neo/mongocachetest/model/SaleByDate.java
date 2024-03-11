package com.neo.mongocachetest.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class SaleByDate extends BaseDocument{

    @DBRef
    public ProductSale orderedProductSales;

    @DBRef
    public ProductSale orderedProductSalesB2B;

    public Long unitsOrdered;

    public Long unitsOrderedB2B;

    public Long totalOrderItems;

    public Long totalOrderItemsB2B;

    @DBRef
    public ProductSale averageSalesPerOrderItem;

    @DBRef
    public ProductSale averageSalesPerOrderItemB2B;

    public Long averageUnitsPerOrderItem;

    public Long averageUnitsPerOrderItemB2B;

    @DBRef
    public ProductSale averageSellingPrice;

    @DBRef
    public ProductSale averageSellingPriceB2B;

    public Long unitsRefunded;

    public Float refundRate;

    public Integer claimsGranted;

    @DBRef
    public ProductSale claimsAmount;

    @DBRef
    public ProductSale shippedProductSales;

    public Long unitsShipped;

    public Long ordersShipped;
}
