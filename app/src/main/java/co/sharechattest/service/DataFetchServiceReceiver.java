package co.sharechattest.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by Varun on 03/05/17.
 */

public class DataFetchServiceReceiver extends ResultReceiver {

    private Listener listener;

    public DataFetchServiceReceiver(Handler handler) {
        super(handler);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (listener != null)
            listener.onReceiveResult(resultCode, resultData);
    }


    public static interface Listener {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

}
