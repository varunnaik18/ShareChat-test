package co.sharechattest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import co.sharechattest.api.callback.ServiceCallback;
import co.sharechattest.api.client.ApiClient;
import co.sharechattest.api.model.FetchData;
import co.sharechattest.api.model.FetchResponse;
import co.sharechattest.api.model.PostDataFetch;
import co.sharechattest.api.model.UpdateData;
import co.sharechattest.api.service.PostApiInterface;
import co.sharechattest.app.ShareChatTestApp;
import co.sharechattest.utils.Check;
import co.sharechattest.utils.Constants;
import co.sharechattest.utils.ToastUtils;
import co.sharechattest.utils.Utility;
import retrofit2.Call;

public class ProfileDetailsActivity extends AppCompatActivity implements TextWatcher, RadioGroup.OnCheckedChangeListener {

    private FetchData mFetchData = null;
    Calendar myCalendar = Calendar.getInstance();

    private boolean mChangesMade = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        // Listen to intent received and assign values
        receiveBundle(getIntent());

        init();

    }

    private void init() {

        getTvDob().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProfileDetailsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        getBtnSave().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataForUser();
            }
        });

        getEtStatus().addTextChangedListener(this);
        getEtContact().addTextChangedListener(this);
        getEtAge().addTextChangedListener(this);
        getTvDob().addTextChangedListener(this);

        getRadioGroup().setOnCheckedChangeListener(this);

    }

    private void receiveBundle(Intent intent) {

        Bundle bundle = intent.getExtras();

        if (bundle == null) // there are some values in bundle
            return;

        Object receivedObject = bundle.getSerializable(Constants.BUNDLE_KEY_PROFILE_DATA);

        mFetchData = (FetchData) receivedObject;

        if (mFetchData == null)
            return;

        getCollapsingToolbarLayout().setTitle(mFetchData.getAuthorName());

        getTvDob().setText(mFetchData.getAuthorDob());
        getEtAge().setText(mFetchData.getAuthorAge());
        getEtContact().setText(mFetchData.getAuthorContact());
        getEtStatus().setText(mFetchData.getAuthorStatus());

        switch (mFetchData.getAuthorGender()) {

            case "male":
                getRbMale().setChecked(true);
                break;

            case "female":
                getRbFemale().setChecked(true);
                break;
        }

        if (mFetchData.isLocalImagePresent()) {
            Picasso.with(this)
                    .load(new File(Utility.getImagePath(mFetchData.getId())))
                    .into(getIvBackdrop());
        } else {

            if (!Check.isEmpty(mFetchData.getProfileUrl()))
                Picasso.with(this)
                        .load(mFetchData.getProfileUrl())
                        .into(getIvBackdrop());
        }
    }

    private void updateDataForUser() {

        if (!ShareChatTestApp.isNetworkAvailable()) {
            ToastUtils.showToast(R.string.network_error);
            return;
        }

        String dob = getTvDob().getText().toString();
        String contact = getEtContact().getText().toString();
        String status = getEtStatus().getText().toString();
        String gender = "";

        if (getRbMale().isChecked())
            gender = "male";
        else if (getRbFemale().isChecked())
            gender = "female";

        if (mFetchData == null) {
            ToastUtils.showToast("Something went wrong, please try again later");
            return;
        }

        if (Check.isEmpty(dob) || Check.isEmpty(contact) || Check.isEmpty(status) || Check.isEmpty(gender)) {
            ToastUtils.showToast("All fields are mandatory");
            return;
        }

        UpdateData updateData = new UpdateData();
        updateData.setId(mFetchData.getId());
        updateData.setAuthorName(mFetchData.getAuthorName());
        updateData.setType(mFetchData.getType());
        updateData.setProfileUrl(mFetchData.getProfileUrl());
        updateData.setAuthorDob(dob);
        updateData.setAuthorStatus(status);
        updateData.setAuthorGender(gender);
        updateData.setAuthorContact(contact);

        PostDataFetch data = new PostDataFetch();
        data.setRequestId(BuildConfig.REQUEST_ID);
        data.setData(updateData);

        PostApiInterface postApiInterface = ApiClient.getInstance().getService(PostApiInterface.class);
        Call<FetchResponse> call = postApiInterface.updateAuthorInfo(data);
        call.enqueue(new ServiceCallback<FetchResponse>() {
            @Override
            protected void onSuccess(FetchResponse response) {

                if (response.isSuccess())
                    ToastUtils.showToast("Profile updated Successfully");
                else
                    ToastUtils.showToast(response.getError());

                ProfileDetailsActivity.this.finish();

            }

            @Override
            protected void onFailure(int code, String message, FetchResponse response) {
                if (response != null)
                    ToastUtils.showToast(Check.isEmpty(response.getError()) ? message : response.getError());
            }
        });

    }

    private void showStopDownloadingDialog(String message) {

        new AlertDialog.Builder(ProfileDetailsActivity.this).setMessage("Confirm").setCancelable(false)
                .setMessage(message)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ProfileDetailsActivity.this.finish();
                    }
                })
                .setNegativeButton("NO", null).show();
    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

        private void updateLabel() {

            String myFormat = "MM/dd/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            getTvDob().setText(sdf.format(myCalendar.getTime()));
        }

    };

    private TextView getTvDob() {
        return (TextView) findViewById(R.id.tvDOb);
    }

    private EditText getEtAge() {

        return (EditText) findViewById(R.id.etAge);
    }

    private EditText getEtStatus() {

        return (EditText) findViewById(R.id.etStatus);
    }

    private EditText getEtContact() {

        return (EditText) findViewById(R.id.etContact);
    }

    private RadioButton getRbMale() {

        return (RadioButton) findViewById(R.id.rbMale);
    }

    private RadioButton getRbFemale() {

        return (RadioButton) findViewById(R.id.rbFemale);
    }

    private Button getBtnSave() {

        return (Button) findViewById(R.id.btnSave);
    }

    private ImageView getIvBackdrop() {

        return (ImageView) findViewById(R.id.ivBackdrop);
    }

    private RadioGroup getRadioGroup() {
        return (RadioGroup) findViewById(R.id.radioGroup);
    }

    private CollapsingToolbarLayout getCollapsingToolbarLayout() {

        return (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mChangesMade = true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        mChangesMade = true;
    }


    @Override
    public void onBackPressed() {
        if (mChangesMade)
            showStopDownloadingDialog("Are you sure you want to exit without saving your changes?");
        else
            super.onBackPressed();
    }
}
