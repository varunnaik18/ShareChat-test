package co.sharechattest.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.Set;

import co.sharechattest.db.bean.HashMapParcel;

/**
 * Created by Varun on 02/05/17.
 */

public class SCContentProvider extends ContentProvider {

    private static final String TAG = SCContentProvider.class.getSimpleName();

    /**
     * Content Provider variables
     */
    public static final String AUTHORITY = "co.sharechattest.db.SCContentProvider";
    public static final String URL = "content://" + AUTHORITY + "";

    // Final Content Provider URI
    public static final Uri CONTENT_URI = Uri.parse(URL);

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        return getResults(uri, projection, selection, selectionArgs, sortOrder);
    }

    private Cursor getResults(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor = null;
        String[] fieldsProjection = projection;

        try {
            String tableName = getTableName(uri);

            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(tableName);

            // Sql query for join always contain one white space before and after
            if (tableName.contains(" JOIN ")) {
                qb.setProjectionMap(projectionForJoin(projection));
                fieldsProjection = null;
            } else {
                //qb.setProjectionMap(mValues);
            }

            cursor = qb.query(mDB, fieldsProjection, selection, selectionArgs, null,
                    null, sortOrder);

            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        } catch (Exception e) {

            Log.w(TAG, e);
        }

        return cursor;
    }

    /**
     * Generate Projection Map of the array for all Join Queries
     *
     * @param projections
     * @return
     */
    private HashMap<String, String> projectionForJoin(String[] projections) {

        HashMap<String, String> columnMap = new HashMap<>();

        for (int i = 0; i < projections.length; i++) {

            try {
                if (projections[i].contains(":")) {
                    String[] result = projections[i].split(":");
                    columnMap.put(result[0], result[1]);
                } else { // when field name has to be kept same
                    columnMap.put(projections[i], projections[i]);
                }
            } catch (Exception e) { // in case null or empty array occurs
                continue;
            }
        }

        return columnMap;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        long rowID = 0;
        /*int uriType = sURIMatcher.match(uri);

        if (uriType == URI_TYPE_SHELF_BOOK) {

            rowID = mDB.insert(TableMyShelf.TABLE_NAME, null, values);

            if (rowID > 0) {

                Uri recordUri = Uri.withAppendedPath(Uri.parse(URL + "/" + TableMyShelf.TABLE_NAME), String.valueOf(rowID));

                notifyChanges(recordUri);

                return recordUri;

            } else
                return null;
        }*/

        String tableName = getTableName(uri);

        try {
            rowID = mDB.insert(tableName, "", values);
        } catch (Exception e) {

            Log.w(TAG, e);
        }

        if (rowID > 0) {

            Uri recordUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            notifyChanges(recordUri);

            return recordUri;
        } else {

            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        String tableName = getTableName(uri);
        int count = 0;

        try {
            count = mDB.delete(tableName, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);

        } catch (Exception e) {
            Log.w(TAG, e);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        String tableName = getTableName(uri);

        int rowsAffected = mDB.update(tableName, values, selection, selectionArgs);

        if (rowsAffected <= 0)
            Log.w(TAG, new Exception(tableName + " not updated"));

        return rowsAffected;
    }

    @Nullable
    @Override
    public Bundle call(String method, String argument, Bundle extras) {

        Bundle bundle = new Bundle();
        StringBuilder responseStr = new StringBuilder();
        Cursor cursor = null;

        switch (method) {
            case DB.METHOD_RAW_SQL:

                try {
                    cursor = mDB.rawQuery(argument, null);

                    if (cursor != null) {

                        HashMapParcel parcel = new HashMapParcel();
                        parcel.setCursor(cursor);

                        bundle.putParcelable(DB.KEY_NAME_PARCEL, parcel);
                    }
                } catch (Exception e) {
                    Log.w(TAG, e);
                }

                break;

            case DB.METHOD_EXECUTE_SQL:

                String sql = argument;

                try {

                    mDB.execSQL(sql);
                    bundle.putString(DB.VALUE_RESPONSE, "1");
                } catch (Exception e) {

                    bundle.putString(DB.VALUE_RESPONSE, "0");
                    bundle.putString(DB.VALUE_ERROR, e.getMessage());
                }

                break;

            case DB.METHOD_CREATE_TABLE:

                // get bundle keys first
                Set<String> keys = extras.keySet();

                StringBuilder columnStr = new StringBuilder();

                for (String key : keys) {
                    columnStr.append(key);
                    columnStr.append(" ");
                    columnStr.append(extras.getString(key));

                    columnStr.append(", ");
                }

                // remove ',' placed last in table string
                String sqlColumnString = (String) columnStr.subSequence(0, columnStr.length() - 2);

                String createTable = "CREATE TABLE IF NOT EXISTS " + argument + " (" + sqlColumnString + ");";

                try {
                    mDB.execSQL(createTable);
                    bundle.putString(DB.VALUE_RESPONSE, "1: " + createTable);
                } catch (Exception e) {
                    bundle.putString(DB.VALUE_RESPONSE, "0: " + createTable);
                    bundle.putString(DB.VALUE_ERROR, e.getMessage());
                }

                break;

            case DB.METHOD_GET_TABLE_NAMES:

                try {
                    cursor = mDB.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
                    responseStr.setLength(0);

                    if (cursor != null && cursor.getCount() > 0) {

                        while (cursor.moveToNext()) {

                            // check for android default tables & remove them from list
                            if (!cursor.getString(0).equals("") &&
                                    !cursor.getString(0).equals(DB.TABLE_IGNORE_METADATA) &&
                                    !cursor.getString(0).equals(DB.TABLE_IGNORE_SEQUENCE)) {

                                responseStr.append(cursor.getString(0));
                                responseStr.append(";");
                            }
                        }
                    }
                } catch (Exception e) {
                    // do nothing
                } finally {

                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                }

                bundle.putString(DB.VALUE_RESPONSE, responseStr.toString());

                break;

            case DB.METHOD_GET_TABLE_COLUMNS:

                String sql_query = "PRAGMA table_info(" + argument + ");";

                try {

                    cursor = mDB.rawQuery(sql_query, null);
                    responseStr.setLength(0);

                    if (cursor != null && cursor.getCount() > 0) {

                        while (cursor.moveToNext()) {
                            responseStr.append(cursor.getString(1));
                            responseStr.append(";");
                        }
                    }

                    bundle.putString(DB.VALUE_RESPONSE, responseStr.toString());
                } catch (Exception e) {
                    // do nothing
                } finally {

                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                }

                break;

            case DB.METHOD_TABLE_EXISTS:

                sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + argument + "'";

                try {
                    cursor = mDB.rawQuery(sql, null);

                    if (cursor != null && cursor.getCount() > 0) {

                        cursor.moveToNext(); // move to first position

                        if (cursor.getColumnIndex("name") != -1)
                            responseStr.append(cursor.getString(cursor.getColumnIndex("name")));

                        bundle.putString(DB.VALUE_RESPONSE, responseStr.toString());
                    }
                } catch (Exception e) {
                    // do nothing
                } finally {

                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                }

                break;
        }

        return bundle;
    }

    private String getTableName(@NonNull Uri uri) {

        String value = uri.getPath();
        value = value.replace("/", ""); // we need to remove '/'

        return value;
    }

    @Override
    public boolean onCreate() {

        Context context = getContext();
        DB db = new DB(context);

        mDB = db.getWritableDatabase();

        if (mDB != null) {
            return true;
        }

        return false;
    }

    private void notifyChanges(Uri uri) {
        if (getContext() != null && getContext().getContentResolver() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }

    private SQLiteDatabase mDB;

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
