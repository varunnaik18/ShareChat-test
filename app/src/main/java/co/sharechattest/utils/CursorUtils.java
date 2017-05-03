package co.sharechattest.utils;

import android.database.Cursor;

/**
 * Created by Varun on 02/05/17.
 */
public class CursorUtils {

    public static void close(Cursor cursor) {

        if (cursor != null && !cursor.isClosed())
            cursor.close();
    }
}
