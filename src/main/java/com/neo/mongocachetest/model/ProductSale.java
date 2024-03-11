package com.neo.mongocachetest.model;

import com.neo.mongocachetest.enums.CurrencyCode;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document
public class ProductSale extends BaseDocument{

//    @DecimalMin(value = "0.00", inclusive = true)
//    @Digits(integer=7, fraction=2)
    public Float amount;

    public CurrencyCode currencyCode;
}
