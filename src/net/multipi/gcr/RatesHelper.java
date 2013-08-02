package net.multipi.gcr;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: marat
 * Date: 02.08.13
 * Time: 13:46
 */
public class RatesHelper {

    public static final String RATES_URI = "http://resources.finance.ua/ru/public/currency-cash.json";

    public static void refreshRates(final RatesLoadCallback callback) {
        Log.v(GlassService.TAG, "begin refresh...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Rate> currRates = new ArrayList<Rate>();
                    HttpResponse response = new DefaultHttpClient().execute(new HttpGet(RATES_URI));
                    String resp = EntityUtils.toString(response.getEntity());
                    JSONObject jo = new JSONObject(resp);
                    JSONArray arr = jo.optJSONArray("organizations");
                    if (arr.length()>0) {
                        JSONObject rates = arr.getJSONObject(0).getJSONObject("currencies");
                        if (rates.has("USD")) currRates.add(new Rate("USD", rates.getJSONObject("USD").getString("bid"), rates.getJSONObject("USD").getString("ask")));
                        if (rates.has("RUB")) currRates.add(new Rate("RUB", rates.getJSONObject("RUB").getString("bid"), rates.getJSONObject("RUB").getString("ask")));
                        if (rates.has("EUR")) currRates.add(new Rate("EUR", rates.getJSONObject("EUR").getString("bid"), rates.getJSONObject("EUR").getString("ask")));
                    }
                    Log.v(GlassService.TAG, "rates loaded");
                    callback.onRatesLoad(currRates);
                } catch (Exception e) {
                    Log.v(GlassService.TAG, "rates load fail "+e.getMessage());
                    e.printStackTrace();
                    callback.onRatesFail(e);
                }
            }
        }).start();
    }

    public interface RatesLoadCallback {
        public void onRatesLoad(List<Rate> rates);
        public void onRatesFail(Exception e);
    }
}
