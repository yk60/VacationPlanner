package edu.sjsu.android.vacationplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "eventsDatabase";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "events";
    private static final String ID = "_id";
    private static final String TITLE = "title";
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String TRIP_DATE = "tripDate";

    private static EventDB instance;

    public static synchronized EventDB getInstance(Context context) {
        if (instance == null) {
            instance = new EventDB(context.getApplicationContext());
        }
        return instance;
    }

    private EventDB(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = String.format("CREATE TABLE %s (" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL, " +
                "%s INTEGER NOT NULL);", TABLE_NAME, ID, TITLE, START_TIME, END_TIME, TRIP_DATE);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertEvent(ContentValues contentValues) {
        SQLiteDatabase database = getWritableDatabase();
        return database.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getAllEvents() {
        SQLiteDatabase database = getWritableDatabase();
        return database.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public int updateEvent(ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = getWritableDatabase();
        return database.update(TABLE_NAME, contentValues, selection, selectionArgs);
    }

    public int deleteEvent(String selection, String[] selectionArgs) {
        SQLiteDatabase database = getWritableDatabase();
        return database.delete(TABLE_NAME, selection, selectionArgs);
    }
}