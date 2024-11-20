package edu.sjsu.android.vacationplanner;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import androidx.annotation.Nullable;

public class DataProvider extends ContentProvider {
    private AppDB appDB;
    @Override
    public boolean onCreate() {
        appDB = new AppDB(getContext());
        return true;
    }

    @Override
    public int delete(@Nullable Uri uri, String selection, String[] selectionArgs) {
        return appDB.deleteAllData();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = appDB.insert(values);
        //If record is added successfully
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(uri, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(@Nullable Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return appDB.getAllLocations();
    }

    @Override
    public String getType(@Nullable Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}