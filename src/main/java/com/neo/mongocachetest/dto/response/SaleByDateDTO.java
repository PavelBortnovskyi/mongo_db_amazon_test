package com.neo.mongocachetest.dto.response;

import lombok.Data;


@Data
public class SaleByDateDTO {

    public ProductSaleDTO orderedProductSales;

    public ProductSaleDTO orderedProductSalesB2B;

    public Long unitsOrdered;

    public Long unitsOrderedB2B;

    public Long totalOrderItems;

    public Long totalOrderItemsB2B;

    public ProductSaleDTO averageSalesPerOrderItem;

    public ProductSaleDTO averageSalesPerOrderItemB2B;

    public Long averageUnitsPerOrderItem;

    public Long averageUnitsPerOrderItemB2B;

    public ProductSaleDTO averageSellingPrice;

    public ProductSaleDTO averageSellingPriceB2B;

    public Long unitsRefunded;

    public Double refundRate;

    public Integer claimsGranted;

    public ProductSaleDTO claimsAmount;

    public ProductSaleDTO shippedProductSales;

    public Long unitsShipped;

    public Long ordersShipped;
}
