package edu.sjsu.android.vacationplanner.group;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NoteDB";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "Note";
    private static final String COUNTER = "_counter";

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String DESC = "desc";
    private static final String COLOR = "color";
    private static final String GROUP_ID = "groupID"; // connects to the group where notes are seen
    private static final String DELETED = "deleted";

    static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s INTEGER, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s INTEGER, " +
                    "%s INTEGER, " +
                    "%s TEXT);", TABLE_NAME, COUNTER, ID, TITLE, DESC, COLOR, GROUP_ID, DELETED);

    public NoteDB(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long insert(ContentValues contentValues) {
        SQLiteDatabase database = getWritableDatabase();
        return database.insert(TABLE_NAME, null, contentValues);
    }

    public int update(ContentValues contentValue){
        SQLiteDatabase database = getWritableDatabase();
        return database.update(TABLE_NAME, contentValue, null, null);
    }

    public int updateNote(ContentValues contentValue, String s, String[] strings) {
        SQLiteDatabase database = getWritableDatabase();
        return database.update(TABLE_NAME, contentValue, s, strings);
    }

    public Cursor getAllNotes(String orderBy) {
        SQLiteDatabase database = getWritableDatabase();
        return database.query(TABLE_NAME,
                new String[]{COUNTER, ID, TITLE, DESC, COLOR, GROUP_ID, DELETED},
                null, null, null, null, orderBy);
    }

}
