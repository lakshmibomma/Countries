package com.example.lbomma.countries.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class CountryDblHelper extends SQLiteOpenHelper {

    //Columns for country
    public static final String TABLE_COUNTRY_LIST = "countries_list";
    public static final String COLUMN_ID_MANAGE_COUNTRIES = "_id";
    public static final String COLUMN_COUNTRY_NAME = "name";
    public static final String COLUMN_REGION = "region";
    public static final String COLUMN_SUB_REGION = "subregion";
    public static final String COLUMN_POPULATION = "population";
    public static final String COLUMN_NATIONALITY = "nationality";
    public static final String COLUMN_CAPITAL = "capital";
    public static final String COLUMN_CURRENCY = "currency";
    public static final String COLUMN_AREA = "area";


    // Database creation sql statement
    private static final String CREATE_COUNTRY =
            "create table "
            + TABLE_COUNTRY_LIST + "(" + COLUMN_ID_MANAGE_COUNTRIES
            + " integer primary key autoincrement, "
            + COLUMN_COUNTRY_NAME + " text not null, "
            + COLUMN_REGION + " text not null, "
            + COLUMN_SUB_REGION + " text not null, "
            + COLUMN_POPULATION + " INTEGER DEFAULT 0, "
            + COLUMN_NATIONALITY + " text not null, "
            + COLUMN_CAPITAL + " text not null, "
            + COLUMN_CURRENCY + " text not null, "
            + COLUMN_AREA + " INTEGER DEFAULT 0); ";


    private static final String DATABASE_NAME = "world_counties.db";
    private static final int DATABASE_VERSION = 2;


    public CountryDblHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        try {
            database.execSQL(CREATE_COUNTRY);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(CountryDblHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRY_LIST);

        onCreate(db);
    }

}
