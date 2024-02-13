package com.volchok.productinfo.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Data;

@Data
public class InputTradeCsvRecord {
    @CsvBindByName(column = "product_id")
    String productId;
    @CsvBindByName(column = "date")
    String date;
    @CsvBindByName(column = "currency")
    String currency;
    @CsvBindByName(column = "price")
    String price;
}
