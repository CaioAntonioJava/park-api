package com.caiohenrique.demo_park_api.parking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingReceiptGenerator {

    private static final DateTimeFormatter RECEIPT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final String RECEIPT_PREFIX = "REC-";

    public static String generate() {

        String timestamp = LocalDateTime.now()
                .format(RECEIPT_FORMATTER);

        String randomCode = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        return RECEIPT_PREFIX + timestamp + "-" + randomCode;
    }
}