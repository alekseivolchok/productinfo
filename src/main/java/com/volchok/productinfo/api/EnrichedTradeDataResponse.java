package com.volchok.productinfo.api;

import com.volchok.productinfo.model.OutputTradeCsvRecord;

import java.util.Comparator;
import java.util.List;

public class EnrichedTradeDataResponse extends CsvRequest<OutputTradeCsvRecord> {
    private final static List<String> outputColumnOrder = List.of("date", "product_name", "currency", "price");

    @Override
    public Comparator<String> getColumnOrder() {
        return Comparator.comparingInt(o -> outputColumnOrder.indexOf(o.toLowerCase()));
    }
}
