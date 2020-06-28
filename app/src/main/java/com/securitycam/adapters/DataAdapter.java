package com.securitycam.adapters;

import android.animation.ObjectAnimator;
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
import com.securitycam.utils.Urls;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Data> datas;
    private View.OnClickListener onClickListener;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvSafety, tvRegular;
        ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvDate = view.findViewById(R.id.tvDate);
            tvSafety = view.findViewById(R.id.tvSafety);
            imageView = view.findViewById(R.id.imageView);
            tvRegular = view.findViewById(R.id.tvRegular);
        }
    }


    public DataAdapter(Context context, ArrayList<Data> datas, View.OnClickListener onClickListener) {
        this.context = context;
        this.datas = datas;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_data, parent, false);
        view.setOnClickListener(onClickListener);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Data data = datas.get(position);
        holder.tvName.setText(data.getName());
        holder.tvDate.setText(data.getDate());
        if (data.isRegular()){
            holder.tvRegular.setText("Regular");
        } else {
            holder.tvRegular.setText("From Bell");
        }
        holder.tvSafety.setText(data.getSafetyPercent() +"%");
        Glide.with(context).load(Urls.APP_URL + data.getImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

}