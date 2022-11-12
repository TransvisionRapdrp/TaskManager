package com.example.taskmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.adapter.TeamAdapter;
import com.example.taskmanager.api.RegisterAPI;
import com.example.taskmanager.api.RetroClient;
import com.example.taskmanager.model.LoginDetails;
import com.example.taskmanager.model.Team;
import com.example.taskmanager.values.FunctionCall;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.taskmanager.SplashActivity.main_curr_version;
import static com.example.taskmanager.values.constants.REQUEST_RESULT_FAILURE;
import static com.example.taskmanager.values.constants.REQUEST_RESULT_SUCCESS;

public class TeamActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int DIALOG_PROFILE_DETAILS = 1;
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindString(R.string.app_name)
    String app_name;
    @BindColor(R.color.white)
    int color;
    @BindDrawable(R.drawable.ic_user1)
    Drawable image;
    List<Team> teamList;
    ProgressDialog progressDialog;
    TeamAdapter adapter;
    @BindView(R.id.rec_status)
    RecyclerView recyclerView;
    FunctionCall functionCall;
    private boolean isFirstBackPressed = false;
    List<LoginDetails> loginList;

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
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        initialize();
    }

    //----------------------------------------------------------------------------------------------------------------------------
    private void initialize() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle((R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_user1));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> showdialog(DIALOG_PROFILE_DETAILS));

        Intent intent = getIntent();
        loginList = (ArrayList<LoginDetails>) intent.getSerializableExtra("loginList");
        functionCall = new FunctionCall();
        teamList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.rec_team);

        functionCall.showprogressdialog("Please wait to complete", progressDialog, "Data Loading");
        getTeamNames();
    }

    //--------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.search_menu, menu);
        MenuItem search = menu.findItem(R.id.nav_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint("Search....");
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //*******************************************************************************************************
    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.main_toolbar) {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void getTeamNames() {
        RegisterAPI api = RetroClient.getApiService();
        api.getTeamNames().enqueue(new Callback<List<Team>>() {
            @Override
            public void onResponse(@NonNull Call<List<Team>> call, @NonNull Response<List<Team>> response) {
                if (response.isSuccessful()) {
                    teamList = response.body();
                    adapter = new TeamAdapter(teamList, TeamActivity.this,loginList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(TeamActivity.this));
                    recyclerView.setAdapter(adapter);
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

    //--------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            super.onBackPressed();
        } else {
            if (isFirstBackPressed) {
                super.onBackPressed();
            } else {
                isFirstBackPressed = true;
                Toast.makeText(this, "Press again to close app", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> isFirstBackPressed = false, 2000);
            }
        }
    }

    //------------------------------------Method for alert dialog---------------------------------------------------------------//
    private void showdialog(int id) {
        final AlertDialog login_dialog;
        if (id == DIALOG_PROFILE_DETAILS) {
            AlertDialog.Builder login_dlg = new AlertDialog.Builder(this);
            @SuppressLint("InflateParams") LinearLayout dlg_linear = (LinearLayout) getLayoutInflater().inflate(R.layout.profile_layout, null);
            login_dlg.setView(dlg_linear);
            final TextView tv_user = dlg_linear.findViewById(R.id.txt_user_name);
            tv_user.setText(loginList.get(0).getEMPNAME());
            final TextView tv_designation = dlg_linear.findViewById(R.id.txt_designation);
            tv_designation.setText(loginList.get(0).getDESIGNATION());
            final TextView tv_version = dlg_linear.findViewById(R.id.txt_vesrion);
            tv_version.setText(main_curr_version);
            final TextView tv_logout = dlg_linear.findViewById(R.id.txt_logout);
            tv_logout.setOnClickListener(view -> {
                logout();
            });
            login_dialog = login_dlg.create();
            login_dialog.show();
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------
    private void logout() {
        Intent intent = new Intent(TeamActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
