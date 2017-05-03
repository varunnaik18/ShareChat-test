package co.sharechattest.utils;

import android.widget.Toast;

import co.sharechattest.app.ShareChatTestApp;

/**
 * Created by Varun on 02/05/17.
 */

public class ToastUtils {

    public static void showToast(int stringId) {

        if (!Check.isEmpty(stringId))
            showToast(ShareChatTestApp.getAppResources().getString(stringId));
    }

    public static void showToast(final String message) {

        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!Check.isEmpty(message))
                    Toast.makeText(ShareChatTestApp.getInstance(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showToastLong(int string) {

        if (!Check.isEmpty(string))
            showToastLong(ShareChatTestApp.getAppResources().getString(string));
    }

    public static void showToastLong(final String message) {

        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!Check.isEmpty(message))
                    Toast.makeText(ShareChatTestApp.getInstance(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

}
