package co.sharechattest.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.sharechattest.R;

/**
 * Created by Varun on 02/05/17.
 */

public class ImagePostHolder extends RecyclerView.ViewHolder {

    public ImageView ivImagePost;
    public TextView tvAuthorName, tvDob;

    public ImagePostHolder(View itemView) {
        super(itemView);

        ivImagePost = (ImageView) itemView.findViewById(R.id.ivImagePost);

        tvAuthorName = (TextView) itemView.findViewById(R.id.tvAuthorName);
        tvDob = (TextView) itemView.findViewById(R.id.tvDob);

    }

}
