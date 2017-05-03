package co.sharechattest;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import co.sharechattest.api.model.FetchData;
import co.sharechattest.utils.Check;
import co.sharechattest.utils.Constants;
import co.sharechattest.utils.ToastUtils;

public class ProfileDetailsActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_CODE_MULTIPLE_PERMISSIONS = 124;

    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        init();

        // Listen to intent received and assign values
        receiveBundle(getIntent());

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

        getIbEditImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForPermission();
            }
        });
    }

    private void receiveBundle(Intent intent) {

        Bundle bundle = intent.getExtras();

        if (bundle == null) // there are some values in bundle
            return;

        Object receivedObject = bundle.getSerializable(Constants.BUNDLE_KEY_PROFILE_DATA);

        FetchData fetchData = (FetchData) receivedObject;

        getTvDob().setText(fetchData.getAuthorDob());
        getEtAge().setText(fetchData.getAuthorAge());
        getEtContact().setText(fetchData.getAuthorContact());
        getEtStatus().setText(fetchData.getAuthorStatus());

        switch (fetchData.getAuthorGender()) {

            case "male":
                getRbMale().setChecked(true);
                break;

            case "female":
                getRbFemale().setChecked(true);
                break;
        }

        if (!fetchData.isLocalImagePresent() && !Check.isEmpty(fetchData.getProfileUrl())) {
            Picasso.with(this).load(fetchData.getProfileUrl()).into(getIvBackdrop());
        }
    }

    private void checkForPermission() {

        if (ContextCompat.checkSelfPermission(ProfileDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) { // case when permission is not granted

            askPermission();
        } else {


        }
    }

    public void askPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(ProfileDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_MULTIPLE_PERMISSIONS);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_CODE_MULTIPLE_PERMISSIONS
                && grantResults.length > 0) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {



            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

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

    private ImageButton getIbEditImage() {

        return (ImageButton) findViewById(R.id.ibEditImage);
    }
}
