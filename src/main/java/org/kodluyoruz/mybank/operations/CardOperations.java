package org.kodluyoruz.mybank.operations;

import org.kodluyoruz.mybank.validations.DoubleValidation;

import java.time.LocalDate;

public class CardOperations implements DoubleOperation, DoubleValidation {

    public int cvcGenerator() {
        int cvc = (100 + (int) (Math.random() * 999));
        return cvc;
    }

    public LocalDate expirationDateGenerator()
    {
        LocalDate expirationDate = LocalDate.now().plusYears(4);
        return expirationDate;
    }
}
