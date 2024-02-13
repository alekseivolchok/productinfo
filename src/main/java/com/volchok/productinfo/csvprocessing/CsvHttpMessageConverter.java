package com.volchok.productinfo.csvprocessing;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.volchok.productinfo.api.CsvRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class CsvHttpMessageConverter<CSV_RECORD_TYPE, CSV_COLLECTION_TYPE extends CsvRequest<CSV_RECORD_TYPE>>
        extends AbstractHttpMessageConverter<CSV_COLLECTION_TYPE> {

    public CsvHttpMessageConverter () {
        super(new MediaType("text", "csv"));
    }

    @Override
    protected boolean supports(@NonNull Class<?> clazz) {
        return CsvRequest.class.isAssignableFrom(clazz);
    }

    @Override
    protected @NonNull CSV_COLLECTION_TYPE readInternal(@NonNull Class<? extends CSV_COLLECTION_TYPE> clazz,
                                                        @NonNull HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        HeaderColumnNameMappingStrategy<CSV_RECORD_TYPE> strategy = new HeaderColumnNameMappingStrategy<>();
        Class<CSV_RECORD_TYPE> csvRecordBeanType = toBeanType(clazz.getGenericSuperclass());
        strategy.setType(csvRecordBeanType);

        InputStreamReader inputStreamReader = createInputStreamReader(inputMessage);
        CSV_COLLECTION_TYPE records = instantiateRecordCollection(clazz);
        assert records != null;
        try (CSVReader reader = new CSVReaderBuilder(inputStreamReader).build()) {
            CsvToBean<CSV_RECORD_TYPE> csvToBean = new CsvToBeanBuilder<CSV_RECORD_TYPE>(reader)
                    .withMappingStrategy(strategy)
                    .withThrowExceptions(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            List<CSV_RECORD_TYPE> beanList = csvToBean.parse();
            records.setRecords(beanList);
        } catch (Exception e) {
            log.error("{} tried to convert CSV data to {}, but something went wrong.", this.getClass(), records.getClass());
        }
        return records;
    }

    protected static InputStreamReader createInputStreamReader(HttpInputMessage inputMessage) throws IOException {
        final Charset contentCharSet =
                Optional.ofNullable(inputMessage.getHeaders().getContentType())
                        .map(MediaType::getCharset)
                        .orElse(StandardCharsets.UTF_8);
        return new InputStreamReader(inputMessage.getBody(), contentCharSet);
    }

    @Override
    protected void writeInternal(CSV_COLLECTION_TYPE records, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        HeaderColumnNameMappingStrategy<CSV_RECORD_TYPE> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(toBeanType(records.getClass().getGenericSuperclass()));
        strategy.setColumnOrderOnWrite(records.getColumnOrder());

        OutputStreamWriter outputStream = new OutputStreamWriter(outputMessage.getBody());
        StatefulBeanToCsv<CSV_RECORD_TYPE> beanToCsv =
                new StatefulBeanToCsvBuilder<CSV_RECORD_TYPE>(outputStream)
                        .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                        .withMappingStrategy(strategy)
                        .build();
        try {
            beanToCsv.write(records.getRecords());
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("{} tried to convert {} to CSV data, but something went very wrong.", this.getClass(), records.getClass());
            throw new RuntimeException(e);
        }
        outputStream.close();
    }

    protected CSV_COLLECTION_TYPE instantiateRecordCollection(Class<? extends CSV_COLLECTION_TYPE> clazz) {
        Optional<Constructor<?>> cons = Arrays.stream(clazz.getDeclaredConstructors()).filter(constructor -> constructor.getParameterCount() == 0).findAny();
        if (cons.isPresent()) {
            try {
                //noinspection unchecked
                return (CSV_COLLECTION_TYPE) cons.get().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error("{} tried to convert CSV data to {}, but couldn't instantiate the record collection. " +
                        "Something went very wrong.", this.getClass(), clazz);
                throw new RuntimeException(e);
            }
        } else {
            log.error("{} tried to convert CSV data to {}, but couldn't instantiate the record collection. " +
                    "No default constructor.", this.getClass(), clazz);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Class<CSV_RECORD_TYPE> toBeanType(Type type) {
        return (Class<CSV_RECORD_TYPE>) ((ParameterizedType) type).getActualTypeArguments()[0];
    }
}
