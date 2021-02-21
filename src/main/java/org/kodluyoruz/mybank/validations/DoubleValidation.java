package org.kodluyoruz.mybank.validations;

public interface DoubleValidation {
    default boolean checkMoneyFormat(String money) {

        String regex = "^(?:(?![,0-9]{14})(?![.0-9]{14})\\d{1,3}(?:\\.\\d{3})*(?:\\,\\d{1,2})?)$";

        return money.matches(regex);
    }
}
