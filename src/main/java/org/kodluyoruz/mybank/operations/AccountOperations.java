package org.kodluyoruz.mybank.operations;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AccountOperations {
    public String ibanGenerator(int accountCount, String currency, Long customerNumber) {
        String type = "2";
        accountCount += 1;
        String accountCountString = String.format("%03d", accountCount);
        switch (currency) {
            case "TRY":
                currency = "01";
                break;
            case "USD":
                currency = "02";
                break;
            case "EUR":
                currency = "03";
                break;
            default:
               throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Currency is not supported. You may choose : TRY / EUR / USD ");

        }
        String accountNumber = customerNumber.toString() + accountCountString + type + currency;

        String iban = "TR" + "34" + "10379" + "0" + accountNumber;

        return iban;
    }
}
