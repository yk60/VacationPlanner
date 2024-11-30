package edu.sjsu.android.vacationplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class GroupDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "groupsDatabase";
    private static final int VERSION = 1;

    // keys for group members table
    private static final String MEMBERS_TABLE_NAME = "members";
    private static final String ID = "_id";
    private static final String MEMBER_NAME = "memberName";
    private static final String MEMBER_PROFILE_PIC = "memProfilePicID";
    private static final String GROUP_ID = "groupID";// this connects to the host whose group with whom the member is associated with
    private static final String IS_HOST = "isHost"; // marks who is a host

    // create table for group members associated with each user
    static final String CREATE_MEMBERS_TABLE =
            String.format("CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT NOT NULL, " +
                    "%s INTEGER NOT NULL, " +
                    "%s INTEGER NOT NULL, " +
                    "%s INTEGER NOT NULL);", MEMBERS_TABLE_NAME, ID, MEMBER_NAME, MEMBER_PROFILE_PIC, GROUP_ID, IS_HOST);

    // keys for trip info table
    private static final String TRIP_TABLE_NAME = "trips";
    private static final String TRIP_ID = "_tripID";
    private static final String TRIP_NAME = "tripName";
    private static final String DESTINATION = "destination";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String BUDGET_GOAL = "budgetGoal";
    // create table for trip info
    static final String CREATE_TRIP_TABLE =
            String.format("CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s FLOAT, " +
                    "%s INTEGER NOT NULL);", TRIP_TABLE_NAME, TRIP_ID, TRIP_NAME, DESTINATION, START_DATE, END_DATE, BUDGET_GOAL, GROUP_ID);




    public GroupDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public GroupDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_MEMBERS_TABLE);
        sqLiteDatabase.execSQL(CREATE_TRIP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MEMBERS_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRIP_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long insert(ContentValues contentValues) {
        SQLiteDatabase database = getWritableDatabase();
        return database.insert(MEMBERS_TABLE_NAME, null, contentValues);
    }
    public long insertTripInfo(ContentValues contentValues) {
        SQLiteDatabase database = getWritableDatabase();
        return database.insert(TRIP_TABLE_NAME, null, contentValues);
    }

    public int update(ContentValues contentValue){
        SQLiteDatabase database = getWritableDatabase();
        return database.update(MEMBERS_TABLE_NAME, contentValue, null, null);
    }

    public int updateGroup(ContentValues contentValue, String s, String[] strings) {
        SQLiteDatabase database = getWritableDatabase();
        return database.update(MEMBERS_TABLE_NAME, contentValue, s, strings);
    }

    public int updateTrip(ContentValues contentValue, String s, String[] strings) {
        SQLiteDatabase database = getWritableDatabase();
        return database.update(TRIP_TABLE_NAME, contentValue, s, strings);
    }

    public Cursor getAllUsers(String orderBy) {
        SQLiteDatabase database = getWritableDatabase();
        return database.query(MEMBERS_TABLE_NAME,
                new String[]{ID, MEMBER_NAME, MEMBER_PROFILE_PIC, GROUP_ID, IS_HOST},
                null, null, null, null, orderBy);
    }

    public Cursor getAllTripInfo(String orderBy) {
        SQLiteDatabase database = getWritableDatabase();
        return database.query(TRIP_TABLE_NAME,
                new String[]{TRIP_ID, TRIP_NAME, DESTINATION, START_DATE, END_DATE, BUDGET_GOAL, GROUP_ID},
                null, null, null, null, orderBy);
    }

    public int deleteMember(String selection, String[] selectionArgs){
        SQLiteDatabase database = getWritableDatabase();
        return database.delete(MEMBERS_TABLE_NAME, selection, selectionArgs);
    }

}
