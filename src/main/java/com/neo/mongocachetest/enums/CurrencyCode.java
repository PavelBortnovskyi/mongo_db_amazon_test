package com.neo.mongocachetest.enums;

public enum CurrencyCode {

    USD("United States Dollar"),
    EUR("Euro"),
    GBP("British Pound Sterling"),
    JPY("Japanese Yen"),
    AUD("Australian Dollar"),
    CAD("Canadian Dollar"),
    CHF("Swiss Franc"),
    CNY("Chinese Yuan"),
    SEK("Swedish Krona"),
    NZD("New Zealand Dollar"),
    UAH("Ukrainian Hryvnia");

    private final String fullName;

    CurrencyCode(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}