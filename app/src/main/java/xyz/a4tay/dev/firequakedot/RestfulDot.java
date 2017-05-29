package xyz.a4tay.dev.firequakedot;

import android.util.JsonReader;
import android.util.Log;
import android.widget.EditText;

import android.widget.Toast;
import org.json.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Daniel Montilla on 5/19/2017.
 */

public class RestfulDot
    {

    private static final String LOG_TAG = RestfulDot.class.getSimpleName();

    public static String getPostDataString(JSONObject params) throws Exception
        {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext())
            {

            String key = itr.next();Object value = params.get(key);


            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
            }
        return result.toString();
        }

    public static String postURL(String stringURL)
        {
        try
            {
            URL url = new URL(stringURL); // here is your URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(10000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            //writer.write(getPostDataString(params));

            Log.d(LOG_TAG, writer.toString());

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK)
                {

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer("");
                String line = "";

                if ((line = in.readLine()) != null)
                    {
                    stringBuffer.append(line);
                    return stringBuffer.toString();
                    }

                in.close();
                return stringBuffer.toString();

                } else
                {
                Log.e(LOG_TAG, String.valueOf(responseCode));
                return "in.readLine didn't work";
                }
            } catch (Exception e)
            {
            Log.e(LOG_TAG, "Exception", e);
            return "Nothing happened: EXCEPTION";
            }
        }

    public static JSONObject getURL(JSONObject locJSON) throws Exception
        {
        StringBuilder result = new StringBuilder();
        URL url = new URL("http://dev.4tay.xyz:8080/yuri/api/location?lat="+locJSON.getString("lat")+"2&lng="+locJSON.getString("lng")+"&range="+locJSON.getString("range"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
        result.append(line);
        }
        rd.close();
        JSONObject JSONresult = new JSONObject(result.toString());
        return JSONresult;
        }
    }
