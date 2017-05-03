package co.sharechattest.utils;

import android.app.DownloadManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import co.sharechattest.app.ShareChatTestApp;
import okhttp3.ResponseBody;

/**
 * Created by Varun on 03/05/17.
 */

public class Utility {

    public static String convertDate(long postedOn) {

        String dateText = "";

        if (Check.isEmpty(postedOn))
            return "";

        try {
            Date date = new Date(postedOn);
            SimpleDateFormat df2 = new SimpleDateFormat("dd/mm/yy");
            dateText = df2.format(date);
            return dateText;

        } catch (Exception e) {
            return dateText;
        }
    }

    public static int getAge(String dob) {

        int totalAge = 0;

        if (Check.isEmpty(dob))
            return totalAge;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
            Date d = sdf.parse(dob);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            totalAge = (Calendar.getInstance().get(Calendar.YEAR)) - (cal.get(Calendar.YEAR));

            return totalAge;
        } catch (ParseException e) {
            e.printStackTrace();
            return totalAge;
        }

    }

    public static String getImagePath(String id) {

        String filePath;

//        switch (imageType) {
//
//            case Constants.ITEM_TYPE_IMAGE:
                filePath = ShareChatTestApp.getInstance().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath()
                        + File.separator + "images/" + id + ".jpg";
//                break;
//
//            default:
//                filePath = ShareChatTestApp.getInstance().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath()
//                        + File.separator + "images/profile/" + id + ".jpg";
//                break;
//        }

        return filePath;
    }

    public static boolean writeResponseBodyToDisk(ResponseBody body, String id) {
        try {
            File futureStudioIconFile = new File(getImagePath(id));

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("Utility", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Checks from default download manager error list
     * if this status code matches
     *
     * @param status
     * @return
     */
    public static boolean isDownloadError(int status) {

        if (Check.isEmpty(status))
            return true;

        return (status == DownloadManager.STATUS_FAILED || status == DownloadManager.ERROR_CANNOT_RESUME
                || status == DownloadManager.ERROR_TOO_MANY_REDIRECTS || status == DownloadManager.ERROR_UNKNOWN
                || status == DownloadManager.ERROR_FILE_ERROR || status == DownloadManager.ERROR_UNHANDLED_HTTP_CODE
                || status == DownloadManager.ERROR_DEVICE_NOT_FOUND || status == DownloadManager.ERROR_HTTP_DATA_ERROR);
    }

    public static Boolean isFilePresent(String id) {

        String filePath = "";

        filePath = ShareChatTestApp.getInstance().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath()
                + File.separator + "images/" + id + ".jpg";
        return isFileExist(filePath);
    }

    public static Boolean isFileExist(String finalPath) {

        if (Check.isEmpty(finalPath))
            return false;

        File file = new File(finalPath);
        return file.exists();
    }

    public static String getImageSubPath(String id) {

        String subPath = "";

        subPath = File.separator + "images/" + id + ".jpg";
        return subPath;
    }

}
