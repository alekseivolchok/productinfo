package com.volchok.productinfo.api;

import com.volchok.productinfo.enricher.TradeDataEnricher;
import com.volchok.productinfo.model.OutputTradeCsvRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
@RequiredArgsConstructor
public class ApiController {
    private final TradeDataEnricher tradeDataEnricher;

    @Value("${health_check_message}")
    private final String healthCheckMessage;

    @PostMapping(value = "/enrich", consumes="text/csv", produces="text/csv")
    public ResponseEntity<EnrichedTradeDataResponse> enrich(@RequestBody TradeDataRequest request) {
        try {
            List<OutputTradeCsvRecord> enrichedData = tradeDataEnricher.enrichTradeData(request.getRecords());
            EnrichedTradeDataResponse response = new EnrichedTradeDataResponse();
            response.setRecords(enrichedData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Couldn't process user request. Please make sure you send well formed CSV file.", e);
        }
    }

    @GetMapping(path = "/health")
    public String health() {
        return healthCheckMessage;
    }

}
