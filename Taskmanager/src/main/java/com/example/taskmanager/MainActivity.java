package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.adapter.StatusAdapter;
import com.example.taskmanager.api.RegisterAPI;
import com.example.taskmanager.api.RetroClient;
import com.example.taskmanager.model.LoginDetails;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.values.FunctionCall;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.taskmanager.SplashActivity.main_curr_version;
import static com.example.taskmanager.values.constants.DELETE_DATA_FAILURE;
import static com.example.taskmanager.values.constants.DELETE_DATA_SUCCESS;
import static com.example.taskmanager.values.constants.INSERT_DATA_FAILURE;
import static com.example.taskmanager.values.constants.INSERT_DATA_SUCCESS;
import static com.example.taskmanager.values.constants.REQUEST_RESULT_FAILURE;
import static com.example.taskmanager.values.constants.REQUEST_RESULT_SUCCESS;

public class MainActivity extends AppCompatActivity {
    private static final int DIALOG_PROFILE_DETAILS = 1;
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    List<Status> statusList;
    ProgressDialog progressDialog;
    StatusAdapter adapter;
    @BindView(R.id.rec_status)
    RecyclerView recyclerView;
    @BindView(R.id.et_status)
    EditText status;
    FunctionCall functionCall;
    @BindString(R.string.app_name)
    String app_name;
    @BindColor(R.color.white)
    int color;
    @BindDrawable(R.drawable.ic_user1)
    Drawable image;
    List<LoginDetails> loginList;
    private boolean isFirstBackPressed = false;
    @BindView(R.id.lin_bottom)
    LinearLayout layout;
    private String regex = "'!~@#$%^&*:;<>.,/}";

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

                case INSERT_DATA_SUCCESS:
                    progressDialog.dismiss();
                    finish();
                    startActivity(getIntent());
                    break;

                case INSERT_DATA_FAILURE:
                    progressDialog.dismiss();
                    functionCall.showToast(MainActivity.this, "try once again..");
                    break;

                case DELETE_DATA_SUCCESS:
                    progressDialog.dismiss();
                    finish();
                    startActivity(getIntent());
                    functionCall.showToast(MainActivity.this, statusList.get(0).getMessage());
                    break;

                case DELETE_DATA_FAILURE:
                    progressDialog.dismiss();
                    functionCall.showToast(MainActivity.this, "try once again..");
                    finish();
                    startActivity(getIntent());
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initialize();
    }

    //----------------------------------------------------------------------------------------------------------------------------
    private void initialize() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar.setTitle(app_name);
        toolbar.setTitleTextColor(color);
        toolbar.setNavigationIcon(image);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> showdialog(DIALOG_PROFILE_DETAILS));

        Intent intent = getIntent();
        loginList = (ArrayList<LoginDetails>) intent.getSerializableExtra("loginList");
        functionCall = new FunctionCall();
        statusList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        status.setFilters(new InputFilter[]{filter});
        functionCall.showprogressdialog("Please wait to complete", progressDialog, "Data Loading");
        getStatus(loginList.get(0).getEMPID(), loginList.get(0).getEMPID());

        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
    }


    @OnClick(R.id.btn_submit)
    public void onClick() {
        if (TextUtils.isEmpty(status.getText().toString())) {
            status.setError("Type something...");
            return;
        }
        functionCall.showprogressdialog("Please wait to complete", progressDialog, "Data Inserting");
        insertStatus(loginList.get(0).getEMPID(), loginList.get(0).getEMPNAME(), loginList.get(0).getTEAMID(), status.getText().toString());
    }

    //---------------------------------------------------------------------------------------------------------------------------------------
    private InputFilter filter = (source, start, end, dest, dstart, dend) -> {
        if (source != null && regex.contains(("" + source))) {
            return "";
        }
        return null;
    };

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

    //-----------------------------------------------------------------------------------------------------------------------------------------
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

    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void getStatus(String EMPID, String TEAMID) {
        RegisterAPI api = RetroClient.getApiService();
        api.getStatus(EMPID, TEAMID, "0").enqueue(new Callback<List<Status>>() {
            @Override
            public void onResponse(@NonNull Call<List<Status>> call, @NonNull Response<List<Status>> response) {
                if (response.isSuccessful()) {
                    statusList = response.body();
                    adapter = new StatusAdapter(statusList, MainActivity.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    recyclerView.setAdapter(adapter);
                    handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
                } else
                    handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Status>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }
        });
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void insertStatus(String EMPID, String EMPNAME, String TEAMID, String STATUS) {
        RegisterAPI api = RetroClient.getApiService();
        api.insertStatus(EMPID, EMPNAME, TEAMID, STATUS, "0").enqueue(new Callback<List<Status>>() {
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
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void deleteStatus(String STATUSID, String EMPID) {
        RegisterAPI api = RetroClient.getApiService();
        api.deleteStatus(STATUSID, EMPID).enqueue(new Callback<List<Status>>() {
            @Override
            public void onResponse(@NonNull Call<List<Status>> call, @NonNull Response<List<Status>> response) {
                if (response.isSuccessful()) {
                    statusList = response.body();
                    handler.sendEmptyMessage(DELETE_DATA_SUCCESS);
                } else
                    handler.sendEmptyMessage(DELETE_DATA_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Status>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(DELETE_DATA_FAILURE);
            }
        });
    }

    //******************************delete by swipe***********************************************************
    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_delete);
                assert xMark != null;
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView tickets_view, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                functionCall.showprogressdialog("Please wait to complete", progressDialog, "Deleting...");
                deleteStatus(statusList.get(swipedPosition).getSTATUSID(), loginList.get(0).getEMPID());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                if (viewHolder.getAdapterPosition() == -1) {
                    return;
                }
                if (!initiated) {
                    init();
                }
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();
                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                xMark.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setUpAnimationDecoratorHelper() {
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                if (!initiated) {
                    init();
                }
                if (Objects.requireNonNull(parent.getItemAnimator()).isRunning()) {
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;
                    int left = 0;
                    int right = parent.getWidth();
                    int top = 0;
                    int bottom = 0;
                    int childCount = Objects.requireNonNull(parent.getLayoutManager()).getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        assert child != null;
                        if (child.getTranslationY() < 0) {
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }
                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }
                    background.setBounds(left, top, right, bottom);
                    background.draw(c);
                }
                super.onDraw(c, parent, state);
            }
        });
    }
}
