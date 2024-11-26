package edu.sjsu.android.vacationplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

// TODO: create a database for user (id, name, password, plannerID(foreign key to link two tables)
public class UserDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "usersDatabase";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "users";
    private static final String ID = "_id";
    private static final String NAME = "name";
    private static final String PASSWORD = "password";
    private static final String PROFILE_PIC = "profilePicID";
    private static final String GROUP_ID = "groupID"; // connect to info in group table
    private static final String HOST_ID = "hostID"; // 1 or 0; only the host will be able to edit group members, etc.

    // create table for all users
    static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s INTEGER NOT NULL, " +
                    "%s INTEGER NOT NULL, " +
                    "%s INTEGER NOT NULL);", TABLE_NAME, ID, NAME, PASSWORD, PROFILE_PIC, GROUP_ID, HOST_ID);


    public UserDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public UserDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldV, int newV) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long insert(ContentValues contentValues) {
        SQLiteDatabase database = getWritableDatabase();
        return database.insert(TABLE_NAME, null, contentValues);
    }


    public Cursor getAllUsers(String orderBy) {
        SQLiteDatabase database = getWritableDatabase();
        return database.query(TABLE_NAME,
                new String[]{ID, NAME, PASSWORD, PROFILE_PIC, GROUP_ID, HOST_ID},
                null, null, null, null, orderBy);
    }

    public int deleteAllUsers() {
        SQLiteDatabase database = getWritableDatabase();
        return database.delete(TABLE_NAME, null, null);
    }

    public int update(ContentValues contentValue){
        SQLiteDatabase database = getWritableDatabase();
        return database.update(TABLE_NAME, contentValue, null, null);
    }

    public int updateWithSpecifications(ContentValues values, String selection,
                                        String[] selectionArgs){
        SQLiteDatabase database = getWritableDatabase();
        return database.update(TABLE_NAME, values, selection, selectionArgs);
    }

}
