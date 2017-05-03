package co.sharechattest.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.sharechattest.R;

/**
 * Created by Varun on 02/05/17.
 */

public class UserProfileHolder extends RecyclerView.ViewHolder {

    public LinearLayout llUserProfile;
    public ImageView ivProfileImage;
    public TextView tvName, tvAge, tvDob, tvStatus, tvGender;

    public UserProfileHolder(View itemView) {
        super(itemView);

        llUserProfile = (LinearLayout) itemView.findViewById(R.id.llUserProfile);
        ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);

        tvName = (TextView) itemView.findViewById(R.id.tvName);
        tvAge = (TextView) itemView.findViewById(R.id.tvAge);
        tvDob = (TextView) itemView.findViewById(R.id.tvDob);
        tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
        tvGender = (TextView) itemView.findViewById(R.id.tvGender);
    }

}
