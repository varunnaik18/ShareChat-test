package co.sharechattest.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Varun on 02/05/17.
 */

public class ThreadUtils {

    private static final String TAG = ThreadUtils.class.getSimpleName();

    private ThreadUtils() {
    }

    public static void runOnUiThread(final Runnable runnable) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            new Handler(Looper.getMainLooper()).post(runnable);
        } else {
            runnable.run();
        }
    }

}
