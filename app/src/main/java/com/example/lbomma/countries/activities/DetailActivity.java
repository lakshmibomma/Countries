package com.example.lbomma.countries.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.lbomma.countries.R;
import com.example.lbomma.countries.data.CountryContract;

import java.util.Locale;


public class DetailActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
    {
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final int DETAIL_LOADER = 0;
        private static final String[] COUNTRY_COLUMNS =
                {
                CountryContract.CountryEntry.TABLE_NAME + "." + CountryContract.CountryEntry._ID,
                CountryContract.CountryEntry.COLUMN_NAME,
                CountryContract.CountryEntry.COLUMN_REGION,
                CountryContract.CountryEntry.COLUMN_CAPITAL,
                CountryContract.CountryEntry.COLUMN_CURRENCY,
                CountryContract.CountryEntry.COLUMN_POPULATION,
                CountryContract.CountryEntry.COLUMN_NATIONALITY,
                CountryContract.CountryEntry.COLUMN_LATITUDE,
                CountryContract.CountryEntry.COLUMN_LONGITUDE,
                };

        // these constants values correspond to the projection defined above with respect to country columns,
        // and must change if the columns order changes
        static final int COL_COUNTRY_ID = 0;
        static final int COL_COUNTRY_NAME = 1;
        static final int COL_COUNTRY_REGION = 2;
        static final int COL_COUNTRY_CAPITAL = 3;
        static final int COL_COUNTRY_CURRENCY = 4;
        static final int COL_COUNTRY_NATIONALITY = 5;
        static final int COL_COUNTRY_POPULATION = 6;
        static final int COL_COUNTRY_LATITUDE = 7;
        static final int COL_COUNTRY_LONGITUDE = 8;

        public DetailFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // The detail Activity called via intent.  Inspect the intent for country data.

            Button mapButton = (Button) rootView.findViewById(R.id.locate_on_map);
            mapButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    //To do map location latitude and longitude fix
                    Bundle extras = getActivity().getIntent().getExtras();

                    String latitude = extras.getString("latitude");
                    String longitude = extras.getString("longitude");

                    System.out.println( "latitude  is  " + latitude) ;

                    String uri = String.format(Locale.ENGLISH, "geo:%s,%s", latitude, longitude);

                    Uri geoLocation = Uri.parse(uri).buildUpon()
                            .build();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(geoLocation);
                    startActivity(intent);
                }
            });

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args)
        {
            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    intent.getData(),
                    COUNTRY_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data)
        {
            Log.v(LOG_TAG, "In onLoadFinished");

            if (data.moveToFirst())
            {
                System.out.println( "country details are  " + data.getString(COL_COUNTRY_REGION)) ;
                System.out.println( "country details are  " + data.getString(COL_COUNTRY_NAME)) ;
                System.out.println( "country details are  " + data.getString(COL_COUNTRY_LATITUDE)) ;
                System.out.println( "country details are  " + data.getString(COL_COUNTRY_LONGITUDE)) ;
                TextView detailTextView = (TextView)getView().findViewById(R.id.detail_text);
                detailTextView.setText(data.getString(COL_COUNTRY_NAME));
                getActivity().getIntent().putExtra("latitude",data.getString(COL_COUNTRY_LATITUDE));
                getActivity().getIntent().putExtra("longitude",data.getString(COL_COUNTRY_LONGITUDE));
            }

//            String dateString = Utility.formatDate(
//                    data.getLong(COL_WEATHER_DATE));
//
//            String weatherDescription =
//                    data.getString(COL_WEATHER_DESC);
//
//            boolean isMetric = Utility.isMetric(getActivity());
//
//            String high = Utility.formatTemperature(
//                    data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
//
//            String low = Utility.formatTemperature(
//                    data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
//
//            mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);
//
//            TextView detailTextView = (TextView)getView().findViewById(R.id.detail_text);
//            detailTextView.setText(mForecast);

//            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
//            if (mShareActionProvider != null) {
//                mShareActionProvider.setShareIntent(createShareForecastIntent());
//            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader)
        {
        }
    }
}
