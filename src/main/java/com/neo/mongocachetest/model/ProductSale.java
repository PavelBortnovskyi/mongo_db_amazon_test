package com.neo.mongocachetest.model;

import com.neo.mongocachetest.enums.CurrencyCode;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class ProductSale extends BaseDocument{

    public Float amount;

    public CurrencyCode currencyCode;
}
