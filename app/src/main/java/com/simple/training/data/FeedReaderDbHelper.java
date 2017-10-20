package com.simple.training.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.simple.training.data.FeedReaderContract.FeedEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FRAMGIA\nguyen.thanh.tuan on 10/20/17.
 */

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    private SQLiteDatabase mDatabase;

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + FeedEntry.TABLE_NAME
            + " ("
            + FeedEntry._ID
            + " INTEGER PRIMARY KEY,"
            + FeedEntry.COLUMN_NAME_TITLE
            + " TEXT,"
            + FeedEntry.COLUMN_NAME_SUBTITLE
            + " TEXT)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDatabase = getWritableDatabase();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insertData(String title, String subtitle) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);

        // Insert the new row, returning the primary key value of the new row
        return mDatabase.insert(FeedEntry.TABLE_NAME, null, values);
    }

    public List<Long> getData() {
        SQLiteDatabase db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                FeedEntry._ID, FeedEntry.COLUMN_NAME_TITLE, FeedEntry.COLUMN_NAME_SUBTITLE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = FeedEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { "Title 2" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";

        Cursor cursor = db.query(FeedEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        List<Long> itemIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry._ID));
            itemIds.add(itemId);
        }
        cursor.close();

        return itemIds;
    }

    public int deleteData() {
        SQLiteDatabase db = getReadableDatabase();
        // Define 'where' part of query.
        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { "Title 2" };
        // Issue SQL statement.
        return db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);
    }

    public int deleteAllData() {
        SQLiteDatabase db = getReadableDatabase();
        // Define 'where' part of query.
        String selection = "1";
        // Issue SQL statement.
        return db.delete(FeedEntry.TABLE_NAME, selection, null);
    }

    public int updateData(String title) {
        SQLiteDatabase db = getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);

        // Which row to update, based on the title
        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = { "Title 2" };

        return db.update(FeedEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public boolean deleteMyDatabase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);
    }
}
