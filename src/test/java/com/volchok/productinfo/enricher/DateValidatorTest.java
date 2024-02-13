package com.volchok.productinfo.enricher;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({"mock", "mock-api"})
class DateValidatorTest {

    @Autowired
    DateValidator dateValidator;

    @Test
    void isValidDateFormat() {
        String value = "20161010";
        assertTrue(dateValidator.isValidDateFormat(value));
    }
    @Test
    void isNotValidDateFormat() {
        String value = "20161313";
        assertFalse(dateValidator.isValidDateFormat(value));
    }
    @Test
    void isUnparseableDateFormat() {
        String value = "Not a date";
        assertFalse(dateValidator.isValidDateFormat(value));
    }
}