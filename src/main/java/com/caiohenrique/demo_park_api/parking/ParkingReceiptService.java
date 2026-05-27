package com.caiohenrique.demo_park_api.parking;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingReceiptService {

    public static String generateReceipt() {

        LocalDateTime dateTime = LocalDateTime.now();

        String receipt = dateTime.toString().substring(0, 19)
                .replace("-", "")
                .replace(":", "")
                .replace("T", "");

        String uuidPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return "RECIBO-" + receipt + "-" + uuidPart;
    }
}
