package co.sharechattest.db.model;

/**
 * Created by Varun on 02/05/17.
 */

public class TableTrendingFeed {

    public static final String TABLE_NAME = "trending_feeds";

    public static final String COLUMN_SNO = "s_no";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_AUTHOR_NAME = "author_name";
    public static final String COLUMN_AUTHOR_CONTACT = "author_contact";
    public static final String COLUMN_AUTHOR_DOB = "author_dob";
    public static final String COLUMN_AUTHOR_STATUS = "author_status";
    public static final String COLUMN_AUTHOR_GENDER = "author_gender";
    public static final String COLUMN_AUTHOR_AGE = "author_age";
    public static final String COLUMN_PROFILE_URL = "profile_url";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_POSTED_ON = "postedOn";
    public static final String COLUMN_LOCALLY_DOWNLOADED = "locally_downloaded";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_SNO + " INTEGER, "
            + COLUMN_ID + " VARCHAR PRIMARY KEY, "
            + COLUMN_TYPE + " VARCHAR, "
            + COLUMN_AUTHOR_NAME + " VARCHAR, "
            + COLUMN_AUTHOR_CONTACT + " VARCHAR, "
            + COLUMN_AUTHOR_DOB + " VARCHAR, "
            + COLUMN_AUTHOR_STATUS + " VARCHAR, "
            + COLUMN_AUTHOR_GENDER + " VARCHAR, "
            + COLUMN_AUTHOR_AGE + " VARCHAR, "
            + COLUMN_PROFILE_URL + " VARCHAR, "
            + COLUMN_URL + " VARCHAR, "
            + COLUMN_POSTED_ON + " VARCHAR, "
            + COLUMN_LOCALLY_DOWNLOADED + " INTEGER DEFAULT 0)";


}
