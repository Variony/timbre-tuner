package com.example.fengwei7.timbretuner.DataBase;

/**
 * Created by fengwei7 on 2016-10-04.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.fengwei7.timbretuner.DataEntry.UserRecEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Kei-Ming on 2015-01-25.
 */
public class RecHandler extends SQLiteOpenHelper{

    // Makes a singleton Database for entire app
    private static RecHandler databaseHandler = null;
    private static Context mContext = null;
    private static String CURRENT_DB_PATH = null;
    private static String BACKUP_DB_PATH_DIR = null;
    // Database table
    public static final String TABLE_ENTRIES_REC = "table_rec"; // values is a keyword in sql :(
    public static final String COLUMN_USER = "username";
    public static final String COLUMN_TITLE_REC = "title";
    public static final String COLUMN_SCORE = "score";

    private static final String DATABASE_NAME = "rec.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_ENTRIES_REC
            + "("
            + COLUMN_USER + " text not null,"
            + COLUMN_TITLE_REC  + " text not null,"
            + COLUMN_SCORE + " text not null"
            + ");";

    private RecHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        databaseHandler = this;
    }

    public static RecHandler getHandler () {
        return databaseHandler;
    }

    public static RecHandler initHandler (Context context) {
        if (databaseHandler == null) {
            databaseHandler = new RecHandler(context);
        }
        BACKUP_DB_PATH_DIR = "/data/data/" + mContext.getPackageName() + "/databases/backup/";

        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        CURRENT_DB_PATH = db.getPath();


        List<UserRecEntry> example_rec_list = new ArrayList<UserRecEntry>();
        RecHandler recHandler = RecHandler.getHandler();



        db.close();

        return databaseHandler;
    }
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(RecHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES_REC);
        onCreate(db);
    }

    private void deleteDatabase () {
        mContext.deleteDatabase(DATABASE_NAME);
        databaseHandler = null;
        RecHandler.initHandler(mContext);
    }

    public static String getDBPathDir () {
        return BACKUP_DB_PATH_DIR;
    }
    // Export Database
    public void exportDatabase(String name) throws IOException {
        // Closes all database connections to commit to mem
        databaseHandler.close();
        // Determines paths
        String outFileName = BACKUP_DB_PATH_DIR + name + ".db";

        // Checks if destination folder exists and create if not
        File createOutFile = new File(BACKUP_DB_PATH_DIR);
        if (!createOutFile.exists()){
            createOutFile.mkdir();
        }

        File fromDB = new File(CURRENT_DB_PATH);
        File toDB = new File(outFileName);
        // Copy the database to the new location
        copyFile(new FileInputStream(fromDB), new FileOutputStream(toDB));
        Log.d("export Database", "copy succeeded");
        // Delete Existing Database
        //deleteDatabase();
    }

    // OverWrites existing Database with chosen one! --> will lose current DB info
    public void importDatabase (String name) throws IOException {
        // Closes all database connections to commit to mem
        databaseHandler.close();
        // Determines paths
        String inFileName = BACKUP_DB_PATH_DIR + name;

        File fromDB = new File(inFileName);
        File toDB = new File(CURRENT_DB_PATH);
        // Copy the database to the new location
        copyFile(new FileInputStream(fromDB), new FileOutputStream(toDB));
        Log.d("Import Database", "succeeded");
    }
    private static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }
    // Adding new value
    public void addValue(UserRecEntry recEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put (COLUMN_USER, recEntry.getUsername());
        values.put (COLUMN_TITLE_REC, recEntry.getTitle());
        values.put (COLUMN_SCORE, recEntry.getScore());

        // Inserting into database
        db.insert(TABLE_ENTRIES_REC, null, values);
        db.close();
    }

    // Getting single value
    public UserRecEntry getValue(String user,String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        UserRecEntry recEntry = null;
        // Uses a cursor to query from the database.
        // Provides the strings we want from the query and the query parameters
        Cursor cursor = db.query(TABLE_ENTRIES_REC, new String[] {
                        COLUMN_USER,
                        COLUMN_TITLE_REC,
                        COLUMN_SCORE
                }
                , COLUMN_USER + "=?"+" and "+ COLUMN_TITLE_REC+"=?", new String[] {
                        String.valueOf(user),String.valueOf(title)
                }
                , null, null, null,null);
        if (cursor.moveToFirst()) {

                recEntry = new UserRecEntry(
                        cursor.getString(0),                   // user
                        cursor.getString(1),                   // title
                        cursor.getInt(2)                       // score

                );
        }
        db.close();
        return recEntry;
    }

    public HashSet<String> getUsers() {
        HashSet<String> users = new HashSet<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ENTRIES_REC;

        SQLiteDatabase db = this.getWritableDatabase();
        // Uses a cursor to query from the database.
        // Provides the strings we want from the query and the query parameters
        Cursor cursor = db.query(TABLE_ENTRIES_REC, new String[] {
                        COLUMN_USER
                }
                , null, null, null, null, null,null);


        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String one_user = cursor.getString(0);                   // user
                // Adding contact to list
                if(!users.contains(one_user))
                users.add(one_user);
            } while (cursor.moveToNext());
        }

        // return contact list
        return users;
    }



  /*  public boolean checkuser(String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean output=false;
        // Uses a cursor to query from the database.
        // Provides the strings we want from the query and the query parameters
        Cursor cursor = db.query(TABLE_ENTRIES_REC, new String[] {
                        COLUMN_USER
                }
                , COLUMN_USER + "=?", new String[] {
                        String.valueOf(user)
                }
                , null, null, null,null);
        if () {
            output=true;
        }
        db.close();
        return output;
    }
*/
    // Getting All Values
    public List<UserRecEntry> getAllValues() {
        List<UserRecEntry> recEntryList = new ArrayList<UserRecEntry>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ENTRIES_REC;

        SQLiteDatabase db = this.getWritableDatabase();
        // Uses a cursor to query from the database.
        // Provides the strings we want from the query and the query parameters
        Cursor cursor = db.query(TABLE_ENTRIES_REC, new String[] {
                        COLUMN_USER,
                        COLUMN_TITLE_REC,
                        COLUMN_SCORE
                }
                , null, null, null, null, null,null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserRecEntry recEntry = new UserRecEntry(
                        cursor.getString(0),                   // user
                        cursor.getString(1),                   // title
                        cursor.getInt(2)                     // score
                );
                // Adding contact to list
                recEntryList.add(recEntry);
            } while (cursor.moveToNext());
        }

        // return contact list
        return recEntryList;
    }

    // Getting Values Count
    public int getValuesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ENTRIES_REC;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    // Updating single value
    public int updateValue(UserRecEntry recEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put (COLUMN_USER, recEntry.getUsername());
        values.put (COLUMN_TITLE_REC, recEntry.getTitle());
        values.put (COLUMN_SCORE, recEntry.getScore());

        // updating row
        return db.update(TABLE_ENTRIES_REC, values, COLUMN_TITLE_REC + " = ? and "+COLUMN_USER+" = ?",
                new String[] { String.valueOf(recEntry.getTitle()),String.valueOf(recEntry.getUsername()) });
    }

    // Deleting single value
    public void deleteUser(String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENTRIES_REC, COLUMN_USER + " = ?",
                new String[] { String.valueOf(user) });
        db.close();
    }

    // Deleting single value
    public void deleteTitle(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENTRIES_REC, COLUMN_TITLE_REC + " = ?",
                new String[] { String.valueOf(title) });
        db.close();
    }
}