package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.model.LoginDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashActivity extends AppCompatActivity {
    public static final int RequestPermissionCode = 1;
    SharedPreferences sharedPreferences;
    List<LoginDetails> loginList;
    static String main_curr_version = "";
    TextView txtAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //----------------------------------------------------------------------------------------------------------------------------------
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo != null) {
            main_curr_version = pInfo.versionName;
        }
        txtAppName = findViewById(R.id.txt_app_name);
        txtAppName.animate().rotationY(360f).setDuration(2000);
        sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        loginList = new Gson().fromJson(sharedPreferences.getString("loginList", ""), new TypeToken<List<LoginDetails>>() {
        }.getType());
        if (Build.VERSION.SDK_INT > 24) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        new Handler().postDelayed(this::checkPermissionsMandAbove, 3000); // WAIT FOR 2 SECONDS
    }

    @TargetApi(23)
    public void checkPermissionsMandAbove() {
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23) {
            if (checkPermission()) {
                movetoLogin();
            } else {
                requestPermission();
            }
        } else {
            movetoLogin();
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------------
    private void movetoLogin() {
        if (TextUtils.isEmpty(sharedPreferences.getString("loginList", ""))) {
            Intent in = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(in);
            finish();
        } else {
            if ((loginList.get(0).getTEAMID().equals("0"))) {
                Intent intent = new Intent(SplashActivity.this, TeamActivity.class);
                intent.putExtra("loginList", (Serializable) loginList);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("loginList", (Serializable) loginList);
                startActivity(intent);
                finish();
            }
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------------
    private void requestPermission() {
        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{
                WRITE_EXTERNAL_STORAGE,
        }, RequestPermissionCode);
    }

    //--------------------------------------------------------------------------------------------------------------------------------------
    private boolean checkPermission() {
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    //--------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestPermissionCode) {
            if (grantResults.length > 0) {
                boolean ReadStoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (ReadStoragePermission) {
                    movetoLogin();
                } else {
                    Toast.makeText(SplashActivity.this, "Required All Permissions to granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
