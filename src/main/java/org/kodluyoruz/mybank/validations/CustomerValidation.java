package org.kodluyoruz.mybank.validations;

public interface CustomerValidation {

    default boolean checkCustomerName(String name) {
        boolean valid = name.matches("^[\\p{L} .'-]+$");

        return valid;
    }

    default boolean checkCustomerSurname(String surname) {
        boolean valid = surname.matches("^[\\p{L} .'-]+$");

        return valid;
    }

    default boolean checkCustomerTckn(String tckn) {
        boolean valid = tckn.matches("^-?\\d{1,11}$");
        return valid;
    }

    default String adjustNameAndSurname(String temp) {
        temp = temp.substring(0,1).toUpperCase() + temp.substring(1).toLowerCase();

        return temp;
    }
}
