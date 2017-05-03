package co.sharechattest.db;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import co.sharechattest.api.model.FetchData;
import co.sharechattest.app.ShareChatTestApp;
import co.sharechattest.db.model.TableTrendingFeed;
import co.sharechattest.utils.Constants;
import co.sharechattest.utils.CursorUtils;
import co.sharechattest.utils.Utility;

/**
 * Created by Varun on 02/05/17.
 */

public class DBHelper {

    public static final String TAG = DBHelper.class.getSimpleName();

    public static void addFeedsToTable(List<FetchData> fetchDataList, int offsetIndex, boolean callFromRefresh) {

        for (int i = 0 ; i < fetchDataList.size(); i++) {

            FetchData fetchData = fetchDataList.get(i);

            ContentValues values = new ContentValues();
            values.put(TableTrendingFeed.COLUMN_SNO, (i + offsetIndex));
            values.put(TableTrendingFeed.COLUMN_ID, fetchData.getId());
            values.put(TableTrendingFeed.COLUMN_TYPE, fetchData.getType());
            values.put(TableTrendingFeed.COLUMN_AUTHOR_NAME, fetchData.getAuthorName());
            values.put(TableTrendingFeed.COLUMN_AUTHOR_CONTACT, fetchData.getAuthorContact());
            values.put(TableTrendingFeed.COLUMN_AUTHOR_DOB, fetchData.getAuthorDob());
            values.put(TableTrendingFeed.COLUMN_AUTHOR_AGE, Utility.getAge(fetchData.getAuthorDob()));
            values.put(TableTrendingFeed.COLUMN_AUTHOR_STATUS, fetchData.getAuthorStatus());
            values.put(TableTrendingFeed.COLUMN_AUTHOR_GENDER, fetchData.getAuthorGender());
            values.put(TableTrendingFeed.COLUMN_PROFILE_URL, fetchData.getProfileUrl());
            values.put(TableTrendingFeed.COLUMN_URL, fetchData.getUrl());
            values.put(TableTrendingFeed.COLUMN_POSTED_ON, Utility.convertDate(fetchData.getPostedOn()));

            CPWrapper.insert(TableTrendingFeed.TABLE_NAME, values);

        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.LOCAL_BROADCAST_DATA_RECEIVED);
        broadcastIntent.putExtra(Constants.BUNDLE_KEY_OFFSET, offsetIndex);
        broadcastIntent.putExtra(Constants.BUNDLE_KEY_FROM_REFRESH, callFromRefresh);
        LocalBroadcastManager.getInstance(ShareChatTestApp.getInstance()).sendBroadcast(broadcastIntent);

    }

    // Get all feeds
    public static List<FetchData> getFeeds(int offset) {

        List<FetchData> fetchDataList = new ArrayList<>();
        Cursor cursor = null;
        try {

            cursor = CPWrapper.query(TableTrendingFeed.TABLE_NAME, null, null, null,
                    TableTrendingFeed.COLUMN_SNO + " ASC");

            if (cursor != null && cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    FetchData fetchData = new FetchData();
                    fetchData.setAuthorName(cursor.getString(cursor.getColumnIndex(TableTrendingFeed.COLUMN_AUTHOR_NAME)));
                    fetchData.setAuthorContact(cursor.getString(cursor.getColumnIndex(TableTrendingFeed.COLUMN_AUTHOR_CONTACT)));
                    fetchData.setAuthorDob(cursor.getString(cursor.getColumnIndex(TableTrendingFeed.COLUMN_AUTHOR_DOB)));
                    fetchData.setAuthorAge(cursor.getString(cursor.getColumnIndex(TableTrendingFeed.COLUMN_AUTHOR_AGE)));
                    fetchData.setAuthorGender(cursor.getString(cursor.getColumnIndex(TableTrendingFeed.COLUMN_AUTHOR_GENDER)));
                    fetchData.setAuthorStatus(cursor.getString(cursor.getColumnIndex(TableTrendingFeed.COLUMN_AUTHOR_STATUS)));
                    fetchData.setId(cursor.getString(cursor.getColumnIndex(TableTrendingFeed.COLUMN_ID)));
                    fetchData.setType(cursor.getString(cursor.getColumnIndex(TableTrendingFeed.COLUMN_TYPE)));
                    fetchData.setPostedDate(cursor.getString(cursor.getColumnIndex(TableTrendingFeed.COLUMN_POSTED_ON)));
                    fetchData.setUrl(cursor.getString(cursor.getColumnIndex(TableTrendingFeed.COLUMN_URL)));
                    fetchData.setProfileUrl(cursor.getString(cursor.getColumnIndex(TableTrendingFeed.COLUMN_PROFILE_URL)));
                    fetchData.setLocalImagePresent(cursor.getInt(cursor.getColumnIndex(TableTrendingFeed.COLUMN_LOCALLY_DOWNLOADED)) == 1);

                    fetchDataList.add(fetchData);
                }
            }

        } catch (Exception e) {

            Log.e(TAG, e.getLocalizedMessage());
        } finally {

            CursorUtils.close(cursor);
        }

        // return feeds list
        return fetchDataList;
    }

}
