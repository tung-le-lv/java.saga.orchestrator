package com.openmind.order.api.dto;

public record AddressRequest(
        String street,
        String city,
        String state,
        String zipCode,
        String country) {
}
