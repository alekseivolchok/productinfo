package com.volchok.productinfo.csvprocessing;

import com.volchok.productinfo.model.StoredProductCsvRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductCsvSearcher extends AbstractCsvSearcher<StoredProductCsvRecord> {

    public static final String CSV_FILE_PATH = "/static/product.csv";

    public ProductCsvSearcher() {
        super(CSV_FILE_PATH, StoredProductCsvRecord.class);
    }
}
