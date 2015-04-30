package com.example.lbomma.countries.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.lbomma.countries.data.CountryContract.CountryEntry;



public class CountryDblHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "world_counties.db";
    private static final int DATABASE_VERSION = 2;


    public CountryDblHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        final String SQL_CREATE_COUNTRIES_TABLE = "CREATE TABLE " + CountryEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                CountryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                CountryEntry.COLUMN_NAME + " text not null, " +
                CountryEntry.COLUMN_REGION + " text not null, " +
                CountryEntry.COLUMN_CAPITAL + " text not null, " +
                CountryEntry.COLUMN_CURRENCY + " text not null," +
                CountryEntry.COLUMN_NATIONALITY + " text not null, " +
                CountryEntry.COLUMN_POPULATION + " text not null, " +
                CountryEntry.COLUMN_LATITUDE + " double, " +
                CountryEntry.COLUMN_LONGITUDE + " double);  ";


                database.execSQL(SQL_CREATE_COUNTRIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(CountryDblHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + CountryEntry.TABLE_NAME);

        onCreate(db);
    }

}
