package co.sharechattest.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.sharechattest.app.ShareChatTestApp;


/**
 * Contains method which call JB Content Provider
 * Every class/function has to call these methods to retrieve data from Content Providers
 * Every method handles appropriate method parameters and returns appropriate data
 * <p/>
 *
 * Created by Varun on 02/05/17.
 */
public class CPWrapper {

    public static final String PROJECTION_PREFIX = "053";

    private static final String TAG = CPWrapper.class.getSimpleName();

    /**
     * Query Content Provider for a respective Table
     *
     * @param tableName     - name of table
     * @param projection    - DB column name array
     * @param selection     - WHERE clause of DB
     * @param selectionArgs - selection arguments
     * @param sortOrder     - result is sorted as per this column
     * @return
     * @throws Exception
     */
    public static Cursor query(String tableName, String[] projection, String selection,
                                String[] selectionArgs, String sortOrder) {

        Uri uri = Uri.parse(SCContentProvider.URL + "/" + tableName);

        return ShareChatTestApp.getInstance().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
    }

    public static Cursor queryDictionaryDB(Uri uri) {

        return ShareChatTestApp.getInstance().getContentResolver().query(uri, null, null, null, null);
    }

    // START - Content Provider call methods

    public static void createTables(String sql) {

        ShareChatTestApp.getInstance().getContentResolver().call(SCContentProvider.CONTENT_URI, DB.METHOD_EXECUTE_SQL,
                sql, null);
    }

    public static List<String> getTableColumns(String tableName) {

        List<String> tableColumn = new ArrayList<>();

        Bundle bundleResponse = ShareChatTestApp.getInstance().getContentResolver().call(Uri.parse(SCContentProvider.URL),
                DB.METHOD_GET_TABLE_COLUMNS, tableName, null);

        if (bundleResponse != null)
            tableColumn.addAll(Arrays.asList(bundleResponse.getString(DB.VALUE_RESPONSE).split(";")));

        return tableColumn;
    }

    public static String getCommaSeparatedColumns(String tableName) {

        String commaSeparatedColumns = "";

        Bundle response = ShareChatTestApp.getInstance().getContentResolver().call(Uri.parse(SCContentProvider.URL),
                DB.METHOD_GET_TABLE_COLUMNS, tableName, null);

        if (response != null) {
            String responseStr = response.getString(DB.VALUE_RESPONSE);

            if (responseStr != null && !responseStr.isEmpty()) {

                commaSeparatedColumns = responseStr.replace(";", ", ");
            }
        }

        return commaSeparatedColumns;
    }

    /**
     * Get List of table names from JB Database
     *
     * @return
     * @throws Exception
     */
    public static List<String> getTableNames() {

        List<String> tableList = new ArrayList<>();

        Bundle response = ShareChatTestApp.getInstance().getContentResolver().call(Uri.parse(SCContentProvider.URL),
                DB.METHOD_GET_TABLE_NAMES, null, null);

        if (response != null) {

            String responseStr = response.getString(DB.VALUE_RESPONSE);

            if (responseStr != null && !responseStr.isEmpty()) {

                tableList.addAll(Arrays.asList(responseStr.split(";")));
            }
        }

        return tableList;
    }

    /**
     * Check if particular table name exist in db
     *
     * @param tableName
     * @return
     */
    public static boolean tableExistsInDB(String tableName) {

        boolean tableExists = false;

        Bundle response = ShareChatTestApp.getInstance().getContentResolver().call(Uri.parse(SCContentProvider.URL),
                DB.METHOD_TABLE_EXISTS, tableName, null);

        if (response != null) {

            String responseStr = response.getString(DB.VALUE_RESPONSE);

            if (responseStr != null && !responseStr.isEmpty()) {

                tableExists = responseStr.equals(tableName);
            }
        }

        return tableExists;
    }

    // END - Content Provider call methods

    /**
     * Insert values in respective Table
     * Check if field is already present or not in table
     *
     * @param tableName
     * @param values
     * @return
     */
    public static Boolean insert(String tableName, ContentValues values) {

        if (values != null) {

            if (ShareChatTestApp.getInstance().getContentResolver().insert(
                    Uri.parse(SCContentProvider.URL + "/" + tableName), values) == null)
                return false;
            else
                return true;
        }

        return false;
    }

    public static boolean fieldAlreadyExists(String tableName, String column, String value) {

        boolean fieldExists = false;
        Cursor cursor = null;

        try {

            cursor = query(tableName, null, column + " = ?", new String[]{value}, null);

            if (cursor != null && cursor.getCount() > 0)
                fieldExists = true;

        } catch (Exception e) {
            Log.w(TAG, e);
        } finally {

            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return fieldExists;
    }

    /**
     * Delete rows in DB
     *
     * @param tableName     - db table on which query is to be applied
     * @param selection     - selection string to query db
     * @param selectionArgs - arguments for selection query
     * @return - boolean if something has been deleted in db
     */
    public static boolean delete(String tableName, String selection, String[] selectionArgs) {

        Uri uri = Uri.parse(SCContentProvider.URL + "/" + tableName);

        int count = ShareChatTestApp.getInstance().getContentResolver().delete(uri, selection, selectionArgs);

        return count > 0;
    }

    /**
     * Delete rows in DB for number of rows deleted
     *
     * @param tableName     - db table on which query is to be applied
     * @param selection     - selection string to query db
     * @param selectionArgs - arguments for selection query
     * @return - number of rows deleted
     */
    public static int deleteForCount(String tableName, String selection, String[] selectionArgs) {

        Uri uri = Uri.parse(SCContentProvider.URL + "/" + tableName);

        int count = ShareChatTestApp.getInstance().getContentResolver().delete(uri, selection, selectionArgs);

        return count;
    }

    /**
     * Calls Content Provider UPDATE Method to update the DB Table
     *
     * @param tableName     - name of DB table
     * @param values        - Content values
     * @param selection     - WHERE clause for table
     * @param selectionArgs - selection arguments
     * @return boolean value (true - if record has been updated)
     * @throws Exception
     */
    public static boolean update(String tableName, ContentValues values, String selection,
                                 String[] selectionArgs) {

        int response = 0;

        try {

            Uri uri = Uri.parse(SCContentProvider.URL + "/" + tableName);

            response = ShareChatTestApp.getInstance().getContentResolver()
                    .update(uri, values, selection, selectionArgs);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return (response > 0);
    }

    /**
     * Drop all the tables in the DB
     *
     * @throws Exception
     */
    public static void DropTables() {

        try {
            List<String> tableList = getTableNames();

            for (String table : tableList) {

                String sql = "DROP TABLE IF EXISTS " + table;
                ShareChatTestApp.getInstance().getContentResolver().call(SCContentProvider.CONTENT_URI, DB.METHOD_EXECUTE_SQL,
                        sql, null);
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }


    public static void EmptyTables() {

        try {
            List<String> tableList = getTableNames();

            for (String table : tableList) {

                String sql = "DELETE FROM " + table;
                ShareChatTestApp.getInstance().getContentResolver().call(SCContentProvider.CONTENT_URI, DB.METHOD_EXECUTE_SQL,
                        sql, null);
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    public static void EmptyTable(String tableName) {

        String sql = "DELETE FROM " + tableName;
        ShareChatTestApp.getInstance().getContentResolver().call(SCContentProvider.CONTENT_URI, DB.METHOD_EXECUTE_SQL,
                sql, null);
    }

    public static Bundle ExecuteRawQuery(String sql) throws Exception {

        return ShareChatTestApp.getInstance().getContentResolver().call(SCContentProvider.CONTENT_URI, DB.METHOD_RAW_SQL,
                sql, null);
    }

    /**
     * Convert Column List to String array
     *
     * @param columnList
     * @param projection
     * @param prefix
     * @param startIndex
     * @return
     */
    public static String[] generateProjectionArray(List<String> columnList, String[] projection, String prefix, int startIndex) {

        int index = startIndex;
        String[] projections = projection;

        for (String column : columnList) {

            String table_column = prefix + "." + column;
            projections[index] = table_column + ":" + table_column + " AS " + table_column.replace(".", PROJECTION_PREFIX);
            index++;
        }

        return projections;
    }
}
