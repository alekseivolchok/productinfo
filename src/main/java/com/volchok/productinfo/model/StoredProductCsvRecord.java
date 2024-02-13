package com.volchok.productinfo.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class StoredProductCsvRecord implements StoredCsvRecord {
    @CsvBindByName(column = "product_id")
    String productId;
    @CsvBindByName(column = "product_name")
    String productName;

    @Override
    public String getKey() {
        return productId;
    }
}
