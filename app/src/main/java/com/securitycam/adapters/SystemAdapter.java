package com.securitycam.adapters;

import android.content.Context;
import android.graphics.Color;
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
import com.securitycam.models.System;

import java.util.ArrayList;

public class SystemAdapter extends RecyclerView.Adapter<SystemAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<System> systems;
    private View.OnClickListener onClickListener;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMembership;
        ImageView ivStatus;

        MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvMembership = view.findViewById(R.id.tvMembership);
            ivStatus = view.findViewById(R.id.ivStatus);
        }
    }


    public SystemAdapter(Context context, ArrayList<System> systems, View.OnClickListener onClickListener) {
        this.context = context;
        this.systems = systems;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_system, parent, false);
        view.setOnClickListener(onClickListener);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final System system = systems.get(position);
        holder.tvName.setText(system.getName());

        if (system.isHost()){
            holder.tvMembership.setText("You are host");
        }else {
            holder.tvMembership.setText("You are a home member");
        }

        if (system.isSelected()){
            Glide.with(context).load(R.drawable.selected).into(holder.ivStatus);
        }
    }

    @Override
    public int getItemCount() {
        return systems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (systems.get(position).isSelected()){
            return 1;
        }
        return 0;
    }
}