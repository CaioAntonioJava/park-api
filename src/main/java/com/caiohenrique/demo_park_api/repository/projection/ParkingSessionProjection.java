package com.caiohenrique.demo_park_api.repository.projection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonPropertyOrder({
        "clientCpf",
        "receiptNumber",
        "spotCode",
        "licensePlate",
        "brand",
        "model",
        "color",
        "checkIn",
        "checkOut",
        "parkingFee",
        "discount"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ParkingSessionProjection {

    String getReceiptNumber();

    String getLicensePlate();

    String getBrand();

    String getModel();

    String getColor();

    String getClientCpf();

    String getSpotCode();

    String getParkingSpotSpotCode();

    LocalDateTime getCheckIn();

    LocalDateTime getCheckOut();

    BigDecimal getParkingFee();

    BigDecimal getDiscount();
}