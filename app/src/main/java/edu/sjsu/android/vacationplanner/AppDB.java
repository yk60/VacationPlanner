package edu.sjsu.android.vacationplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class AppDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "placesDatabase";
    private static final String TABLE_NAME = "places";
    private static final String ID = "_id";
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String RATING = "rating";
    private static final String BUSINESS_HOUR = "business_hour";
    private static final String IS_SAVED = "is_saved";
    private static final String IMAGE = "image";
    private static final String COST = "cost";
    private static final String DATETIME = "datetime";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String TYPE = "type";

    private static AppDB instance;

    public static synchronized AppDB getInstance(Context context) {
        if (instance == null) {
            instance = new AppDB(context.getApplicationContext());
        }
        return instance;
    }

    static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s INTEGER, " +
                    "%s BLOB, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT);", TABLE_NAME, ID, NAME, ADDRESS, RATING, BUSINESS_HOUR, IS_SAVED, IMAGE, COST, DATETIME, START_TIME, END_TIME, TYPE);

    public AppDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        sqlDB.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlDB, int oldVersion, int newVersion) {
        sqlDB.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqlDB);
    }

    public long insert(ContentValues contentValues) {
        SQLiteDatabase database = getWritableDatabase();
        return database.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getAllPlaces() {
        SQLiteDatabase database = getWritableDatabase();
        return database.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public int delete(String selection, String[] selectionArgs) {
        SQLiteDatabase database = getWritableDatabase();
        return database.delete(TABLE_NAME, selection, selectionArgs);
    }

    public int deleteAllData(){
        SQLiteDatabase database = getWritableDatabase();
        return database.delete(TABLE_NAME, null, null);
    }
    public int update(ContentValues contentValue){
        return 0;
    }

    public int updatePlace(ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = getWritableDatabase();
        return database.update(TABLE_NAME, contentValues, selection, selectionArgs);
    }
}

