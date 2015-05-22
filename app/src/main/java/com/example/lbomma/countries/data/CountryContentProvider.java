package com.example.lbomma.countries.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by LBomma on 4/16/15.
 */
public class CountryContentProvider extends ContentProvider
{
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CountryDblHelper mOpenHelper;

    static final int COUNTRY = 100;
    static final int COUNTRY_WITH_NAME = 101;

    private static final String sCountryNameSelection =
            CountryContract.CountryEntry.TABLE_NAME+
                    "." + CountryContract.CountryEntry.COLUMN_NAME + " = ? ";

    private Cursor getCountryBySelectedName(Uri uri, String[] projection, String sortOrder)
    {
        String name = CountryContract.CountryEntry.getNameFromUri(uri);

        String[] selectionArgs;
        String selection;

            selection = sCountryNameSelection;
            selectionArgs = new String[]{name};

        return mOpenHelper.getReadableDatabase().query(
                CountryContract.CountryEntry.TABLE_NAME,
                projection,
                sCountryNameSelection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    /* creating uri matcher */
    static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CountryContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, CountryContract.PATH_COUNTRY, COUNTRY);
        matcher.addURI(authority, CountryContract.PATH_COUNTRY + "/*", COUNTRY_WITH_NAME);

        return matcher;
    }


    @Override
    public boolean onCreate()
    {
        mOpenHelper = new CountryDblHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri)
    {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case COUNTRY_WITH_NAME:
                return CountryContract.CountryEntry.CONTENT_ITEM_TYPE;
            case COUNTRY:
                return CountryContract.CountryEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder)
    {
        // Switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri))
        {
            //Selected country
            // "country/*/*"
            case COUNTRY_WITH_NAME:
            {
                retCursor = getCountryBySelectedName(uri, projection, sortOrder);
                break;
            }

            // "country"
            case COUNTRY:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CountryContract.CountryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match)
        {
            case COUNTRY:
            {
                long _id = db.insert(CountryContract.CountryEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CountryContract.CountryEntry.buildCountryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match)
        {
            case COUNTRY:
                rowsDeleted = db.delete(
                        CountryContract.CountryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }



    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match)
        {
            case COUNTRY:
                rowsUpdated = db.update(CountryContract.CountryEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case COUNTRY:
                db.beginTransaction();
                int returnCount = 0;
                try
                {
                    for (ContentValues value : values)
                    {
                        long _id = db.insert(CountryContract.CountryEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                        {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally
                {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing

    @Override
    @TargetApi(11)
    public void shutdown()
    {
        mOpenHelper.close();
        super.shutdown();
    }
}

