package com.volchok.productinfo.enricher;

import com.volchok.productinfo.csvprocessing.ProductCsvSearcher;
import com.volchok.productinfo.model.InputTradeCsvRecord;
import com.volchok.productinfo.model.OutputTradeCsvRecord;
import com.volchok.productinfo.model.StoredProductCsvRecord;
import org.springframework.lang.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradeDataEnricher {
    public static final String MISSING_PRODUCT_NAME = "Missing Product Name";
    private final ProductCsvSearcher productCsvSearcher;
    private final DateValidator dateValidator;
    public List<OutputTradeCsvRecord> enrichTradeData(@NonNull List<InputTradeCsvRecord> input) {
        List<OutputTradeCsvRecord> enrichedTradeData = input.stream()
                .filter(this::validateTradeData)
                .map(this::enrichRecord)
                .collect(Collectors.toList());
        log.debug("{} tried to enrich {} user input records and the output is {} enriched records",
                this.getClass(), input.size(), enrichedTradeData.size());
        return enrichedTradeData;
    }

    private OutputTradeCsvRecord enrichRecord(InputTradeCsvRecord inputRecord) {
        StoredProductCsvRecord foundProduct = productCsvSearcher.findRecord(inputRecord.getProductId());
        String productName;
        if (foundProduct != null) {
            productName = foundProduct.getProductName();
        } else {
            productName = MISSING_PRODUCT_NAME;
        }
        return OutputTradeCsvRecord.builder()
                .date(inputRecord.getDate())
                .price(inputRecord.getPrice())
                .currency(inputRecord.getCurrency())
                .productName(productName)
                .build();
    }

    private boolean validateTradeData(InputTradeCsvRecord item) {
        boolean isValidDateFormat = dateValidator.isValidDateFormat(item.getDate());
        if (!isValidDateFormat) {
            log.error("User record \"{}\" has a wrong date and will not be processed. Please use \"{}\" date format.",
                    item, dateValidator.getDateFormat());
            return false;
        }
        return true;
    }
}
