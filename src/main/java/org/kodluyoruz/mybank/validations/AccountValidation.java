package org.kodluyoruz.mybank.validations;

public interface AccountValidation {


    default boolean checkCurrency(String currency) {
        if (currency.equals("USD") || currency.equals("EUR") || currency.equals("TRY")) return true;
        else return false;
    }

    default boolean checkIban(String iban) {
        if (iban.length() == 26) {

            String letter = iban.substring(0, 2);
            String number = iban.substring(3, 26);
            if (letter.equals("TR") && number.matches("^-?\\d{1,24}$")) {
                return true;
            } else return false;

        } else
            return false;


    }
}
