package com.example.lbomma.countries.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.lbomma.countries.models.Country;

import java.util.ArrayList;

/**
 * Created by LBomma on 4/11/15.
 */
public class CountryContract {

    // Database fields

    public static final String CONTENT_AUTHORITY = "com.example.lbomma.countries.activities";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ = "country";


    private SQLiteDatabase database;
    private CountryDblHelper dbHelper;


    private String[] allColumns =
            {CountryDblHelper.COLUMN_ID_MANAGE_COUNTRIES,
                    CountryDblHelper.COLUMN_COUNTRY_NAME,
                    CountryDblHelper.COLUMN_REGION,
                    CountryDblHelper.COLUMN_SUB_REGION,
                    CountryDblHelper.COLUMN_POPULATION,
                    CountryDblHelper.COLUMN_NATIONALITY,
                    CountryDblHelper.COLUMN_CAPITAL,
                    CountryDblHelper.COLUMN_CURRENCY,
                    CountryDblHelper.COLUMN_AREA
            };

    public CountryContract(Context context) {
        dbHelper = new CountryDblHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Country insertCountry(Country country) {

        long insertId = -1;

        ContentValues values = new ContentValues();
        values.put(CountryDblHelper.COLUMN_COUNTRY_NAME, country.getCountryName());
        values.put(CountryDblHelper.COLUMN_REGION, country.getRegion());
        values.put(CountryDblHelper.COLUMN_SUB_REGION, country.getSubregion());
        values.put(CountryDblHelper.COLUMN_POPULATION, country.getPopulation());
        values.put(CountryDblHelper.COLUMN_NATIONALITY, country.getNationality());
        values.put(CountryDblHelper.COLUMN_CAPITAL, country.getCapital());
        values.put(CountryDblHelper.COLUMN_CURRENCY, country.getCurrency());
        values.put(CountryDblHelper.COLUMN_AREA, country.getArea());

        try {
            insertId = database.insert(CountryDblHelper.TABLE_COUNTRY_LIST, null,
                    values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Cursor cursor = database.query(CountryDblHelper.TABLE_COUNTRY_LIST,
                allColumns, CountryDblHelper.COLUMN_ID_MANAGE_COUNTRIES + " = " + insertId, null,
                null, null, null);


        cursor.moveToFirst();

        Country t = cursorToTrip(cursor);
        cursor.close();
        return country;
    }


    public void updateCountry(Country country){
        long id = country.getId();

        ContentValues values = new ContentValues();
        values.put(CountryDblHelper.COLUMN_COUNTRY_NAME, country.getCountryName());
        values.put(CountryDblHelper.COLUMN_REGION, country.getRegion());
        values.put(CountryDblHelper.COLUMN_SUB_REGION, country.getSubregion());
        values.put(CountryDblHelper.COLUMN_POPULATION, country.getPopulation());
        values.put(CountryDblHelper.COLUMN_NATIONALITY, country.getNationality());
        values.put(CountryDblHelper.COLUMN_CAPITAL, country.getCapital());
        values.put(CountryDblHelper.COLUMN_CURRENCY, country.getCurrency());
        values.put(CountryDblHelper.COLUMN_AREA, country.getArea());


        String WHERE = CountryDblHelper.COLUMN_ID_MANAGE_COUNTRIES + "=" + id;

        long insertId = database.update(CountryDblHelper.TABLE_COUNTRY_LIST, values,
                WHERE, null);


    }

    public void deleteCountry(Country country) {
        long id = country.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(CountryDblHelper.TABLE_COUNTRY_LIST, CountryDblHelper.COLUMN_ID_MANAGE_COUNTRIES
                + " = " + id, null);
    }

    public ArrayList<Country> getAllCountries() {
        ArrayList<Country> countriesList = new ArrayList<Country>();

        Cursor cursor = database.query(CountryDblHelper.TABLE_COUNTRY_LIST,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Country trip = cursorToTrip(cursor);
            countriesList.add(trip);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return countriesList;
    }

    private Country cursorToTrip(Cursor cursor) {
        Country country = new Country();
        country.setId(cursor.getLong(0));
        country.setCountryName(cursor.getString(1));
        country.setRegion(cursor.getString(2));
        country.setSubregion(cursor.getString(3));
        country.setPopulation(cursor.getString(4));
        country.setNationality(cursor.getString(5));
        country.setCapital(cursor.getString(6));
        country.setCurrency(cursor.getString(7));
        country.setArea(cursor.getString(8));


        return country;
    }
}
