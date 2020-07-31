package com.example.cafeapp;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.text.HtmlCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QueryUtils {
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static ArrayList<Cafe> fetchCafeData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<Cafe> cafes = extractCafes(jsonResponse);

        // Return the {@link Event}
        return cafes;
    }

    /**
     * Returns new URL object from the given string URL.
     */

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(40000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Toast.makeText(getActivity,"dafdasfd",Toast.LENGTH_SHORT).show();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Cafe JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return a list of {@link Cafe} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Cafe> extractCafes(String cafeJSON) {

        if (TextUtils.isEmpty(cafeJSON)) {
            return null;
        }

        ArrayList<Cafe> cafes = new ArrayList<Cafe>();

        try{
            JSONObject jsonRootObject = new JSONObject(cafeJSON);
            JSONArray results = jsonRootObject.getJSONArray("results");
            Log.i("results", String.valueOf(results.length()));
            for(int i = 0; i<results.length(); i++){
                JSONObject cafeObject = results.getJSONObject(i);
                JSONObject location = cafeObject.getJSONObject("geometry").getJSONObject("location");
                //Log.i("location", String.valueOf(location));
                //Log.i("latitude", String.valueOf(location.getDouble("lat")));
                //Log.i("longitude", String.valueOf(location.getDouble("lng")));
                LatLng latLng = new LatLng(location.getDouble("lat"),location.getDouble("lng"));
                //Log.i("latlng", latLng.toString());

                String name = cafeObject.getString("name");
                //Log.i("name", name);

                String vicinity = cafeObject.getString("vicinity");
                //Log.i("vincitiy", vicinity);

                double rating = cafeObject.getDouble("rating");
                //Log.i("rating", String.valueOf(rating));

                cafes.add(new Cafe(name,latLng,vicinity,rating));
            }

            Collections.sort(cafes, new Comparator<Cafe>() {
                @Override
                public int compare(Cafe cafe, Cafe t1) {
                    return Double.compare(t1.getRating(),cafe.getRating());
                }
            });

        }catch (JSONException e){
            Log.e("QueryUtils", "Problem parsing the Cafe JSON results", e);
        }


        //Log.i(LOG_TAG,"can extract cafeJSON");

        return cafes;

    }


}
