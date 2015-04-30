package com.example.lbomma.countries.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by LBomma on 4/11/15.
 */
public class CountryContract {

    // Database fields

    public static final String CONTENT_AUTHORITY = "com.example.lbomma.countries.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_COUNTRY = "country";


    /* Inner class that defines the table contents of the country table */
    public static final class CountryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COUNTRY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COUNTRY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COUNTRY;

        public static final String TABLE_NAME = "country";

        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_NAME = "name";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_REGION = "region";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_CAPITAL = "capital";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_CURRENCY = "currency";
        public static final String COLUMN_NATIONALITY = "nationality";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_POPULATION = "population";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LANGITUDE = "langitude";


        public static Uri buildCountryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /*
            Student: This is the buildWeatherLocation function you filled in.
         */

        public static Uri buildSelectedCountry(String countryname) {
            return CONTENT_URI.buildUpon().appendPath(countryname).build();
        }

        public static Uri buildCountriesList() {
            return CONTENT_URI.buildUpon().build();
        }

        public static String getNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getCountryDetailsFromUri(Uri uri) {
           String countryDetails = uri.getQueryParameter(COLUMN_NAME);
            if (null != countryDetails)
                return countryDetails;
            else
                return "";
        }
    }
}
