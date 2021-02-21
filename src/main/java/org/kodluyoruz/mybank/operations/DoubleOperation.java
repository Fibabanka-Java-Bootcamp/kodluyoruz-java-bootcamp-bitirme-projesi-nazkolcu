package org.kodluyoruz.mybank.operations;

public interface DoubleOperation {
    default double adjustDoubleDigit(Double amount, int digit) {
        String temp = "";

        if (digit == 2) {
            temp = String.format("%1$,.2f", amount);

        } else if (digit == 4) {
            temp = String.format("%1$,.4f", amount);//"%1$,.04f"

        }
        amount = adjustStringToDouble(temp);

        return amount;
    }

    default double adjustStringToDouble(String money) {
        money = money.replaceAll("\\.", "");
        money = money.replaceAll("\\,", ".");
        double amount = Double.valueOf(money);
        return amount;
    }


}
