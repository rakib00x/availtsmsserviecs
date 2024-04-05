package com.asianitinc.availtrade.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.asianitinc.availtrade.R;
import com.google.android.material.snackbar.Snackbar;

import com.asianitinc.availtrade.config.InternetConnection;


public class Registration extends AppCompatActivity{

    private String admin;
    private String type;
    private String branch;

    private RelativeLayout rlRoot;
    private TextView tvInternetNotice;
    private WebView webView;
    private ProgressDialog progressDialog;

    private InternetConnection internetConnection;

    private boolean isUrlLoaded = false, isUrlLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading ....");
        progressDialog.setTitle("Please wait !!");
        init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (!isUrlLoading && !isUrlLoaded && internetConnection.isOnline()) {

                admin = getIntent().getStringExtra("admin");
                type = getIntent().getStringExtra("type");
                branch = getIntent().getStringExtra("branch");

                String url = "https://sms.availtrade.com/get-user-registration-form";
                renderWebPage(url);

            }
        }
    }

    private void init() {
        initViews();
        if (!internetConnection.isOnline()) {
            //tvInternetNotice.setVisibility(View.GONE);
            Intent intent = new Intent(Registration.this,errorOnConnection.class);
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

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
