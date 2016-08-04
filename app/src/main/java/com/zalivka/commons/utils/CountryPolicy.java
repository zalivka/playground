package com.zalivka.commons.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class CountryPolicy {

    public interface ICountry {
        void onCountryDetermined(String country);
        void onCountryFailed();
    }

    private static final String UNKNOWN_COUNTRY = "<unknown>";

    private static final Locale sLocale = Locale.getDefault();
    private static String sCountry = getCountryByPhone();

    private static ArrayList<String> sDeadbeats = new ArrayList<String>(Arrays.asList(new String[] {
            "vn", "ua", "th", "ph", "si", "sk", "sr", "ro", "id", "in", "gr", "za", "eg"
    }));

    private static ArrayList<String> sDeeppockets = new ArrayList<String>(Arrays.asList(new String[] {
            "se", "us", "pt", "no", "lt", "lv", "il", "fi", "ie", "dk", "es", "kr", "jp", "ch", "it", "ch", "li", "at", "fr",
            "ca", "be", "sg", "nz", "gb", "au", "nl", "cz", "tw"
    }));

    private static ArrayList<String> sOkay = new ArrayList<String>(Arrays.asList(new String[] {
            "ru", "br", "hu", "hr", "bg"
    }));

    private static ArrayList<String> sCis = new ArrayList<String>(Arrays.asList(new String[] {
            "ru", "ua", "by", "kz",
    }));

    public static boolean isRich() {
        return sDeeppockets.contains(sCountry.toLowerCase());
    }

    public static boolean isPoor() {
        return sDeadbeats.contains(sCountry.toLowerCase());
    }

    public static boolean isOkay() {
        return sOkay.contains(sCountry.toLowerCase());
    }

    public static boolean hideCommunityPack() {
        return false;//!isCis();
    }

    public static boolean isRu() {
        return "ru".equalsIgnoreCase(sLocale.getLanguage()) || "ru".equalsIgnoreCase(sLocale.getCountry());
    }

    public static boolean isCis() {
        return sCis.contains(sCountry.toLowerCase());
    }

    public static String getDeterminedCountry() {
        return sCountry;
    }

    private static String getCountryByPhone() {
        try {
            TelephonyManager tm = (TelephonyManager) StaticContextHolder.mCtx.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE)
                return UNKNOWN_COUNTRY;

            String data;
            if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM)
                data = tm.getSimCountryIso();
            else
                data = tm.getNetworkCountryIso();

            return TextUtils.isEmpty(data) ? UNKNOWN_COUNTRY : data;
        } catch (Exception e) {
            return UNKNOWN_COUNTRY;
        }
    }

    public static void getCountryAsync(final ICountry callback) {
        // first trying to use the telephony manager (doesn't work without SIM-card)
        String simData = getCountryByPhone();
        if (!UNKNOWN_COUNTRY.equals(simData)) {
            callback.onCountryDetermined(simData);
            return;
        }
        
        // using geolocation API
        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... uri) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                String result = UNKNOWN_COUNTRY;
                try {
                    response = httpclient.execute(new HttpGet(uri[0]));
                    StatusLine statusLine = response.getStatusLine();
                    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        String responseString = out.toString();
                        CountryGson g = Stuff.sGson.fromJson(responseString, CountryGson.class);
                        result = g.countryCode.toLowerCase();
                        out.close();
                    } else{
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (UNKNOWN_COUNTRY.equals(result))
                    callback.onCountryFailed();
                else {
                    callback.onCountryDetermined(result);
                    sCountry = result;
                }
            }
        }.execute("http://ip-api.com/json");
    }

    private static class CountryGson {
        String countryCode;
    }
}
