package co.sharechattest.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import co.sharechattest.BuildConfig;

/**
 * Created by Varun on 02/05/17.
 */

public class ShareChatTestApp extends Application {

    /**
     * Database Variables hold:
     * 1. Database Name
     * 2. Database Version
     */
    public static final String DATABASE_NAME = "juggernautBooks.db";
    public static final int DATABASE_VERSION = BuildConfig.VERSION_CODE;

    // Global Instance, used by application
    private static ShareChatTestApp sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

    }

    /**
     * STATIC METHODS
     * <p/>
     * exposing global information like :
     * 1. Context
     * 2. App Resources
     * 3. Shared Preferences
     * 4. Connected to Internet
     */
    public static synchronized ShareChatTestApp getInstance() {
        return sInstance;
    }

    public static Resources getAppResources() {
        return getInstance().getResources();
    }

    public static synchronized boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) sInstance.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
