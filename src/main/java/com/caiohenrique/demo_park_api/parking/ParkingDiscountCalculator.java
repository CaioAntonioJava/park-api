package com.caiohenrique.demo_park_api.parking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParkingDiscountCalculator {

    private static final BigDecimal DISCOUNT_RATE = BigDecimal.valueOf(0.30);

    private static final long DISCOUNT_ELIGIBILITY_INTERVAL = 10;

    public static BigDecimal calculateDiscount(BigDecimal parkingFee, long completedSessions) {

        return isEligibleForDiscount(completedSessions) ? parkingFee.multiply(DISCOUNT_RATE) : BigDecimal.ZERO;
    }

    private static boolean isEligibleForDiscount(long completedSessions) {
        return completedSessions > 0 && completedSessions % DISCOUNT_ELIGIBILITY_INTERVAL == 0;
    }
}