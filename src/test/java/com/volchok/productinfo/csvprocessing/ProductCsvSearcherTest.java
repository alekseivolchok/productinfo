package com.volchok.productinfo.csvprocessing;

import com.volchok.productinfo.model.StoredProductCsvRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductCsvSearcherTest {
    @Autowired
    ProductCsvSearcher productCsvSearcher;

    @Test
    void searchPositiveScenario() {
        StoredProductCsvRecord record = productCsvSearcher.findRecord("1");
        assertNotNull(record);
        assertEquals(record.getProductId(), "1");
        assertEquals(record.getProductName(), "Test1");
    }
    @Test
    void searchNegativeScenario() {
        StoredProductCsvRecord record = productCsvSearcher.findRecord("3");
        assertNull(record);
    }
}