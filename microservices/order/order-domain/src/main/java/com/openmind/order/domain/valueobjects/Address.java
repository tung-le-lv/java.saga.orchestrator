package com.openmind.order.domain.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record Address(String street, String city, String state, String zipCode, String country) {

    public Address {
        street = requireNonBlank(street, "Street");
        city = requireNonBlank(city, "City");
        state = state == null ? "" : state;
        zipCode = zipCode == null ? "" : zipCode;
        country = requireNonBlank(country, "Country");
    }

    public static Address create(String street, String city, String state, String zipCode, String country) {
        return new Address(street, city, state, zipCode, country);
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value;
    }

    @Override
    public String toString() {
        String stateSuffix = state == null || state.isBlank() ? "" : ", " + state;
        return street + ", " + city + stateSuffix + " " + zipCode + ", " + country;
    }
}
