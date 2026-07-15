package com.caiohenrique.demo_park_api.parking;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class ParkingReceiptGeneratorTest {

    @Test
    void generate_shouldStartWithPrefix() {
        String receipt = ParkingReceiptGenerator.generate();
        assertTrue(receipt.startsWith("REC-"));
    }

    @Test
    void generate_shouldContainTimestamp() {
        String receipt = ParkingReceiptGenerator.generate();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // The receipt should contain today's date prefix
        String todayPrefix = "REC-" + timestamp.substring(0, 8);
        assertTrue(receipt.startsWith(todayPrefix),
                "Receipt should start with REC-YYYYMMDD. Actual: " + receipt);
    }

    @Test
    void generate_shouldContainDashBeforeRandomCode() {
        String receipt = ParkingReceiptGenerator.generate();
        // Format: REC-YYYYMMDDHHmmss-XXXXXXXX (8 chars after last dash)
        String[] parts = receipt.split("-");
        assertEquals(3, parts.length, "Receipt should have 3 parts separated by dashes");
    }

    @Test
    void generate_shouldHaveRandomCodeOfLength8() {
        String receipt = ParkingReceiptGenerator.generate();
        String randomCode = receipt.substring(receipt.lastIndexOf("-") + 1);
        assertEquals(8, randomCode.length(), "Random code should be 8 characters long");
    }

    @Test
    void generate_shouldHaveRandomCodeInUppercase() {
        String receipt = ParkingReceiptGenerator.generate();
        String randomCode = receipt.substring(receipt.lastIndexOf("-") + 1);
        assertEquals(randomCode.toUpperCase(), randomCode, "Random code should be uppercase");
    }

    @Test
    void generate_shouldProduceUniqueReceipts() {
        String receipt1 = ParkingReceiptGenerator.generate();
        String receipt2 = ParkingReceiptGenerator.generate();
        assertNotEquals(receipt1, receipt2, "Two generated receipts should be different");
    }

    @Test
    void generate_shouldMatchExpectedPattern() {
        String receipt = ParkingReceiptGenerator.generate();
        // Pattern: REC- + 14 digits (yyyyMMddHHmmss) + - + 8 alphanumeric uppercase chars
        assertTrue(receipt.matches("^REC-\\d{14}-[A-Z0-9]{8}$"),
                "Receipt format is invalid. Actual: " + receipt);
    }
}
