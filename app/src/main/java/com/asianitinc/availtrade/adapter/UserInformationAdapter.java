package com.asianitinc.availtrade.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.asianitinc.availtrade.R;
import com.asianitinc.availtrade.model.GetUserResponseModel.UserModel;
import com.asianitinc.availtrade.utils.LoadingUtils;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInformationAdapter extends
        RecyclerView.Adapter<UserInformationAdapter.ViewHolder>
{
    private List<UserModel> getUserResponseModels;
    private Context context;
    private LayoutInflater inflater;
    private LoadingUtils loadingUtils;

    public UserInformationAdapter(Context context, List<UserModel>models)
    {
        this.context = context;
        this.getUserResponseModels = models;
        inflater = LayoutInflater.from(context);
        loadingUtils = new LoadingUtils(context);
    }

    @NonNull
    @Override
    public UserInformationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_user_model,parent,false);
        return new UserInformationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserInformationAdapter.ViewHolder holder, int position) {
        UserModel model = getUserResponseModels.get(position);
        holder.setData(model);
    }

    @Override
    public int getItemCount() {
        return getUserResponseModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView tvUserName,tvUserEmail;
        private CircleImageView ivUserImage;
        private UserModel currentModel;

        private ViewHolder(View view) {
            super(view);
            tvUserEmail = view.findViewById(R.id.tv_email);
            tvUserName = view.findViewById(R.id.tv_name);
            ivUserImage = view.findViewById(R.id.img_user_avatar);
            CardView cardView = view.findViewById(R.id.cv_cardview);
            cardView.setOnClickListener(UserInformationAdapter.ViewHolder.this);
        }

        @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
        private void setData(UserModel model)
        {
            currentModel = model;
            tvUserName.setText(model.getFirstName() + model.getLastName());
            tvUserEmail.setText(model.getEmail());

            if(model.getAvatar() != null)
                Glide.with(context)
                        .load(model.getAvatar())
                        .error(R.drawable.img_pro_pic)
                        .placeholder(R.drawable.img_pro_pic)
                        .into(ivUserImage);
        }

        @Override
        public void onClick(View view) {
            loadingUtils.singleUserDetails(context,currentModel.getFirstName()+" "+currentModel.getLastName(),currentModel.getEmail(),currentModel.getAvatar());
        }
    }
}

