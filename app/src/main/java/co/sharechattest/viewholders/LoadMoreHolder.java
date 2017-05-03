package co.sharechattest.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import co.sharechattest.R;

/**
 * Created by Varun on 02/05/17.
 */

public class LoadMoreHolder extends RecyclerView.ViewHolder {

    public Button btnLoadMore;
    public ProgressBar progressBar;

    public LoadMoreHolder(View itemView) {
        super(itemView);

        btnLoadMore = (Button) itemView.findViewById(R.id.btnLoadMore);
        progressBar = (ProgressBar) itemView.findViewById(R.id.footerProgressBar);

    }

}
