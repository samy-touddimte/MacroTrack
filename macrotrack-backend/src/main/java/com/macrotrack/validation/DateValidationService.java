package com.macrotrack.validation;

import com.macrotrack.shared.exception.DateRangeException;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class DateValidationService {

    public static final int MAX_DATE_RANGE_DAYS = 3650;
    public static final int MAX_PAST_YEARS_LIMIT = 5;

    private final Clock clock;

    public DateValidationService(Clock clock) {
        this.clock = clock;
    }

    public void validateDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new DateRangeException("'from' date must be before 'to' date");
        }
        if (ChronoUnit.DAYS.between(from, to) > MAX_DATE_RANGE_DAYS) {
            throw new DateRangeException("Date range must not exceed " + MAX_DATE_RANGE_DAYS + " days");
        }
    }

    public void validatePastDateLimit(LocalDate date) {
        if (date.isBefore(LocalDate.now(clock).minusYears(MAX_PAST_YEARS_LIMIT))) {
            throw new DateRangeException("Requested date is too far in the past (" + MAX_PAST_YEARS_LIMIT + " year limit)");
        }
    }

}
