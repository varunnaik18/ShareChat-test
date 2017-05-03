package co.sharechattest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import co.sharechattest.api.callback.ServiceCallback;
import co.sharechattest.api.client.ApiClient;
import co.sharechattest.api.model.FetchData;
import co.sharechattest.api.model.FetchResponse;
import co.sharechattest.api.model.PostDataFetch;
import co.sharechattest.api.service.PostApiInterface;
import co.sharechattest.app.ShareChatTestApp;
import co.sharechattest.db.DBHelper;
import co.sharechattest.service.DataFetchService;
import co.sharechattest.service.DataFetchServiceReceiver;
import co.sharechattest.utils.Check;
import co.sharechattest.utils.Constants;
import co.sharechattest.utils.CustomSwipeRefreshLayout;
import co.sharechattest.utils.FileDownloadThread;
import co.sharechattest.utils.ToastUtils;
import retrofit2.Call;

public class HomeActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        FeedsAdapter.FeedsHandler, DataFetchServiceReceiver.Listener {

    private static final int REQUEST_CODE_MULTIPLE_PERMISSIONS = 124;

    /**
     * Broadcast Receiver listening to Internet connection changes
     * <p>
     * Our handler for received Intents. This will be called whenever an Intent
     * with an action named  is broadcast.
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.getAction() != null) {

                switch (intent.getAction()) {

                    case Constants.LOCAL_BROADCAST_DATA_RECEIVED:

                        Bundle bundle = intent.getExtras();

                        if (bundle == null)
                            return;

                        int offset = bundle.getInt(Constants.BUNDLE_KEY_OFFSET, 1);
                        boolean callFromRefresh = bundle.getBoolean(Constants.BUNDLE_KEY_FROM_REFRESH, false);

                        loadFeedsFromDb(offset, callFromRefresh);

                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
    }

    private void init() {

        initRecyclerView();

        if (getSwipeToRefresh() != null) {
            getSwipeToRefresh().setColorSchemeColors(Check.getColor(R.color.colorPrimary),
                    Check.getColor(R.color.colorPrimary),
                    Check.getColor(R.color.colorPrimary));

            getSwipeToRefresh().setProgressViewOffset(false, 0, Math.round(getResources().getDimension(R.dimen.refresh_view_offset)));
            getSwipeToRefresh().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    if (!ShareChatTestApp.isNetworkAvailable())
                        ToastUtils.showToast(R.string.network_error);

//                    apiCallForTrendingFeeds(1, true);
                    startService(createCallingIntent(null));
                }
            });
        }

        // intent filter to check network connectivity change
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.LOCAL_BROADCAST_DATA_RECEIVED);

        LocalBroadcastManager.getInstance(HomeActivity.this).registerReceiver(mMessageReceiver, intentFilter);

        checkForPermission();

        if (DBHelper.isFeedsPresentInTable())
            loadFeedsFromDb(1, false);
        else
            apiCallForTrendingFeeds(1, false);
    }

    /**
     * Initialize all views related to recycler
     * Also includes its scroll listener and manager
     */
    private void initRecyclerView() {

        final LinearLayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);

        if (getRecycler() != null) {

            getRecycler().setHasFixedSize(true);
            getRecycler().setLayoutManager(layoutManager);

            RecyclerView.Adapter adapter = new FeedsAdapter(HomeActivity.this, getRecycler());
            getRecycler().setAdapter(adapter);

        }

        if (getSwipeToRefresh() != null)
            getSwipeToRefresh().setOnChildScrollUpListener(new CustomSwipeRefreshLayout.OnChildScrollUpListener() {
                @Override
                public boolean canChildScrollUp() {

                    //Custom implementation to override swipe refresh behaviour
                    return (layoutManager.findFirstVisibleItemPosition() > 0 ||
                            layoutManager.findViewByPosition(0) == null ||
                            layoutManager.findViewByPosition(0).getTop() < 0);
                }
            });
    }

    private void apiCallForTrendingFeeds(final int idOffset, final boolean callFromRefresh) {

        PostDataFetch postDataFetch = new PostDataFetch();
        postDataFetch.setRequestId(BuildConfig.REQUEST_ID);
        postDataFetch.setIdOffset(idOffset);

        PostApiInterface postApiInterface = ApiClient.getInstance().getService(PostApiInterface.class);
        Call<FetchResponse> call = postApiInterface.fetchTrendingFeeds(postDataFetch);

        call.enqueue(new ServiceCallback<FetchResponse>() {
            @Override
            protected void onSuccess(FetchResponse response) {

                if (response.isSuccess()) {

                    if (callFromRefresh)
                        DBHelper.deleteAllFromTableFeeds();

                    DBHelper.addFeedsToTable(response.getData(), idOffset, callFromRefresh);
                } else
                    ToastUtils.showToast(response.getError());

                stopSwipeToRefreshLayout();

            }

            @Override
            protected void onFailure(int code, String message, FetchResponse response) {
                if (response != null)
                    ToastUtils.showToast((response == null || Check.isEmpty(response.getError())) ?
                            (Check.isEmpty(message) ? "Something went wrong" : message) :
                            response.getError());

                stopSwipeToRefreshLayout();

            }
        });
    }

    private void loadFeedsFromDb(int offset, boolean callFromRefresh) {

        List<FetchData> fetchDataList = DBHelper.getFeeds(offset);

        getAdapter().updateList(fetchDataList, callFromRefresh);

        if (offset > 1)
            getAdapter().stopLoader();

        HashMap<String, FetchData> map = DBHelper.getImagesToBeDownloadedList();

        if (map == null || map.size() == 0)
            return;

        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) { // case when permission is not granted

            FileDownloadThread fileDownloadThread = new FileDownloadThread();
            fileDownloadThread.downloadImages(DBHelper.getImagesToBeDownloadedList());
            fileDownloadThread.start();
        }
    }

    private void stopSwipeToRefreshLayout() {

        if (getSwipeToRefresh() != null && getSwipeToRefresh().isRefreshing())
            getSwipeToRefresh().setRefreshing(false);
    }

    private void checkForPermission() {

        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) { // case when permission is not granted

            askPermission();
        }
    }

    public void askPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_MULTIPLE_PERMISSIONS);
    }

    private Intent createCallingIntent(Integer offsetId) {
        Intent i = new Intent(this, DataFetchService.class);
        DataFetchServiceReceiver receiver = new DataFetchServiceReceiver(new Handler());
        receiver.setListener(HomeActivity.this);
        i.putExtra("rec", receiver);
        i.putExtra(Constants.BUNDLE_KEY_OFFSET, offsetId);
        return i;
    }

    /**
     * Get variable to view
     * This way we avoid global variables
     */
    private CustomSwipeRefreshLayout getSwipeToRefresh() {

        return (CustomSwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    }

    private RecyclerView getRecycler() {

        return (RecyclerView) findViewById(R.id.recyclerView);
    }

    public FeedsAdapter getAdapter() {

        return (FeedsAdapter) getRecycler().getAdapter();

    }


    @Override
    public void onImagePostClick(FetchData fetchData) {


    }

    @Override
    public void onUserProfileClick(FetchData fetchData) {
        Intent intent = new Intent(ShareChatTestApp.getInstance(), ProfileDetailsActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_PROFILE_DATA, fetchData);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ShareChatTestApp.getInstance().startActivity(intent);
    }

    @Override
    public void onLoadMoreClick(int offset) {
        startService(createCallingIntent(offset));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_CODE_MULTIPLE_PERMISSIONS
                && grantResults.length > 0) {

            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    // user has denied permission
                    // or he has been doing this from last multiple times

                    ToastUtils.showToastLong(R.string.error_download_permission_denied);

                } else {
                    //Never ask again selected, or device policy prohibits the app from having that permission.
                    //So, disable that feature, or fall back to another situation...

                    ToastUtils.showToastLong(R.string.error_download_permission_never_ask);

                }
            }
        }

    }

    // Callback method for intentService
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {

            case 0:

                FetchResponse fetchResponse = (FetchResponse) resultData.getSerializable("value");
                int idOffset = resultData.getInt(Constants.BUNDLE_KEY_OFFSET);

                DBHelper.addFeedsToTable(fetchResponse.getData(), idOffset, false);

                stopSwipeToRefreshLayout();
                break;

            case 1:

                String message = resultData.getString(Constants.BUNDLE_KEY_MESSAGE);
                ToastUtils.showToast(message);

                getAdapter().stopLoader();

                stopSwipeToRefreshLayout();
                break;
        }
    }

    @Override
    protected void onDestroy() {

        if (mMessageReceiver != null)
            LocalBroadcastManager.getInstance(HomeActivity.this).unregisterReceiver(mMessageReceiver);

        super.onDestroy();
    }
}
