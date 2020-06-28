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
import com.securitycam.models.User;
import com.securitycam.utils.Urls;

import java.util.ArrayList;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<User> users;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            imageView = view.findViewById(R.id.imageView);
        }
    }


    public PersonAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_person, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final User user = users.get(position);
        holder.tvName.setText(user.getName());
        Glide.with(context).load(Urls.APP_URL + user.getImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}