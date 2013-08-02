package net.multipi.gcr;

/**
 * Created with IntelliJ IDEA.
 * User: marat
 * Date: 02.08.13
 * Time: 13:03
 */
public class Rate {

    private String ccy, asc, big;

    public Rate(String ccy, String big, String asc) {
        this.ccy = ccy;
        this.big = big;
        this.asc = asc;
    }

    public String getCcy() {
        return ccy;
    }

    public String getAsc() {
        return asc;
    }

    public String getBig() {
        return big;
    }
}
