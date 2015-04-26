package com.example.lbomma.countries.activities;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lbomma.countries.data.CountryContract.CountryEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by LBomma on 4/25/15.
 */
public class FetchCountriesTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchCountriesTask.class.getSimpleName();
    private final Context mContext;
    public FetchCountriesTask(Context context) {
        mContext = context;
    }


    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getCountriesDataFromJson(String countriesJsonStr)
            throws JSONException {

        // Country information fields
        final String COUNTRY_NAME = "name";
        final String COUNTRY_REGION = "region";
        final String COUNTRY_CAPITAL = "capital";
        final String COUNTRY_CURRENCY = "currency";
        final String COUNTRY_NATIONALITY = "nationality";
        final String COUNTRY_POPULATION = "population";

        // These are the names of the JSON objects that need to be extracted.

        JSONArray jsonArray = new JSONArray(countriesJsonStr);
        Integer count = jsonArray.length();
        //String[] resultStrs = new String[count];

        // Insert the  country information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(jsonArray.length());
        try {
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jb = (JSONObject)jsonArray.getJSONObject(i);

                ContentValues countryValues = new ContentValues();

                countryValues.put(CountryEntry.COLUMN_NAME, jb.getString(COUNTRY_NAME));
                countryValues.put(CountryEntry.COLUMN_REGION, jb.getString(COUNTRY_REGION));
                countryValues.put(CountryEntry.COLUMN_CAPITAL, jb.getString(COUNTRY_CAPITAL));
                countryValues.put(CountryEntry.COLUMN_CURRENCY, jb.getString(COUNTRY_CURRENCY));
                countryValues.put(CountryEntry.COLUMN_NATIONALITY, jb.getString(COUNTRY_NATIONALITY));
                countryValues.put(CountryEntry.COLUMN_POPULATION, jb.getString(COUNTRY_POPULATION));

                cVVector.add(countryValues);



            // To do check null condition for lat,lang
//                JSONArray latLong = jb.getJSONArray("latlng");
//                for(int j=0;j<latLong.length();j++)
//                {
//                    Double latLangJsonObj = (Double)latLong.getDouble(j);
//                    System.out.println( "latLangJsonObj are" + latLangJsonObj) ;
//
//                }

//                country.setLangitude((JSONObject)latLong.get(""));
//
//                 String message = "Country : " + jb.getString("name") + " , " +
//                    "Region : " + jb.getString("region");
//                 resultStrs[i] =  message;
//                 //System.out.println( "country details are" + message) ;
            }
            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(CountryEntry.CONTENT_URI, cvArray);
            }
            Log.d(LOG_TAG, "FetchCountryTask Complete. " + inserted + " Inserted countries info");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
    @Override
    protected Void doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String countriesJsonStr = null;

        try {
            // Construct the URL for the Mshapes countries query
            // https://restcountries.p.mashape.com/all
            // Pass the api key through request
            final String COUNTRIES_URL = "https://restcountries.p.mashape.com/all";
            final String MSHAPES_KEY = "X-Mashape-Key";
            final String CONTENT_TYPE = "application/json;charset=utf-8";
            final String mShapesApiKey = "kDvnCaZlCGmshimVbAnTPcyYQIO6p1of3IkjsnWLowpYbSwyZw";

            Uri builtUri = Uri.parse(COUNTRIES_URL).buildUpon().build();
            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty(MSHAPES_KEY, mShapesApiKey);
            urlConnection.setRequestProperty("Content-Type", CONTENT_TYPE);

            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            countriesJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the api is not returning any data, there's no point in parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            Log.d(LOG_TAG,countriesJsonStr);
            getCountriesDataFromJson(countriesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the countries list.
        return null;
    }

}
