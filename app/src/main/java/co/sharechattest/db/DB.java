package co.sharechattest.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import co.sharechattest.app.ShareChatTestApp;
import co.sharechattest.db.model.TableTrendingFeed;

/**
 * Created by Varun on 02/05/17.
 */

public class DB extends SQLiteOpenHelper {

    private static final String TAG = DB.class.getSimpleName();

    Context context;
    private SQLiteDatabase mDatabase;

    public DB(Context context) {

        super(context, ShareChatTestApp.DATABASE_NAME, null, ShareChatTestApp.DATABASE_VERSION);
        //super(context, context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + DATABASE_NAME, null, 4);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        mDatabase = db;

        db.execSQL(TableTrendingFeed.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //TODO: Implement upgrade feature
    }

    /**
     * Content Provider Method variables
     */
    public static final String METHOD_EXECUTE_SQL = "execute_sql";
    public static final String METHOD_RAW_SQL = "raw_query";
    public static final String METHOD_CREATE_TABLE = "create_table";
    public static final String METHOD_GET_TABLE_NAMES = "get_tables";
    public static final String METHOD_GET_TABLE_COLUMNS = "get_table_columns";
    public static final String METHOD_TABLE_EXISTS = "get_table_exists";

    /**
     * Content Provider Result Value variables
     */
    public static final String VALUE_RESPONSE = "response"; // 0 or 1
    public static final String VALUE_ERROR = "error";
    //public static final String VALUE_RESULT = "result";

    /**
     * Android Default Tables - have to be ignored
     */
    public static final String TABLE_IGNORE_METADATA = "android_metadata";
    public static final String TABLE_IGNORE_SEQUENCE = "sqlite_sequence";

    // Hash Map key name
    public static final String KEY_NAME_PARCEL = "parcel";

}
