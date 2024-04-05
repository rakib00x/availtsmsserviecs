package com.asianitinc.availtrade.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

import com.asianitinc.availtrade.R;
import com.asianitinc.availtrade.config.ApiConfig;
import com.asianitinc.availtrade.config.RetrofitClient;
import com.asianitinc.availtrade.model.ServerResponseModel;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.Objects;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class messagebox extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    Handler handler; // declared before onCreate
    Runnable runnable;
    private Button start_reading_sms;
    private Button stop_reading_sms;
    private Button reading_contact;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagebox);

        final int REQUEST_CODE_ASK_PERMISSIONS = 123;
        final int REQUEST_CODE_FOR_READ_CONTACT_PERMISSIONS = 1;

        ActivityCompat.requestPermissions(messagebox.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);

        ActivityCompat.requestPermissions(messagebox.this, new String[]{"android.permission.READ_CONTACTS"}, REQUEST_CODE_FOR_READ_CONTACT_PERMISSIONS);

        start_reading_sms = (Button) findViewById(R.id.start_reading_sms);
        start_reading_sms.setOnClickListener(this);

        stop_reading_sms = (Button) findViewById(R.id.stop_reading_sms);
        stop_reading_sms.setOnClickListener(this);

        reading_contact = (Button) findViewById(R.id.reading_contact);
        reading_contact.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.start_reading_sms){

                FancyToast.makeText(messagebox.this,"Synchronization Successfully Started !!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();

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

                                    if (ContextCompat.checkSelfPermission(messagebox.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                        if (ActivityCompat.shouldShowRequestPermissionRationale(messagebox.this, Manifest.permission.SEND_SMS)) {

                                        } else {
                                            ActivityCompat.requestPermissions(messagebox.this,
                                                    new String[]{Manifest.permission.SEND_SMS},
                                                    MY_PERMISSIONS_REQUEST_SEND_SMS);
                                        }
                                    }

                                    try {
                                        for (String number : numbers)
                                        {
                                            smsManager.sendTextMessage(number, null, message, null, null);
                                        }
                                        FancyToast.makeText(messagebox.this,"SMS Sent Successfully",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                    } catch (Exception e) {
                                        Log.d("error",e.toString());
                                        FancyToast.makeText(messagebox.this,"SMS Sending Failed",FancyToast.LENGTH_LONG,FancyToast.WARNING,false).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ServerResponseModel> call, Throwable t) {
                                Log.d("error", Objects.requireNonNull(t.getMessage()));
                                FancyToast.makeText(messagebox.this,"failed",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                finish();
                            }
                        });

                        handler.postDelayed(this, 5000); // Optional, to repeat the task.
                    }
                };

                handler.postDelayed(runnable, 5000);

        }

        if(v.getId() == R.id.stop_reading_sms){
            handler.removeCallbacks(runnable);
            FancyToast.makeText(messagebox.this,"Synchronization Stopped !!",FancyToast.LENGTH_LONG,FancyToast.WARNING,false).show();
        }

        if(v.getId() == R.id.reading_contact){

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
                            Toast.makeText(messagebox.this, "Name: " + name + ", Phone No: " + mobile, Toast.LENGTH_SHORT).show();

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
                                    FancyToast.makeText(messagebox.this,"failed",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                    finish();
                                }
                            });

                        }
                        pCur.close();
                    }
                }
            }

        }

    }

}