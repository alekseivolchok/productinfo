package com.volchok.productinfo.model;

import lombok.Builder;

import java.util.List;

@Builder
public class TradeDataRequest {
    List<InputTradeCsvRecord> tradeRecords;
}
