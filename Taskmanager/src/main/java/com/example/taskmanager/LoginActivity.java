package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.taskmanager.api.RegisterAPI;
import com.example.taskmanager.api.RetroClient;
import com.example.taskmanager.invoke.FTPAPI;
import com.example.taskmanager.model.LoginDetails;
import com.example.taskmanager.values.FunctionCall;
import com.google.gson.Gson;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.taskmanager.SplashActivity.main_curr_version;
import static com.example.taskmanager.values.constants.APK_FILE_DOWNLOADED;
import static com.example.taskmanager.values.constants.APK_FILE_NOT_FOUND;
import static com.example.taskmanager.values.constants.LOGIN_FAILURE;
import static com.example.taskmanager.values.constants.LOGIN_SUCCESS;

public class LoginActivity extends AppCompatActivity {
    private static final int DLG_APK_UPDATE_SUCCESS = 1;
    @BindView(R.id.et_user_name)
    EditText user_name;
    @BindView(R.id.et_password)
    EditText password;
    @BindView(R.id.lin_login)
    LinearLayout layout;
    FunctionCall functionCall;
    ProgressDialog progressDialog;
    List<LoginDetails> loginList;
    FTPAPI ftpapi;

    @SuppressLint("SetTextI18n")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case LOGIN_SUCCESS:
                    progressDialog.dismiss();
                    SavePreferences("loginList", loginList);
                    if (functionCall.compare(main_curr_version, loginList.get(0).getVERSION()))
                        show_Dialog(DLG_APK_UPDATE_SUCCESS);
                    else {
                        moveToNext();
                    }
                    break;

                case LOGIN_FAILURE:
                    progressDialog.dismiss();
                    functionCall.showToast(LoginActivity.this, "Login Failure");
                    break;

                case APK_FILE_DOWNLOADED:
                    progressDialog.dismiss();
                    functionCall.updateApp(LoginActivity.this, new File(functionCall.filepath("ApkFolder") +
                            File.separator + "Taskmanager_" + loginList.get(0).getVERSION() + ".apk"));
                    break;

                case APK_FILE_NOT_FOUND:
                    progressDialog.dismiss();
                    functionCall.setSnackBar(LoginActivity.this, layout, "APK Not Found");
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initialize();
    }

    //----------------------------------------------------------------------------------------------------------------------
    private void initialize() {
        functionCall = new FunctionCall();
        progressDialog = new ProgressDialog(this);
        loginList = new ArrayList<>();
        ftpapi = new FTPAPI();
    }

    //-------------------------------------------------------------------------------------------------------------------------
    @OnClick(R.id.btn_login)
    public void onClick() {
        if (TextUtils.isEmpty(user_name.getText())) {
            user_name.setError("Please Enter UserName");
            return;
        }
        if (TextUtils.isEmpty(password.getText())) {
            password.setError("Please Enter Password");
            return;
        }

        functionCall.showprogressdialog("Please wait to complete...", progressDialog, "Login");
        loginDetails(user_name.getText().toString(), password.getText().toString());
    }

    //-------------------------------------------------------------------------------------------------------------------------
    @OnClick(R.id.txt_signup)
    public void userSignup() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void loginDetails(String USERNAME, String PASSWORD) {
        RegisterAPI api = RetroClient.getApiService();
        api.getLoginDetails(USERNAME, PASSWORD).enqueue(new Callback<List<LoginDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<LoginDetails>> call, @NonNull Response<List<LoginDetails>> response) {
                if (response.isSuccessful()) {
                    loginList = response.body();
                    handler.sendEmptyMessage(LOGIN_SUCCESS);
                } else
                    handler.sendEmptyMessage(LOGIN_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<LoginDetails>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(LOGIN_FAILURE);
            }
        });
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void moveToNext() {
        if ((loginList.get(0).getTEAMID().equals("0"))) {
            Intent intent = new Intent(LoginActivity.this, TeamActivity.class);
            intent.putExtra("loginList", (Serializable) loginList);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("loginList", (Serializable) loginList);
            startActivity(intent);
            finish();
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------
    private void show_Dialog(int id) {
        Dialog dialog;
        if (id == DLG_APK_UPDATE_SUCCESS) {
            AlertDialog.Builder appupdate = new AlertDialog.Builder(this);
            appupdate.setTitle("App Updates");
            appupdate.setCancelable(false);
            appupdate.setMessage("Your current version number : " + main_curr_version + "\n" + "\n" +
                    "New version is available : " + loginList.get(0).getVERSION() + "\n");
            appupdate.setPositiveButton("UPDATE", (dialog1, which) -> {
                FTPAPI.Download_apk downloadApk = ftpapi.new Download_apk(handler, progressDialog, loginList.get(0).getVERSION());
                downloadApk.execute();
            });
            dialog = appupdate.create();
            dialog.show();
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    private void SavePreferences(String key, List<LoginDetails> value) {
        String httpParamJSONList = new Gson().toJson(value);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, httpParamJSONList);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
