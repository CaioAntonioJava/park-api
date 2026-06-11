package com.caiohenrique.demo_park_api.parking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingFeeCalculator {

    private static final BigDecimal FIRST_15_MINUTES_RATE = BigDecimal.valueOf(5.00);

    private static final BigDecimal FIRST_HOUR_RATE = BigDecimal.valueOf(9.25);

    private static final BigDecimal ADDITIONAL_15_MINUTES_RATE = BigDecimal.valueOf(1.75);

    public static BigDecimal calculate(LocalDateTime entryTime, LocalDateTime exitTime) {

        if (exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException(
                    "O horário de saída deve ser posterior ao horário de entrada.");
        }

        long totalMinutes = entryTime.until(exitTime, ChronoUnit.MINUTES);

        if (totalMinutes <= 15) {
            return FIRST_15_MINUTES_RATE;
        }

        if (totalMinutes <= 60) {
            return FIRST_HOUR_RATE;
        }

        long extraMinutes = totalMinutes - 60;
        long additionalPeriods = (extraMinutes + 14) / 15;

        BigDecimal additionalFee = ADDITIONAL_15_MINUTES_RATE.multiply(BigDecimal.valueOf(additionalPeriods));

        return FIRST_HOUR_RATE.add(additionalFee).setScale(2, RoundingMode.HALF_EVEN);
    }
}