package edu.sjsu.android.vacationplanner;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class GroupProvider extends ContentProvider {
    private GroupDB database;

    private static UriMatcher uriMatcher;


    @Override
    public boolean onCreate() {
        database = new GroupDB(getContext());
        uriMatcher = buildUriMatcher();
        return true;
    }

    public static UriMatcher buildUriMatcher(){
        String AUTHORITY = "edu.sjsu.android.vacationplanner.GroupProvider";
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "members", 1); // to access group members table
        matcher.addURI(AUTHORITY, "trips", 2); // to access trips table
        return matcher;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // If not specified, sort by ID
        switch (uriMatcher.match(uri)) {
            case 1:
                sortOrder = sortOrder == null ? "_id" : sortOrder;
                return database.getAllUsers(sortOrder);
            case 2:
                sortOrder = sortOrder == null ? "_id" : sortOrder;
                return database.getAllTripInfo(sortOrder);
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long rowID = 0;
        //If record is added successfully
        switch (uriMatcher.match(uri)) {
            case 1:
                rowID = database.insert(contentValues);
                break;
            case 2:
                rowID = database.insertTripInfo(contentValues);
                break;
        }
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(uri, rowID);
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return database.deleteMember(s, strings);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        switch (uriMatcher.match(uri)) {
            case 1:
                return database.updateGroup(contentValues, s, strings);
            case 2:
                return database.updateTrip(contentValues, s, strings);
        }
        return 0;
    }
}
