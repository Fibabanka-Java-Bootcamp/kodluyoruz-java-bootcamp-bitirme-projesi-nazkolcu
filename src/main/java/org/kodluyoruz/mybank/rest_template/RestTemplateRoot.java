package org.kodluyoruz.mybank.rest_template;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestTemplateRoot {

    @SerializedName("rates")
    @Expose
    private RestTemplateRates rates;
    @SerializedName("base")
    @Expose
    private String base;
    @SerializedName("date")
    @Expose
    private String date;

    public RestTemplateRates getRates() {
        return rates;
    }

    @Override
    public String toString() {
        return "Root{" +
                "rates=" + rates +
                ", base='" + base + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public void setRates(RestTemplateRates rates) {
        this.rates = rates;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
