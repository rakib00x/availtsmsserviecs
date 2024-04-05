package com.asianitinc.availtrade.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.asianitinc.availtrade.R;
import com.shashank.sony.fancytoastlib.FancyToast;

import com.asianitinc.availtrade.config.ApiConfig;
import com.asianitinc.availtrade.config.InternetConnection;
import com.asianitinc.availtrade.config.RetrofitClient;
import com.asianitinc.availtrade.model.ServerResponseModel;
import com.asianitinc.availtrade.utils.LoadingUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signin extends AppCompatActivity implements View.OnClickListener {

    private LoadingUtils loadingUtils;
    private InternetConnection internetConnection;
    private EditText userName;
    private EditText passWord;
    private Button forgot;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0;

    private Button sendinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        initComponent();

        // Button element
        sendinfo = (Button) findViewById(R.id.sendinfo);
        sendinfo.setOnClickListener(this);

        forgot = (Button) findViewById(R.id.forgot);
        forgot.setOnClickListener(this);

    }

    /**
     * @ Initialize component
     */
    private void initComponent()
    {
        loadingUtils = new LoadingUtils(this);
        internetConnection = new InternetConnection(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.sendinfo){

            userName = (EditText) findViewById(R.id.username);
            String infoUserName = userName.getText().toString().trim();

            passWord = (EditText) findViewById(R.id.password);
            String infoPassWord = passWord.getText().toString().trim();


            loadingUtils.showProgressDialog();
            if(!internetConnection.isOnline())
            {
                FancyToast.makeText(Signin.this,"Please check your internet connection",FancyToast.LENGTH_LONG,FancyToast.WARNING,false).show();
                return;
            }

            ApiConfig config = RetrofitClient.getRetrofitInstance().create(ApiConfig.class);
            Call<ServerResponseModel> call = config.userLogin("",infoUserName,infoPassWord);
            call.enqueue(new Callback<ServerResponseModel>() {
                @Override
                public void onResponse(Call<ServerResponseModel> call, Response<ServerResponseModel> response) {

                    //Log.d("response",response.body().toString());

                    if(response.isSuccessful())
                    {

                        //String naj = response.body().getType();
                        //FancyToast.makeText(DealerAccess.this,naj,FancyToast.LENGTH_LONG,FancyToast.WARNING,false).show();

                        if(response.body().getCode().equals("200"))
                        {
                            String admin = response.body().getAdmin();

                            //FancyToast.makeText(Signin.this,admin,FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();

                            Intent intent = new Intent(Signin.this,Dashboard.class);
                            intent.putExtra("admin",admin);
                            startActivity(intent);

                        }else if(response.body().getCode().equals("1")){
                            FancyToast.makeText(Signin.this,"Mobile is missing",FancyToast.LENGTH_LONG,FancyToast.WARNING,false).show();
                        }else if(response.body().getCode().equals("2")){
                            FancyToast.makeText(Signin.this,"Password is missing",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
                        }else if(response.body().getCode().equals("3")){
                            FancyToast.makeText(Signin.this,"You must input only digit 0 to 9",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
                        }else if(response.body().getCode().equals("4")){
                            FancyToast.makeText(Signin.this,"Password is greater than 4 digit",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
                        }else if(response.body().getCode().equals("5")){
                            FancyToast.makeText(Signin.this,"Password is less than 4 digit",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
                        }else{
                            FancyToast.makeText(Signin.this,"User does not exist",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                        }

                    }
                    loadingUtils.dismissProgressDialog();
                }

                @Override
                public void onFailure(Call<ServerResponseModel> call, Throwable t) {
                    //Log.d("error", Objects.requireNonNull(t.getMessage()));
                    //loadingUtils.dismissProgressDialog();
                    FancyToast.makeText(Signin.this,"No Server Response", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                    //Intent intent = new Intent(DealerAccess.this,success.class);
                    //startActivity(intent);
                    finish();
                }

            });

        }

        if(v.getId() == R.id.forgot){
            Intent intent = new Intent(Signin.this,Forgot.class);
            startActivity(intent);
        }

    }
}