package org.kodluyoruz.mybank.operations;

import org.kodluyoruz.mybank.validations.DoubleValidation;

import java.time.LocalDate;

public class CardOperations implements DoubleOperation, DoubleValidation {

    public int cvcGenerator() {
        int cvv = (100 + (int) (Math.random() * 899));
        return cvv;
    }

    public LocalDate expirationDateGenerator()
    {
        LocalDate expirationDate = LocalDate.now().plusYears(4);
        return expirationDate;
    }
}
