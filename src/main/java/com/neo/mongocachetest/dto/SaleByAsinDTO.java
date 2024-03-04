package com.neo.mongocachetest.dto;

import lombok.Data;

@Data
public class SaleByAsinDTO {

  public Long unitsOrdered;

  public Long unitsOrderedB2B;

  public ProductSaleDTO orderedProductSales;

  public ProductSaleDTO orderedProductSalesB2B;

  public Long totalOrderItems;

  public Long totalOrderItemsB2B;
}
