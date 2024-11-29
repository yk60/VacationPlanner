package edu.sjsu.android.vacationplanner;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

public class DataProvider extends ContentProvider {
    private AppDB appDB;

    public DataProvider(){

    }

    @Override
    public boolean onCreate() {
        appDB = new AppDB(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = appDB.insert(values);
        Log.d("DataProvider", "values: " + values);
        Log.d("DataProvider", "rowID: " + rowID);
        //If record is added successfully
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(uri, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
        String[] selectionArgs, String sortOrder) {

        return appDB.getAllPlaces();
    }

    @Override
    public String getType(@Nullable Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(@Nullable Uri uri, String selection, String[] selectionArgs) {
        return appDB.delete(selection, selectionArgs);

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return appDB.updatePlace(values, selection, selectionArgs);

    }
}