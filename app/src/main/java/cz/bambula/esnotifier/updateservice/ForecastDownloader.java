package cz.bambula.esnotifier.updateservice;

import org.apache.commons.lang3.RegExUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import cz.bambula.esnotifier.forecast.Forecast;
import cz.bambula.esnotifier.forecast.ForecastXmlParser;

/**
 * Created by tkozel on 10/22/16.
 */

public class ForecastDownloader {
    public static Forecast getForecast(String forecastLink) throws IOException {
        URL url = new URL(forecastLink);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        InputStream stream = conn.getInputStream();
        String xml = streamToString(stream);
        // text and graphicname tags don't contain valid XML and are irrelevant
        // so they must be removed before parsing
        xml = RegExUtils.removeAll(xml, "(<text>.*</text>|<graphicname>.*</graphicname>)");
        Forecast forecast = ForecastXmlParser.parseForecast(new StringReader(xml));
        stream.close();
        conn.disconnect();

        return forecast;
    }

    private static String streamToString(InputStream stream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }
}
