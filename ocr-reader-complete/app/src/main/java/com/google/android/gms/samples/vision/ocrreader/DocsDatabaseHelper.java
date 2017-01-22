package com.google.android.gms.samples.vision.ocrreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ramyarao on 1/21/2017.
 */
public class DocsDatabaseHelper extends SQLiteOpenHelper {
    private static DocsDatabaseHelper sInstance;

    private static final String TAG = "DEBUG";
    // Database Info
    private static final String DATABASE_NAME = "docsDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_DOCS = "docs";

    // Docs Table Columns
    private static final String KEY_DOC_ID = "id";
    private static final String KEY_DOC_NAME = "docName";
    private static final String KEY_DOC_TEXT = "docText";

    private DocsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DocsDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DocsDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_DOCS_TABLE = "CREATE TABLE " + TABLE_DOCS +
                "(" +
                KEY_DOC_ID + " INTEGER PRIMARY KEY," +
                KEY_DOC_NAME + " TEXT," +
                KEY_DOC_TEXT + " TEXT" +
                ")";

        db.execSQL(CREATE_DOCS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCS);
            onCreate(db);
        }
    }





    // Insert or update a user in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // user already exists) optionally followed by an INSERT (in case the user does not already exist).
    // Unfortunately, there is a bug with the insertOnConflict method
    // (https://code.google.com/p/android/issues/detail?id=13045) so we need to fall back to the more
    // verbose option of querying for the user's primary key if we did an update.
    public long addOrUpdateDoc(Doc doc) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_DOC_NAME, doc.name);
            values.put(KEY_DOC_TEXT, doc.text);

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = db.update(TABLE_DOCS, values, KEY_DOC_NAME + "= ?", new String[]{doc.name});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_DOC_ID, TABLE_DOCS, KEY_DOC_NAME);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(doc.name)});
                try {
                    if (cursor.moveToFirst()) {
                        userId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                userId = db.insertOrThrow(TABLE_DOCS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d("hi:", "Error while trying to add or update doc");
        } finally {
            db.endTransaction();
        }
        return userId;
    }


    // Gets all videos in the database
    public ArrayList<Doc> getAllDocs() {
        ArrayList<Doc> docs = new ArrayList<>();

        // SELECT * FROM POSTS
        // LEFT OUTER JOIN USERS
        // ON POSTS.KEY_POST_USER_ID_FK = USERS.KEY_USER_ID
        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DOCS, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Doc doc = new Doc();
                    doc.name = cursor.getString(cursor.getColumnIndex(KEY_DOC_NAME));
                    doc.text = cursor.getString(cursor.getColumnIndex(KEY_DOC_TEXT));
                    docs.add(doc);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get videos from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return docs;
    }

    public void deleteAllDocs() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_DOCS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all docs");
        } finally {
            db.endTransaction();
        }
    }

    //Deletes video from the database, and deletes all tags associated with the video
    public boolean deleteDoc(String name) {
        try {
            //Open the database
            SQLiteDatabase database = this.getWritableDatabase();
            //Execute sql query to remove from database
            //NOTE: When removing by String in SQL, value must be enclosed with ''
            database.execSQL("DELETE FROM " + TABLE_DOCS + " WHERE " + KEY_DOC_NAME + "= '" + name + "'");
            //Close the database
            database.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }



//    public Set<Doc> getSearchResultsName(String query) {
//        Set<Doc> videoResults = new HashSet<Doc>();
//        Set<Doc> tags = new HashSet<Doc>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = null;
//        try {
//
//            //cursor = db.query(TABLE_VIDTAGS, new String[]{KEY_VIDTAG_ID, KEY_VIDTAG_VIDEO_ID_FK, KEY_VIDTAG_LABEL, KEY_VIDTAG_TIME}, KEY_VIDTAG_LABEL + "=?", new String[]{query.toLowerCase()}, null, null, null);
//            cursor = db.query(TABLE_DOCS, new String[]{KEY_DOC_ID,KEY_DOC_TEXT,},KEY_DOC_NAME + "=?",new String[]{query.toLowerCase()}, null, null, null);
//
//            if (cursor.moveToFirst()) {
//                do {
//                    int videoId = cursor.getInt(cursor.getColumnIndex(KEY_VIDEO_ID));
//
//                    Doc video = getVideo(videoId);
//                    if (!tags.contains(video.uri)) {
//                        videoResults.add(video);
//                        tags.add(video.uri);
//                    }
//                } while (cursor.moveToNext());
//            }
//            return videoResults;
//        } finally {
//            cursor.close();
//        }
//    }
}