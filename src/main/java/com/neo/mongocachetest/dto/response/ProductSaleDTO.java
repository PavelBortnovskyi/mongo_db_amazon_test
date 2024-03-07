package com.neo.mongocachetest.dto.response;

import com.neo.mongocachetest.enums.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSaleDTO {

    @DecimalMin(value = "0.00", inclusive = true)
    @Digits(integer=7, fraction=2)
    public BigDecimal amount;

    public CurrencyCode currencyCode;
}
