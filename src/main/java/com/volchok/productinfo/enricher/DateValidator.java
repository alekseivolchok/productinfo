package com.volchok.productinfo.enricher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class DateValidator {

    @Value("${date_format}")
    private final String dateFormat;

    public boolean isValidDateFormat(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

        try {
            LocalDate ldt = LocalDate.parse(value, formatter);
            String result = ldt.format(formatter);
            return result.equals(value);
        } catch (DateTimeParseException e) {
            log.debug("Tried to parse String \"{}\" using \"{}\" format unsuccessfully.", value, dateFormat);
        }
        return false;
    }

}
