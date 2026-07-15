package com.caiohenrique.demo_park_api.parking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ParkingDiscountCalculatorTest {

    @Test
    void calculateDiscount_withZeroCompletedSessions_shouldReturnZero() {
        BigDecimal fee = BigDecimal.valueOf(100.00);
        BigDecimal discount = ParkingDiscountCalculator.calculateDiscount(fee, 0);

        assertEquals(BigDecimal.ZERO.setScale(2), discount.setScale(2));
    }

    @Test
    void calculateDiscount_withNegativeCompletedSessions_shouldReturnZero() {
        BigDecimal fee = BigDecimal.valueOf(100.00);
        BigDecimal discount = ParkingDiscountCalculator.calculateDiscount(fee, -1);

        assertEquals(BigDecimal.ZERO.setScale(2), discount.setScale(2));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 19, 21, 29, 31})
    void calculateDiscount_withNonEligibleSessions_shouldReturnZero(long completedSessions) {
        BigDecimal fee = BigDecimal.valueOf(100.00);
        BigDecimal discount = ParkingDiscountCalculator.calculateDiscount(fee, completedSessions);

        assertEquals(BigDecimal.ZERO.setScale(2), discount.setScale(2));
    }

    @ParameterizedTest
    @ValueSource(longs = {10, 20, 30, 40, 50})
    void calculateDiscount_withEligibleSessions_shouldApply30PercentDiscount(long completedSessions) {
        BigDecimal fee = BigDecimal.valueOf(100.00);
        BigDecimal discount = ParkingDiscountCalculator.calculateDiscount(fee, completedSessions);

        BigDecimal expected = BigDecimal.valueOf(30.00);
        assertEquals(expected.setScale(2), discount.setScale(2));
    }

    @Test
    void calculateDiscount_withEligibleSessionsAndDifferentFee_shouldCalculateCorrectly() {
        BigDecimal fee = BigDecimal.valueOf(45.50);
        BigDecimal discount = ParkingDiscountCalculator.calculateDiscount(fee, 10);

        // 45.50 * 0.30 = 13.65
        BigDecimal expected = BigDecimal.valueOf(13.65);
        assertEquals(expected.setScale(2), discount.setScale(2));
    }

    @Test
    void calculateDiscount_withZeroFee_shouldReturnZero() {
        BigDecimal fee = BigDecimal.ZERO;
        BigDecimal discount = ParkingDiscountCalculator.calculateDiscount(fee, 10);

        assertEquals(BigDecimal.ZERO.setScale(2), discount.setScale(2));
    }

    @Test
    void calculateDiscount_withVerySmallFee_shouldCalculateCorrectly() {
        BigDecimal fee = BigDecimal.valueOf(0.50);
        BigDecimal discount = ParkingDiscountCalculator.calculateDiscount(fee, 10);

        // 0.50 * 0.30 = 0.15
        BigDecimal expected = BigDecimal.valueOf(0.15);
        assertEquals(expected.setScale(2), discount.setScale(2));
    }
}
