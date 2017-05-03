package co.sharechattest.utils;

import android.os.Build;
import android.support.v4.content.ContextCompat;

import co.sharechattest.app.ShareChatTestApp;

/**
 * Created by Varun on 02/05/17.
 */

public class Check {

    @SuppressWarnings("deprecation")
    public static int getColor(int id) {

        final int version = Build.VERSION.SDK_INT;

        if (version >= 23)
            return ContextCompat.getColor(ShareChatTestApp.getInstance(), id);
        else
            return ShareChatTestApp.getAppResources().getColor(id);
    }


    /**
     * Checks if String is null or empty.
     *
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
        try {

            if (string == null || string.trim().length() == 0 || string.length() == 0 || string.isEmpty()
                    || string.equalsIgnoreCase("null")) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }

    /**
     * Checks if Integer is null or empty
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(Integer value) {
        try {

            if (value == null || value == -1 || value == 0) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }

    /**
     * Checks if long is null or empty
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(Long value) {
        try {

            if (value == null || value == -1 || value == 0) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }



}
