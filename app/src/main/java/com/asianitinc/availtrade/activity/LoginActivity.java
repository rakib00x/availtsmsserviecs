package com.asianitinc.availtrade.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.asianitinc.availtrade.R;
import com.asianitinc.availtrade.config.InternetConnection;
import com.asianitinc.availtrade.utils.LoadingUtils;
import com.google.android.material.snackbar.Snackbar;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private String email,password;
    private RelativeLayout rlRoot;

    private Button tv_sign_up;
    private Button tv_sign_in;

    private LoadingUtils loadingUtils;
    private InternetConnection internetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        ButterKnife.bind(this);
        initComponent();

        final int REQUEST_CODE_ASK_PERMISSIONS = 123;
        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);

        // Button element
        tv_sign_up = (Button) findViewById(R.id.tv_sign_up);
        tv_sign_up.setOnClickListener(this);

        tv_sign_in = (Button) findViewById(R.id.tv_sign_in);
        tv_sign_in.setOnClickListener(this);
    }

    /**
     * @ Initialize component
     */
    private void initComponent()
    {
        loadingUtils = new LoadingUtils(this);
        internetConnection = new InternetConnection(this);

        if (!internetConnection.isOnline()) {
            //tvInternetNotice.setVisibility(View.GONE);
            Intent intent = new Intent(LoginActivity.this,errorOnConnection.class);
            startActivity(intent);
        } else {
            setContentView(R.layout.layout_login);
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.tv_sign_up){
            Intent intent = new Intent(LoginActivity.this,Registration.class);
            startActivity(intent);
        }

        if(v.getId() == R.id.tv_sign_in){
            Intent intent = new Intent(LoginActivity.this,Signin.class);
            startActivity(intent);
        }

    }

}
