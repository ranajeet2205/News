package com.example.android.news;

import android.text.TextUtils;
import android.util.Log;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class QueryUtils {

    public static final String LOG_TAG = MainActivity.class.getName();

    private QueryUtils() {
    }

    private static List<News> extractFeatureFromJson(String NewsJSON){

        if (TextUtils.isEmpty(NewsJSON)){
            return null;
        }

        List<News> newsList = new ArrayList<>();

        try{
            //create base json object
            JSONObject baseJsonObject = new JSONObject(NewsJSON);
            //create response object from base json object
            JSONObject responseObject = baseJsonObject.getJSONObject("response");
            //create json array from json object
            JSONArray newsArray = responseObject.getJSONArray("results");

            for (int i=0;i<newsArray.length();i++){

                JSONObject currentNewsObject =newsArray.getJSONObject(i);
                //getting json objects from the array

                JSONArray tags = currentNewsObject.getJSONArray("tags");

                String sectionName = currentNewsObject.getString("sectionName");

                String author = "author name not found";
                if (tags != null && tags.length() > 0){
                    author = tags.getJSONObject(0).getString("firstName") + " " + tags.getJSONObject(0).getString("lastName");
                }

                String publicationDate = currentNewsObject.getString("webPublicationDate");

                //format date from iso 8601 standard to normal view of date and time
                publicationDate = formatDate(publicationDate);

                String webTitle = currentNewsObject.getString("webTitle");

                String webUrl = currentNewsObject.getString("webUrl");

                News news = new News(webTitle,sectionName,author,publicationDate,webUrl);
                //Add items to newslist
                newsList.add(news);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the News JSON results", e);
        }

        return  newsList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        int readTimeOut=10000;
        int connectTimeOut = 15000;

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            int responseCode = urlConnection.getResponseCode();
            urlConnection.setReadTimeout(readTimeOut);
            urlConnection.setConnectTimeout(connectTimeOut);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful
            // then read the input stream and parse the response.
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
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
     * Query the News data and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {

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
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link News}s

        return news;
    }


    //Formatting ISO 8601 standard date to normal date and time

    private static String formatDate(String rawDate) {
        //json date pattern
        String jsonDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat jsonFormatter = new SimpleDateFormat(jsonDatePattern, Locale.ENGLISH);
        try {
            Date parsedJsonDate = jsonFormatter.parse(rawDate);
            //converting to the new pattern
            String finalDatePattern = "MMM d, yyy, H:m:s";
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDatePattern, Locale.ENGLISH);
            return finalDateFormatter.format(parsedJsonDate);
        } catch (ParseException e) {
            Log.e("QueryUtils", "Error parsing JSON date: ", e);
            return "";
        }
    }

}
