package com.securitycam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.securitycam.MainActivity;
import com.securitycam.R;
import com.securitycam.models.Data;
import com.securitycam.models.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<User> users;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole;
        ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvRole = view.findViewById(R.id.tvRole);
            imageView = view.findViewById(R.id.imageView);
        }
    }


    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_user, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final User user = users.get(position);
        holder.tvName.setText(user.getName());
        if (user.getRole() == 1){
            holder.tvRole.setText("Host");
        }else {
            holder.tvRole.setText("Member");
        }
        if (user.getImage().length() > 20){
            Glide.with(context).load(user.getImage()).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}