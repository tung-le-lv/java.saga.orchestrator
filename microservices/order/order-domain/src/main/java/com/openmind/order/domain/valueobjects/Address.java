package com.openmind.order.domain.valueobjects;

import com.openmind.shared.domain.ValueObject;

import java.util.List;

public class Address extends ValueObject {

    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    // Required for MongoDB deserialization
    protected Address() {
    }

    private Address(String street, String city, String state, String zipCode, String country) {
        this.street = requireNonBlank(street, "Street");
        this.city = requireNonBlank(city, "City");
        this.state = state == null ? "" : state;
        this.zipCode = zipCode == null ? "" : zipCode;
        this.country = requireNonBlank(country, "Country");
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        String stateSuffix = state == null || state.isBlank() ? "" : ", " + state;
        return street + ", " + city + stateSuffix + " " + zipCode + ", " + country;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return List.of(street, city, state, zipCode, country);
    }
}
