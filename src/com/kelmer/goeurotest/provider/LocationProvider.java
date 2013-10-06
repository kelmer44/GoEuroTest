
package com.kelmer.goeurotest.provider;

import android.util.Log;

import com.kelmer.goeurotest.model.RemoteLocation;
import com.kelmer.goeurotest.util.AndroidUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class LocationProvider {

    private static final String LOG_TAG = LocationProvider.class.getName();
    private static final int RESULT_IS_EMPTY = 2;
    private final String API_ENDPOINT = "http://pre.dev.goeuro.de:12345/api/v1/suggest/position/en/name/";
    private final int NUM_TRIES = 3;

    public List<RemoteLocation> autocompleteLocations(final String query) throws IOException
    {

        final StringBuilder uri = new StringBuilder(API_ENDPOINT);
        uri.append(query);

        final CharSequence response = scrape(uri.toString(), Charset.forName("UTF-8"), NUM_TRIES);

        try
        {
            final List<RemoteLocation> resultList = new ArrayList<RemoteLocation>();
            final JSONObject head = new JSONObject(response.toString());
            final JSONArray results = head.getJSONArray("results");

            final int nResults = results.length();

            for (int i = 0; i < nResults; i++)
            {
                final JSONObject res = results.optJSONObject(i);
                final RemoteLocation location = RemoteLocation.fromJson(res);
                resultList.add(location);
            }
            return resultList;

        } catch (final JSONException e)
        {
            throw new RuntimeException("cannot parse: '" + uri.toString() + "' on " + uri, e);
        }
    }

    public static final CharSequence scrape(final String urlStr, Charset encoding, int tries)
            throws IOException
    {
        // It either fails or produces useable results
        while (true)
        {
            try
            {
                final StringBuilder buffer = new StringBuilder();
                final HttpURLConnection connection = AndroidUtils.buildHttpUrlConnection(urlStr);

                final int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK)
                {
                    final InputStream is = connection.getInputStream();
                    final Reader pageReader = new InputStreamReader(is, encoding);
                    AndroidUtils.copy(pageReader, buffer);
                    pageReader.close();

                    if (buffer.length() > RESULT_IS_EMPTY)
                    {
                        return buffer;
                    }
                    else
                    {
                        final String message = "got empty page (length: " + buffer.length() + ")";
                        if (tries-- > 0){
                            Log.i(LOG_TAG, message + ", retrying...");
                        }
                        else{
                            Log.e(LOG_TAG, message);
                            throw new IOException(message + ": " + urlStr);
                        }
                    }
                }
                else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN)
                {
                    Log.e(LOG_TAG, "Url location is forbidden");
                }
                else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                {
                    Log.e(LOG_TAG, "Request is malformed");
                }
                else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND)
                {
                    Log.e(LOG_TAG, "Url location was not found");
                }
                else
                {
                    final String message = "got response: " + responseCode + " " + connection.getResponseMessage();
                    if (tries-- > 0) {
                        Log.i(LOG_TAG, message + ", retrying...");
                    }
                    else {
                        Log.e(LOG_TAG, message);
                        throw new IOException(message + ": " + urlStr);
                    }
                }
            } catch (final SocketTimeoutException e)
            {
                if (tries-- > 0) {
                    Log.i(LOG_TAG, "socket timed out, retrying...");
                }
                else {
                    Log.e(LOG_TAG, "Socket timed out too many times.");
                    throw e;
                }
            }
        }
    }

}
