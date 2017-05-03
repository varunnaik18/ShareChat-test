package co.sharechattest.utils;

import android.app.DownloadManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import co.sharechattest.api.model.FetchData;
import co.sharechattest.app.ShareChatTestApp;
import co.sharechattest.db.DBHelper;

/**
 * Created by Varun on 03/05/17.
 */

public class FileDownloadThread extends Thread {

    public static final String TAG = FileDownloadThread.class.getSimpleName();

    private DownloadManager mDownloadManager = ShareChatTestApp.getInstance().getDownloadManager();

    HashMap<String, FetchData> map;

    @Override
    public void run() {
        super.run();

        ImageDownloadThread(map);
    }

    public void downloadImages(HashMap<String, FetchData> map) {

        this.map = new HashMap<>();

        this.map.putAll(map);

    }

    private void ImageDownloadThread(final HashMap<String, FetchData> map) {

        try {

            if (!ShareChatTestApp.isNetworkAvailable()) {
                return;
            }

            // progress update counter
            int i = 1;
            for (Map.Entry<String, FetchData> entry : map.entrySet()) {

                final String id = entry.getKey();
                final FetchData value = entry.getValue();

                if (Utility.isFilePresent(id)) {
                    i++;
                    continue;
                }

                Uri downloadUri = Uri.parse(value.getType().equalsIgnoreCase(Constants.ITEM_TYPE_PROFILE) ?
                        value.getProfileUrl() : value.getUrl());

                DownloadManager.Request downloadRequest = new DownloadManager.Request(downloadUri);

                //Set the local destination for the downloaded file to a path within the application's external files directory

                downloadRequest.setDestinationInExternalFilesDir(ShareChatTestApp.getInstance(), Environment.DIRECTORY_DOWNLOADS,
                        Utility.getImageSubPath(id));

                downloadRequest.allowScanningByMediaScanner();

                // keep the visibility of downloading notification hidden
                downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

                // do not show our downloaded files in download manager UI
                downloadRequest.setVisibleInDownloadsUi(false);

                //Enqueue a new download and save the referenceId
                long downloadRefId = mDownloadManager.enqueue(downloadRequest);

                boolean isDownloading = true;

                // reset success flag to
                //downloadSuccessful = false;

                while (isDownloading) {

                    if (!ShareChatTestApp.isNetworkAvailable()) {

                        isDownloading = false;
                        break;
                    }

                    // Query the manager for download progress
                    DownloadManager.Query myDownloadQuery = new DownloadManager.Query();

                    //set the query filter to our previously Enqueued download
                    myDownloadQuery.setFilterById(downloadRefId);

                    //Query the download manager about downloads that have been requested.
                    Cursor cursor = null;

                    try {
                        cursor = mDownloadManager.query(myDownloadQuery);

                        if (cursor != null && cursor.getCount() > 0) {

                            cursor.moveToFirst(); // move to first position of cursor

                            int status = (cursor.getColumnIndex(DownloadManager.COLUMN_STATUS) != -1) ?
                                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) : DownloadManager.STATUS_FAILED;

                            if (status == DownloadManager.STATUS_SUCCESSFUL) { // downloaded successful

                                isDownloading = false;

                                Log.d(TAG, "Image download completed id=" + id);

                                DBHelper.updateBookTagsString(id);


                            } else if (Utility.isDownloadError(status)) { // some error is there while downloading

                                // we are not showing download fail when a particular image download
                                // fails cause its a background process

                                isDownloading = false;
                            }

                        } else {

                            // if download id do not exits then break teh loop
                            isDownloading = false;
                        }
                    } catch (Exception e) {

                        // break the loop
                        isDownloading = false;

                    } finally {

                        // close cursor
                        CursorUtils.close(cursor);
                    }
                }

                i++;
            }

        } catch (Exception e) {

            Log.e("fileDowloadManager", "ImageDownload Thread Interrupted: " + e.getMessage());
        } finally {

        }
    }
}
