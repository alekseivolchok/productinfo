package com.volchok.productinfo.enricher;

import com.volchok.productinfo.model.InputTradeCsvRecord;
import com.volchok.productinfo.model.OutputTradeCsvRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TradeDataEnricherTest {

    @Autowired
    TradeDataEnricher tradeDataEnricher;

    @Test
    void enrichTradeDataSimplePositive() {
        List<InputTradeCsvRecord> input = List.of(createRecord("20150101","100","EUR","1"),
                createRecord("20160101","200","USD","2"));
        List<OutputTradeCsvRecord> output = tradeDataEnricher.enrichTradeData(input);
        assertEquals(output.size(), 2);
        assertEquals(output.get(0).getProductName(), "Test1");
        assertEquals(output.get(1).getProductName(), "Test2");
        assertEquals(output.get(0).getDate(), "20150101");
        assertEquals(output.get(1).getDate(), "20160101");
        assertEquals(output.get(0).getCurrency(), "EUR");
        assertEquals(output.get(1).getCurrency(), "USD");
        assertEquals(output.get(0).getPrice(), "100");
        assertEquals(output.get(1).getPrice(), "200");
    }
    @Test
    void enrichTradeDataOneItemWithBadDate() {
        List<InputTradeCsvRecord> input = List.of(createRecord("20150101","100","EUR","1"),
                createRecord("20169999","200","EUR","2"));
        List<OutputTradeCsvRecord> output = tradeDataEnricher.enrichTradeData(input);
        assertEquals(output.size(), 1);
    }
    @Test
    void enrichTradeDataOneItemWithBadProductId() {
        List<InputTradeCsvRecord> input = List.of(createRecord("20150101","100","EUR","1"),
                createRecord("20150101","200","EUR","2"),
                createRecord("20171010","400","EUR","3"));
        List<OutputTradeCsvRecord> output = tradeDataEnricher.enrichTradeData(input);
        assertEquals(output.size(), 3);
        assertEquals(output.get(2).getProductName(), "Missing Product Name");
    }
    
    InputTradeCsvRecord createRecord(String date, String price, String currency, String productId) {
        InputTradeCsvRecord record = new InputTradeCsvRecord();
        record.setDate(date);
        record.setPrice(price);
        record.setProductId(productId);
        record.setCurrency(currency);
        return record;
    }
}