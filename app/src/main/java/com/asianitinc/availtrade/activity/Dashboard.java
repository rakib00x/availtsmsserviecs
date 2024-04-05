package com.asianitinc.availtrade.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.asianitinc.availtrade.R;
import com.asianitinc.availtrade.config.ApiConfig;
import com.asianitinc.availtrade.config.RetrofitClient;
import com.asianitinc.availtrade.model.ServerResponseModel;
import com.google.android.material.snackbar.Snackbar;

import com.asianitinc.availtrade.config.InternetConnection;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Dashboard extends AppCompatActivity{

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private String admin;
    private String type;
    private String branch;

    private RelativeLayout rlRoot;
    private TextView tvInternetNotice;
    private WebView webView;
    private ProgressDialog progressDialog;

    private InternetConnection internetConnection;

    private boolean isUrlLoaded = false, isUrlLoading = false;

    private Context mContext;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    Handler handler; // declared before onCreate
    Runnable runnable;

    private Button start_reading_sms;
    private Button stop_reading_sms;
    private Button reading_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        requestContactPermission();

        final int REQUEST_CODE_ASK_PERMISSIONS = 123;
        ActivityCompat.requestPermissions(Dashboard.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading ...");
        progressDialog.setTitle("Please wait");
        init();

        // starting the API Server
        // Param is optional, to run task on UI thread.
        Handler handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                // Do the task...

                String device = "101";
                ApiConfig config = RetrofitClient.getRetrofitInstance().create(ApiConfig.class);
                Call<ServerResponseModel> call = config.checkapirequest("",device);

                call.enqueue(new Callback<ServerResponseModel>() {
                    @Override
                    public void onResponse(Call<ServerResponseModel> call, Response<ServerResponseModel> response) {
                        if(response.isSuccessful())
                        {

                            String mobile = response.body().getMobile();

                            String[] numbers = {mobile};
                            String message = response.body().getMessage();

                            SmsManager smsManager = SmsManager.getDefault();

                            if (ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(Dashboard.this, Manifest.permission.SEND_SMS)) {

                                } else {
                                    ActivityCompat.requestPermissions(Dashboard.this,
                                            new String[]{Manifest.permission.SEND_SMS},
                                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                                }
                            }

                            try {
                                for (String number : numbers)
                                {
                                    smsManager.sendTextMessage(number, null, message, null, null);
                                }
                                FancyToast.makeText(Dashboard.this,"SMS Sent Successfully",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                            } catch (Exception e) {
                                Log.d("error",e.toString());
                                FancyToast.makeText(Dashboard.this,"SMS Sending Failed",FancyToast.LENGTH_LONG,FancyToast.WARNING,false).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponseModel> call, Throwable t) {
                        Log.d("error", Objects.requireNonNull(t.getMessage()));
                        //FancyToast.makeText(Dashboard.this,"failed",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                        finish();
                    }
                });

                handler.postDelayed(this, 5000); // Optional, to repeat the task.
            }
        };
        handler.postDelayed(runnable, 5000);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (!isUrlLoading && !isUrlLoaded && internetConnection.isOnline()) {
                admin = getIntent().getStringExtra("admin");
                type = getIntent().getStringExtra("type");
                branch = getIntent().getStringExtra("branch");

                Intent intent = getIntent();
                Bundle extras = intent.getExtras();
                String data = extras.getString("admin");

                String url = "https://sms.availtrade.com/admin-dashboard/"+data;
                renderWebPage(url);
            }
        }
    }

    private void init() {
        initViews();
        if (!internetConnection.isOnline()) {
            //tvInternetNotice.setVisibility(View.GONE);
            Intent intent = new Intent(Dashboard.this,errorOnConnection.class);
            startActivity(intent);
        } else {
            Snackbar.make(rlRoot, "Keep the internet connection on as long as it stays on the dashboard, please !!", Snackbar.LENGTH_LONG).show();
            tvInternetNotice.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        rlRoot = findViewById(R.id.rl_root);
        webView = findViewById(R.id.webView);
        tvInternetNotice = findViewById(R.id.tv_internet_warning);
        internetConnection = new InternetConnection(this);
    }

    /**
     * Custom method to render a web page
     *
     * @param urlToRender - web url
     */
    @SuppressLint("SetJavaScriptEnabled")
    protected void renderWebPage(String urlToRender) {

        isUrlLoading = true;

        tvInternetNotice.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressDialog.show();
                isUrlLoading = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                isUrlLoaded = true;
                isUrlLoading = false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
            }
        });

        webView.loadUrl(urlToRender);

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
    }


    private void getContacts() {
        //TODO get contacts code here
        //Toast.makeText(this, "Get contacts ....", Toast.LENGTH_LONG).show();

        // Collecting mobile number
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = ((ContentResolver) cr).query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {

                        String mobile = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //Toast.makeText(Dashboard.this, "Name: " + name + ", Phone No: " + mobile, Toast.LENGTH_SHORT).show();

                        Intent intent = getIntent();
                        Bundle extras = intent.getExtras();
                        String admin = extras.getString("admin");

                        ApiConfig config = RetrofitClient.getRetrofitInstance().create(ApiConfig.class);
                        Call<ServerResponseModel> call = config.importContact("",name,mobile,admin);

                        call.enqueue(new Callback<ServerResponseModel>() {
                            @Override
                            public void onResponse(Call<ServerResponseModel> call, Response<ServerResponseModel> response) {
                                if(response.isSuccessful())
                                {
                                    String mobile = response.body().getMobile();
                                }
                            }

                            @Override
                            public void onFailure(Call<ServerResponseModel> call, Throwable t) {
                                Log.d("error", Objects.requireNonNull(t.getMessage()));
                                //FancyToast.makeText(Dashboard.this,"failed",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                finish();
                            }
                        });

                    }
                    pCur.close();
                }
            }
        }
        // End of collecting mobile number

    }

    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.P)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                getContacts();
            }
        } else {
            getContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            } else {
                Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
            }
            return;
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.isFocused() && webView.canGoBack()) {
            webView.goBack();
        } else {
            //super.onBackPressed();
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Dashboard.super.onBackPressed();
                            quit();
                        }
                    }).create().show();
        }
    }

    public void quit() {
        Intent start = new Intent(Intent.ACTION_MAIN);
        start.addCategory(Intent.CATEGORY_HOME);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(start);
    }

    @Override
    public void finish() {
        super.finish();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
