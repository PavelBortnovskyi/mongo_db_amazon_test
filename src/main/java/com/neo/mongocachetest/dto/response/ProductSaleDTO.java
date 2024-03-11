package com.neo.mongocachetest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neo.mongocachetest.enums.CurrencyCode;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSaleDTO {

//    @DecimalMin(value = "0.00", inclusive = true)
//    @Digits(integer=7, fraction=2)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#0.00")
    public Float amount;

    public CurrencyCode currencyCode;
}
