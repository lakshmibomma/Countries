package com.example.lbomma.countries.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.lbomma.countries.R;
import com.example.lbomma.countries.models.Country;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by LBomma on 4/9/15.
 */
public class CountiresFragment extends Fragment {

    private ArrayAdapter<String> mCountriestAdapter;
    private ArrayList<Country> countriesList;

    public CountiresFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.countriesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateCountriesList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mCountriestAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_country, // The name of the layout ID.
                        R.id.list_item_country_textview, // The ID of the textview to populate.
                        new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_countries);
        listView.setAdapter(mCountriestAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //To do how to pass object through extras
                String countryDetails = mCountriestAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                .putExtra(Intent.EXTRA_TEXT, countryDetails);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateCountriesList() {
        FetchCountriesTask countryTask = new FetchCountriesTask();
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String location = prefs.getString(getString(R.string.pref_location_key),
//                getString(R.string.pref_location_default));
        countryTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateCountriesList();
    }

    public class FetchCountriesTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchCountriesTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getCountriesDataFromJson(String countriesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.

            JSONArray jr = new JSONArray(countriesJsonStr);
            Integer count = jr.length();
            String[] resultStrs = new String[count];

            countriesList = new ArrayList<Country>();

            for(int i=0;i<jr.length();i++)
            {
                JSONObject jb = (JSONObject)jr.getJSONObject(i);
                Country country = new Country();
                country.setCountryName(jb.getString("name"));
                country.setRegion(jb.getString("region"));
                country.setCapital(jb.getString("capital"));
                country.setCurrency(jb.getString("currency"));
                country.setNationality(jb.getString("nationality"));
                country.setPopulation(jb.getString("population"));
                countriesList.add(country);

                // To do check null condition for lat,lang
//                JSONArray latLong = jb.getJSONArray("latlng");
//                for(int j=0;j<latLong.length();j++)
//                {
//                    Double latLangJsonObj = (Double)latLong.getDouble(j);
//                    System.out.println( "latLangJsonObj are" + latLangJsonObj) ;
//
//                }

//                country.setLangitude((JSONObject)latLong.get(""));

                String message = "Country : " + jb.getString("name") + " , " +
                        "Region : " + jb.getString("region");
                resultStrs[i] =  message;
                //System.out.println( "country details are" + message) ;
            }

            return resultStrs;

        }
        @Override
        protected String[] doInBackground(String... params) {

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
                return getCountriesDataFromJson(countriesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the countries list.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mCountriestAdapter.clear();
                for(String dayForecastStr : result) {
                    mCountriestAdapter.add(dayForecastStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}
