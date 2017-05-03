package co.sharechattest.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import co.sharechattest.BuildConfig;
import co.sharechattest.api.callback.ServiceCallback;
import co.sharechattest.api.client.ApiClient;
import co.sharechattest.api.model.FetchResponse;
import co.sharechattest.api.model.PostDataFetch;
import co.sharechattest.api.service.PostApiInterface;
import co.sharechattest.utils.Check;
import co.sharechattest.utils.Constants;
import retrofit2.Call;

/**
 * Created by Varun on 03/05/17.
 */

public class DataFetchService extends IntentService {

    public DataFetchService() {
        super("DataFetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final ResultReceiver rec = (ResultReceiver) intent.getParcelableExtra("rec");

        final int idOffset = intent.getExtras().getInt(Constants.BUNDLE_KEY_OFFSET, 0);

        if (Check.isEmpty(idOffset))
            return;

        PostDataFetch postDataFetch = new PostDataFetch();
        postDataFetch.setRequestId(BuildConfig.REQUEST_ID);
        postDataFetch.setIdOffset(idOffset);

        PostApiInterface postApiInterface = ApiClient.getInstance().getService(PostApiInterface.class);
        Call<FetchResponse> call = postApiInterface.fetchTrendingFeeds(postDataFetch);

        call.enqueue(new ServiceCallback<FetchResponse>() {
            @Override
            protected void onSuccess(FetchResponse response) {
                Bundle b = new Bundle();
                if (response.isSuccess()) {
                    b.putInt(Constants.BUNDLE_KEY_OFFSET, idOffset);
                    b.putSerializable("value", response);
                    rec.send(0, b);
                } else {
                    b.putSerializable("value", null);
                    b.putString(Constants.BUNDLE_KEY_MESSAGE, response.getError());
                    rec.send(1, b);
                }
            }

            @Override
            protected void onFailure(int code, String message, FetchResponse response) {

                Bundle b = new Bundle();
                b.putString(Constants.BUNDLE_KEY_MESSAGE, Check.isEmpty(response.getError()) ? message : response.getError());
                rec.send(1, b);
            }
        });
    }

}
