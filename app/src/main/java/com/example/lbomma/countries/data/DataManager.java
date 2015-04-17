package com.example.lbomma.countries.data;

import com.example.lbomma.countries.models.Country;

import java.util.ArrayList;

/**
 * Created by LBomma on 4/11/15.
 */
public class DataManager {

    private static DataManager instance;

    public Country getmCountry() {
        return mCountry;
    }

    public void setmCountry(Country mCountry) {
        this.mCountry = mCountry;
    }

    private Country mCountry;


    private ArrayList<Country> mCountriesList;


    private DataManager() {
    }

    public static DataManager getInstance() {

        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }



}
