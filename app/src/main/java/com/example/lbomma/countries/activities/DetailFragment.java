package com.example.lbomma.countries.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.lbomma.countries.R;
import com.example.lbomma.countries.data.CountryContract;

import java.util.Locale;

/**
 * Created by LBomma on 5/4/15.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    static final String DETAIL_URI = "URI";
    // these constants values correspond to the projection defined above with respect to country columns,
    // and must change if the columns order changes
    static final int COL_COUNTRY_ID = 0;
    static final int COL_COUNTRY_NAME = 1;
    static final int COL_COUNTRY_REGION = 2;
    static final int COL_COUNTRY_CAPITAL = 3;
    static final int COL_COUNTRY_CURRENCY = 4;
    static final int COL_COUNTRY_POPULATION = 5;
    static final int COL_COUNTRY_NATIONALITY = 6;
    static final int COL_COUNTRY_LATITUDE = 7;
    static final int COL_COUNTRY_LONGITUDE = 8;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    private static final String COUNTRY_SHARE_HASHTAG = " #Countries";
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
    private ShareActionProvider mShareActionProvider;
    private String mcountryDetails;
    private Button mapButton;
    private Uri mUri;

    public DetailFragment()
    {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mapButton = (Button) rootView.findViewById(R.id.locate_on_map);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Bundle extras = getActivity().getIntent().getExtras();

                String latitude = extras.getString("latitude");
                String longitude = extras.getString("longitude");
                String countryName = extras.getString("countryName");
                
                String uri;
                if ( latitude == null || longitude == null)
                {
                    uri = String.format(Locale.ENGLISH, "geo:%s,%s?q=%s", latitude, longitude,countryName);
                }
                else
                {
                    uri = String.format(Locale.ENGLISH, "geo:%s,%s", latitude, longitude) + "?z=7";
                }
                Uri geoLocation = Uri.parse(uri).buildUpon()
                        .build();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);
                startActivity(intent);
            }
        });
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras == null)
        {
            mapButton.setEnabled(false);
        }


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mcountryDetails != null)
        {
            mShareActionProvider.setShareIntent(createShareCountryIntent());
        }
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
        if (mUri == null)
        {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                mUri,
                COUNTRY_COLUMNS,
                null,
                null,
                null
        );
    }

    private Intent createShareCountryIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mcountryDetails + COUNTRY_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        Log.v(LOG_TAG, "In onLoadFinished");

        if (data.moveToFirst())
        {
            getActivity().getIntent().putExtra("latitude", data.getString(COL_COUNTRY_LATITUDE));
            getActivity().getIntent().putExtra("longitude", data.getString(COL_COUNTRY_LONGITUDE));
            getActivity().getIntent().putExtra("countryName", data.getString(COL_COUNTRY_NAME));


            TextView countryTextView = (TextView) getView().findViewById(R.id.country_text);
            countryTextView.setText(data.getString(COL_COUNTRY_NAME));

            TextView regionTextView = (TextView) getView().findViewById(R.id.region_text);
            String region = data.getString(COL_COUNTRY_REGION);
            if (region != null && !region.isEmpty())
            {
                regionTextView.setText(region);
            }
            else
            {
                regionTextView.setText("Not Applicable");
            }

            TextView capitalTextView = (TextView) getView().findViewById(R.id.capital_text);
            String capital = data.getString(COL_COUNTRY_CAPITAL);
            if (capital != null && !capital.isEmpty())
            {
                capitalTextView.setText(capital);
            }
            else
            {
                capitalTextView.setText("Not Applicable");
            }

            TextView populationTextView = (TextView) getView().findViewById(R.id.population_text);

            String population = data.getString(COL_COUNTRY_POPULATION);
            if (population != null && !population.isEmpty() && !population.equals("null"))
            {
                populationTextView.setText(population);
            }
            else
            {
                populationTextView.setText("Not Applicable");
            }
            TextView currencyTextView = (TextView) getView().findViewById(R.id.currency_text);

            String currency = data.getString(COL_COUNTRY_CURRENCY);
            if (currency != null && !currency.isEmpty())
            {
                currencyTextView.setText(currency);
            }
            else
            {
                currencyTextView.setText("Not Applicable");
            }

            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null)
            {
                mapButton.setEnabled(true);
            }

            mcountryDetails = String.format("Country name: %s Region: %s Capital:%s Population: %s", countryTextView.getText(), regionTextView.getText(), capitalTextView.getText(), populationTextView.getText());


        }

       //shared provider
       if (mShareActionProvider != null)
       {
            mShareActionProvider.setShareIntent(createShareCountryIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }
}
