package com.volchok.productinfo.api;

import lombok.Data;

import java.util.Comparator;
import java.util.List;

@Data
public class CsvRequest<T> {
    private List<T> records;

    public Comparator<String> getColumnOrder() {
        return Comparator.naturalOrder();
    }
}
