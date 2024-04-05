package com.asianitinc.availtrade.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.asianitinc.availtrade.R;
import com.bumptech.glide.Glide;

import java.util.Objects;

public class LoadingUtils
{
    private ProgressDialog progressDialog;

    public LoadingUtils(Context context ){

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading..");
        progressDialog.setTitle("Please wait");
    }

    /**
     * @ Display progress dialog
     */
    public void showProgressDialog()
    {
        if(progressDialog != null)
            progressDialog.show();
    }

    /**
     *
     * @ Dismiss progress dialog
     */
    public void dismissProgressDialog()
    {
        if(progressDialog != null)
        {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    /**
     * warning alert dialog
     * @param context, application context
     */
    public void singleUserDetails(Context context,String name,String email,String avatar)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        @SuppressLint("InflateParams")
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.layout_single_user, null);
        //Set the view
        builder.setView(view);
        TextView tvName,tvEmail;
        ImageView imageIcon = view.findViewById(R.id.img_user_avatar);
        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvName.setText(name);//set name
        tvEmail.append(email);//set email
        Glide.with(context)
                        .load(avatar)
            .error(R.drawable.img_pro_pic)
            .placeholder(R.drawable.img_pro_pic)
            .into(imageIcon);

        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

}
