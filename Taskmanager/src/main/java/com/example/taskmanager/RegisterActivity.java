package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.taskmanager.adapter.RoleAdapter;
import com.example.taskmanager.api.RegisterAPI;
import com.example.taskmanager.api.RetroClient;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Team;
import com.example.taskmanager.values.FunctionCall;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.taskmanager.SplashActivity.main_curr_version;
import static com.example.taskmanager.values.constants.INSERT_DATA_FAILURE;
import static com.example.taskmanager.values.constants.INSERT_DATA_SUCCESS;
import static com.example.taskmanager.values.constants.REQUEST_RESULT_FAILURE;
import static com.example.taskmanager.values.constants.REQUEST_RESULT_SUCCESS;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.et_name)
    EditText name;
    @BindView(R.id.et_mobile)
    EditText mobile;
    @BindView(R.id.et_designation)
    EditText designation;
    @BindView(R.id.et_password)
    EditText password;
    @BindView(R.id.sp_team)
    Spinner spinner;
    String TEAM = "";
    FunctionCall functionCall;
    ProgressDialog progressDialog;
    List<Status> statusList;
    List<Team> teamList;
    RoleAdapter adapter;

    @SuppressLint("SetTextI18n")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case REQUEST_RESULT_SUCCESS:
                    progressDialog.dismiss();
                    break;

                case REQUEST_RESULT_FAILURE:
                    progressDialog.dismiss();
                    finish();
                    break;

                case INSERT_DATA_SUCCESS:
                    progressDialog.dismiss();
                    functionCall.showToast(RegisterActivity.this, statusList.get(0).getMessage());
                    finish();
                    break;

                case INSERT_DATA_FAILURE:
                    progressDialog.dismiss();
                    functionCall.showToast(RegisterActivity.this, "try once again");
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initialize();
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    private void initialize() {
        functionCall = new FunctionCall();
        progressDialog = new ProgressDialog(this);
        statusList = new ArrayList<>();
        teamList = new ArrayList<>();

        getTeamNames();
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    @OnClick(R.id.btn_signup)
    public void onClick() {
        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError("Enter Full Name");
            return;
        }
        if (TextUtils.isEmpty(mobile.getText().toString())) {
            mobile.setError("Enter Mobile Number");
            return;
        }
        if (TextUtils.isEmpty(designation.getText().toString())) {
            designation.setError("Enter Designation");
            return;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Enter Password");
            return;
        }
        if (TextUtils.isEmpty(TEAM)) {
            functionCall.showToast(RegisterActivity.this, "Select Team");
            return;
        }
        functionCall.showprogressdialog("Please wait to complete", progressDialog, "Data Inserting");
        userRegister(name.getText().toString(), TEAM, designation.getText().toString(), main_curr_version, mobile.getText().toString(), password.getText().toString());
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void userRegister(String EMPNAME, String TEAMID, String DESIGNATION, String VERSION, String MOBILE, String PASSWORD) {
        RegisterAPI api = RetroClient.getApiService();
        api.userRegister(EMPNAME, TEAMID, DESIGNATION, VERSION, MOBILE, PASSWORD).enqueue(new Callback<List<Status>>() {
            @Override
            public void onResponse(@NonNull Call<List<Status>> call, @NonNull Response<List<Status>> response) {
                if (response.isSuccessful()) {
                    statusList = response.body();
                    handler.sendEmptyMessage(INSERT_DATA_SUCCESS);
                } else
                    handler.sendEmptyMessage(INSERT_DATA_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Status>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(INSERT_DATA_FAILURE);
            }
        });
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void getTeamNames() {
        RegisterAPI api = RetroClient.getApiService();
        api.getTeamNames().enqueue(new Callback<List<Team>>() {
            @Override
            public void onResponse(@NonNull Call<List<Team>> call, @NonNull Response<List<Team>> response) {
                if (response.isSuccessful()) {
                    teamList.clear();
                    teamList = response.body();
                    adapter = new RoleAdapter(teamList, RegisterActivity.this);
                    spinner.setAdapter(adapter);
                    spinner.setSelection(1);
                    handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
                } else
                    handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Team>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }
        });
    }

    @OnItemSelected(R.id.sp_team)
    public void spinnerItemSelected(int i) {
        TEAM = String.valueOf(i);
    }
}
