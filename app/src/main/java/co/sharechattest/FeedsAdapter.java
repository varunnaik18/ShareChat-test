package co.sharechattest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.sharechattest.api.model.FetchData;
import co.sharechattest.app.ShareChatTestApp;
import co.sharechattest.utils.Check;
import co.sharechattest.utils.ThreadUtils;
import co.sharechattest.utils.Utility;
import co.sharechattest.viewholders.ImagePostHolder;
import co.sharechattest.viewholders.LoadMoreHolder;
import co.sharechattest.viewholders.ProgressHolder;
import co.sharechattest.viewholders.UserProfileHolder;

/**
 * Created by Varun on 02/05/17.
 */

public class FeedsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = FeedsAdapter.class.getSimpleName();

    private List<FetchData> list;
    private Context context;

    private static final int VIEW_TYPE_USER_PROFILE = 0;
    private static final int VIEW_TYPE_IMAGE_POST = 1;
    private static final int VIEW_TYPE_LOAD_MORE = 2;
    private static final int VIEW_TYPE_PROGRESS = 3;

    private boolean loadMoreCall = false;

    private RecyclerView recyclerView;

    public interface FeedsHandler {

        void onImagePostClick(FetchData fetchData);

        void onUserProfileClick(FetchData fetchData);

        void onLoadMoreClick(int offsetId);
    }

    private FeedsHandler mFeedsHandler;

    public FeedsAdapter(Context context, RecyclerView recyclerView) {
        super();

        this.context = context;
        this.list = new ArrayList<>();

        this.recyclerView = recyclerView;

        try {
            this.mFeedsHandler = ((FeedsHandler) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {

            case VIEW_TYPE_USER_PROFILE:

                View userProfileView = inflater.inflate(R.layout.item_user_profile, viewGroup, false);
                viewHolder = new UserProfileHolder(userProfileView);

                break;

            case VIEW_TYPE_IMAGE_POST:

                View imagePostView = inflater.inflate(R.layout.item_image_post, viewGroup, false);
                viewHolder = new ImagePostHolder(imagePostView);

                break;

            case VIEW_TYPE_LOAD_MORE:

                View loadMoreView = inflater.inflate(R.layout.item_load_more, viewGroup, false);
                viewHolder = new LoadMoreHolder(loadMoreView);

                break;

            case VIEW_TYPE_PROGRESS:

                View progressView = inflater.inflate(R.layout.item_progress, viewGroup, false);
                viewHolder = new ProgressHolder(progressView);

                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {

            case VIEW_TYPE_PROGRESS:


                break;

            case VIEW_TYPE_USER_PROFILE:

                UserProfileHolder userProfileHolder = (UserProfileHolder) viewHolder;

                configureUserProfileView(userProfileHolder, list.get(position));

                break;

            case VIEW_TYPE_LOAD_MORE:

                LoadMoreHolder loadMoreHolder = (LoadMoreHolder) viewHolder;

                configureLoadMoreView(loadMoreHolder);

                break;

            case VIEW_TYPE_IMAGE_POST:

                ImagePostHolder imagePostHolder = (ImagePostHolder) viewHolder;

                configureImagePostView(imagePostHolder, list.get(position));

                break;

        }

    }

    public void updateList(List<FetchData> dataList, boolean callFromRefresh) {

        if (dataList == null)
            return;

        if (list == null)
            list = new ArrayList<>();

        if (list.contains(null))
            list.remove(null);

        if (callFromRefresh)
            list.clear();

        list.addAll(dataList);

        // notify whole adapter about new data inserted
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() + 1 : 0);
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {

        if (isPositionFooter(position)) {
            return VIEW_TYPE_LOAD_MORE;
        }

        switch (list.get(position).getType()) {

            case "profile":
                return VIEW_TYPE_USER_PROFILE;

            case "image":
                return VIEW_TYPE_IMAGE_POST;

            default:
                return VIEW_TYPE_PROGRESS;
        }

    }

    private boolean isPositionFooter(int position) {
        return position >= list.size();
    }

    private void configureUserProfileView(UserProfileHolder holder, final FetchData fetchData) {

        if (fetchData == null)
            return;

        holder.tvName.setText(Check.isEmpty(fetchData.getAuthorName()) ? "" : fetchData.getAuthorName());
        holder.tvAge.setText(Check.isEmpty(fetchData.getAuthorAge()) ? "" : fetchData.getAuthorAge());
        holder.tvDob.setText(Check.isEmpty(fetchData.getAuthorDob()) ? "" : fetchData.getAuthorDob());
        holder.tvGender.setText(Check.isEmpty(fetchData.getAuthorGender()) ? "" : fetchData.getAuthorGender());
        holder.tvStatus.setText(Check.isEmpty(fetchData.getAuthorStatus()) ? "" : fetchData.getAuthorStatus());

        holder.llUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFeedsHandler != null)
                    mFeedsHandler.onUserProfileClick(fetchData);
            }
        });

        if (fetchData.isLocalImagePresent()) {
            Picasso.with(context)
                    .load(new File(Utility.getImagePath(fetchData.getId())))
                    .resize((int) ShareChatTestApp.getAppResources().getDimension(R.dimen.profile_image_width),
                            (int) ShareChatTestApp.getAppResources().getDimension(R.dimen.profile_image_width))
                    .centerCrop()
                    .into(holder.ivProfileImage);
        } else {

            if (!Check.isEmpty(fetchData.getProfileUrl()))
                Picasso.with(context)
                        .load(fetchData.getProfileUrl())
                        .resize((int) ShareChatTestApp.getAppResources().getDimension(R.dimen.profile_image_width),
                                (int) ShareChatTestApp.getAppResources().getDimension(R.dimen.profile_image_width))
                        .centerCrop()
                        .into(holder.ivProfileImage);
        }

    }

    private void configureImagePostView(ImagePostHolder holder, FetchData fetchData) {

        if (fetchData == null)
            return;

        holder.tvAuthorName.setText(Check.isEmpty(fetchData.getAuthorName()) ? "" : fetchData.getAuthorName());
        holder.tvDob.setText(Check.isEmpty(fetchData.getPostedOn()) ? "" : "" + fetchData.getPostedOn());

        if (fetchData.isLocalImagePresent()) {
            Picasso.with(context)
                    .load(new File(Utility.getImagePath(fetchData.getId())))
                    .into(holder.ivImagePost);
        } else {

            if (!Check.isEmpty(fetchData.getUrl()))
                Picasso.with(context)
                        .load(fetchData.getUrl())
                        .into(holder.ivImagePost);
        }
    }

    private void configureLoadMoreView(final LoadMoreHolder holder) {

        holder.btnLoadMore.setVisibility(loadMoreCall ? View.GONE : View.VISIBLE);
        holder.progressBar.setVisibility(loadMoreCall ? View.VISIBLE : View.GONE);

        holder.btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFeedsHandler != null) {
                    mFeedsHandler.onLoadMoreClick(list.size());

                    loadMoreCall = true;

                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.btnLoadMore.setVisibility(View.GONE);
                }
            }
        });
    }

    public void stopLoader() {
        loadMoreCall = false;

        try {
            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) recyclerView.findViewHolderForAdapterPosition(list.size());
            loadMoreHolder.progressBar.setVisibility(View.GONE);
            loadMoreHolder.btnLoadMore.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

//    public void downloadFile(String url) {
//
//        FileDownloadService downloadFileService = ApiClient.getInstance().getService(FileDownloadService.class);
//        Call<ResponseBody> call = downloadFileService.downloadFileWithDynamicUrlSync(url);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    Log.d(TAG, "server contacted and has file");
//
//                    boolean writtenToDisk = Utility.writeResponseBodyToDisk(response.body(), );
//
//                    Log.d(TAG, "file download was a success? " + writtenToDisk);
//                } else {
//                    Log.d(TAG, "server contact failed");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });
//
//
//    }
}