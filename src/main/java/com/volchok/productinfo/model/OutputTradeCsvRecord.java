package com.volchok.productinfo.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OutputTradeCsvRecord {
    @CsvBindByName(column = "product_name")
    String productName;
    @CsvBindByName(column = "date")
    String date;
    @CsvBindByName(column = "currency")
    String currency;
    @CsvBindByName(column = "price")
    String price;
}
