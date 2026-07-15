package com.caiohenrique.demo_park_api.parking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ParkingFeeCalculatorTest {

    @Test
    void calculate_withExitBeforeEntry_shouldThrowIllegalArgumentException() {
        LocalDateTime entry = LocalDateTime.of(2026, 7, 15, 10, 0);
        LocalDateTime exit = LocalDateTime.of(2026, 7, 15, 9, 0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ParkingFeeCalculator.calculate(entry, exit)
        );

        assertTrue(exception.getMessage().contains("posterior ao horário de entrada"));
    }

    @Test
    void calculate_withEqualEntryAndExit_shouldNotThrow() {
        LocalDateTime entry = LocalDateTime.of(2026, 7, 15, 10, 0);
        LocalDateTime exit = LocalDateTime.of(2026, 7, 15, 10, 0);

        // 0 minutes <= 15, so it should return the first 15 minutes rate without throwing
        BigDecimal fee = ParkingFeeCalculator.calculate(entry, exit);
        assertEquals(0, BigDecimal.valueOf(5.00).compareTo(fee));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 5.00",
            "1, 5.00",
            "15, 5.00"
    })
    void calculate_withUpTo15Minutes_shouldReturnFirst15MinutesRate(long minutes, double expected) {
        LocalDateTime entry = LocalDateTime.of(2026, 7, 15, 10, 0);
        LocalDateTime exit = entry.plusMinutes(minutes);

        BigDecimal fee = ParkingFeeCalculator.calculate(entry, exit);

        assertEquals(0, BigDecimal.valueOf(expected).compareTo(fee),
                "Expected " + expected + " but got " + fee);
    }

    @ParameterizedTest
    @CsvSource({
            "16, 9.25",
            "30, 9.25",
            "60, 9.25"
    })
    void calculate_withBetween16And60Minutes_shouldReturnFirstHourRate(long minutes, double expected) {
        LocalDateTime entry = LocalDateTime.of(2026, 7, 15, 10, 0);
        LocalDateTime exit = entry.plusMinutes(minutes);

        BigDecimal fee = ParkingFeeCalculator.calculate(entry, exit);

        assertEquals(0, BigDecimal.valueOf(expected).compareTo(fee),
                "Expected " + expected + " but got " + fee);
    }

    @ParameterizedTest
    @CsvSource({
            "61, 11.00",
            "75, 11.00",
            "90, 12.75",
            "105, 14.50",
            "120, 16.25"
    })
    void calculate_withMoreThan60Minutes_shouldAddAdditionalPeriods(long minutes, double expected) {
        LocalDateTime entry = LocalDateTime.of(2026, 7, 15, 10, 0);
        LocalDateTime exit = entry.plusMinutes(minutes);

        BigDecimal fee = ParkingFeeCalculator.calculate(entry, exit);

        assertEquals(0, BigDecimal.valueOf(expected).compareTo(fee),
                "Expected " + expected + " but got " + fee + " for " + minutes + " minutes");
    }

    @Test
    void calculate_withExactBoundary_shouldReturnCorrectFee() {
        // 61 minutes = 1h + 1min extra -> (1+14)/15 = 1 additional period
        LocalDateTime entry = LocalDateTime.of(2026, 7, 15, 10, 0);
        LocalDateTime exit = LocalDateTime.of(2026, 7, 15, 11, 1);

        BigDecimal fee = ParkingFeeCalculator.calculate(entry, exit);

        // 9.25 + 1.75 = 11.00
        assertEquals(0, BigDecimal.valueOf(11.00).compareTo(fee));
    }

    @Test
    void calculate_withLongPeriod_shouldCalculateCorrectly() {
        // 3 hours = 180 minutes -> 120 extra minutes -> (120+14)/15 = 8 additional periods
        LocalDateTime entry = LocalDateTime.of(2026, 7, 15, 8, 0);
        LocalDateTime exit = LocalDateTime.of(2026, 7, 15, 11, 0);

        BigDecimal fee = ParkingFeeCalculator.calculate(entry, exit);

        // 9.25 + (8 * 1.75) = 9.25 + 14.00 = 23.25
        assertEquals(0, BigDecimal.valueOf(23.25).compareTo(fee));
    }

    @Test
    void calculate_withDifferentDays_shouldCalculateCorrectly() {
        LocalDateTime entry = LocalDateTime.of(2026, 7, 15, 22, 0);
        LocalDateTime exit = LocalDateTime.of(2026, 7, 16, 7, 0);

        // 9 hours = 540 minutes -> 480 extra minutes -> (480+14)/15 = 32 additional periods
        BigDecimal fee = ParkingFeeCalculator.calculate(entry, exit);

        // 9.25 + (32 * 1.75) = 9.25 + 56.00 = 65.25
        assertEquals(0, BigDecimal.valueOf(65.25).compareTo(fee));
    }
}
