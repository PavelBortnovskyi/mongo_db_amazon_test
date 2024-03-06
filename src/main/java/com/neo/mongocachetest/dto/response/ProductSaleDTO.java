package com.neo.mongocachetest.dto.response;

import com.neo.mongocachetest.enums.CurrencyCode;
import lombok.Data;

@Data
public class ProductSaleDTO {

    public Float amount;

    public CurrencyCode currencyCode;
}
