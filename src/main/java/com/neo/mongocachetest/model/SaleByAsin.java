package com.neo.mongocachetest.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class SaleByAsin extends BaseDocument {

  public Long unitsOrdered;

  public Long unitsOrderedB2B;

  @DBRef
  public ProductSale orderedProductSales;

  @DBRef
  public ProductSale orderedProductSalesB2B;

  public Long totalOrderItems;

  public Long totalOrderItemsB2B;
}
