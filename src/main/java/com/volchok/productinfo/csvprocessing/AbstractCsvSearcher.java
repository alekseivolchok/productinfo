package com.volchok.productinfo.csvprocessing;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.volchok.productinfo.model.StoredCsvRecord;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractCsvSearcher<CSV_RECORD_TYPE extends StoredCsvRecord> {

    private final String resourceCsvPath;
    private final Class<CSV_RECORD_TYPE> csvRecordType;

    private ConcurrentMap<String, CSV_RECORD_TYPE> store;

    public AbstractCsvSearcher(String resourceCsvPath, Class<CSV_RECORD_TYPE> csvRecordType) {
        this.resourceCsvPath = resourceCsvPath;
        this.csvRecordType = csvRecordType;
    }

    public CSV_RECORD_TYPE findRecord(String input) {
        if (!store.containsKey(input)) {
            log.warn("Unsuccessfully tried to find \"{}\" using {} ", input, this.getClass());
            return null;
        } else {
            return store.get(input);
        }
    }

    @PostConstruct
    public void init() {
        try (InputStream inputStream = this.getClass().getResourceAsStream(resourceCsvPath)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not load resource: %s".formatted(resourceCsvPath));
            }
            CsvToBean<CSV_RECORD_TYPE> csvToBean =
                    new CsvToBeanBuilder<CSV_RECORD_TYPE>(new BufferedReader(new InputStreamReader(inputStream)))
                    .withType(csvRecordType)
                    .build();
            List<CSV_RECORD_TYPE> records = csvToBean.parse();
            this.store = records.stream().collect(Collectors.toConcurrentMap(StoredCsvRecord::getKey, Function.identity()));
        } catch (IOException e) {
            log.error("Error during reading our static data. Path: {}, searcher: {}", resourceCsvPath, this.getClass());
            throw new RuntimeException(e);
        }
    }
}
