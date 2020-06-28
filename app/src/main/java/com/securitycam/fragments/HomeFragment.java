package com.securitycam.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.securitycam.MainActivity;
import com.securitycam.R;
import com.securitycam.adapters.DataAdapter;
import com.securitycam.models.Data;
import com.securitycam.models.System;
import com.securitycam.utils.MySingleton;
import com.securitycam.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private ArrayList<Data> datas = new ArrayList<>();
    private DataAdapter dataAdapter;
    private RecyclerView recyclerView;

    private RequestQueue queue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = MySingleton.getInstance(getContext()).getRequestQueue();
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setView(view);
        return view;
    }

    private void setView(View view) {

        recyclerView = view.findViewById(R.id.recyclerView);
        dataAdapter = new DataAdapter(getContext(), datas, HomeFragment.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(dataAdapter);

        getDatas();
    }

    private void getDatas() {
        datas.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Urls.DATAS_URL, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    response = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                    JSONObject jsonObject = new JSONObject(response);
                    int selected_membership_count = jsonObject.getInt("selected_membership_count");

                    if (selected_membership_count > 0){
                        JSONArray jsonArrayMembers = jsonObject.getJSONArray("datas");
                        JSONObject jsonObjectSystem = jsonObject.getJSONObject("system");

                        int maxSecurityPercent = jsonObjectSystem.getInt("security_percent");

                        for (int i = 0; i < jsonArrayMembers.length(); i++) {
                            JSONObject jo = jsonArrayMembers.getJSONObject(i);
                            String id = jo.getString("id");
                            String text = jo.getString("text");
                            String image = jo.getString("image");
                            String time = jo.getString("time");
                            String safety = jo.getString("safety");
                            String is_regular = jo.getString("is_regular");
                            boolean isRegular = false;
                            if (is_regular.equals("true")){
                                isRegular = true;
                            }
                            int safePercent = 0;
                            if (!safety.equals("")){
                                safePercent = Integer.valueOf(safety);
                            }
                            datas.add(new Data(id, image, text, time, safePercent, maxSecurityPercent, isRegular));
                        }
                        dataAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(getContext(), "There is no selected camera system! Please select a system from profile page.", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + MainActivity.user_token);
                return headers;
            }
        };

        queue.add(stringRequest);
    }

    private void openDataDialog(final Data data) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.data_dialog);
        dialog.show();

        ImageView ivData = dialog.findViewById(R.id.ivDialog);
        TextView tvName = dialog.findViewById(R.id.tvName);

        Glide.with(getContext()).load(Urls.APP_URL + data.getImage()).into(ivData);
        tvName.setText(data.getName());

        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        final EditText etPersonName = dialog.findViewById(R.id.etPersonName);
        Button buttonOk = dialog.findViewById(R.id.buttonOk);
        Button buttonSuspicious= dialog.findViewById(R.id.buttonSuspicious);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String personName = etPersonName.getText().toString();
                setSecurity(data, personName, "yes", dialog);
            }
        });

        buttonSuspicious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSecurity(data, "", "no", dialog);
            }
        });
    }

    private void setSecurity(final Data data, final String personName, final String isSecure, final Dialog dialog) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.SET_SECURITY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getContext(),jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            getDatas();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERRIIX", error.toString());
                        Toast.makeText(getContext(),"Error!, Please try later" , Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + MainActivity.user_token);
                return headers;
            }

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("data_id", data.getId());
                params.put("person_name", personName);
                params.put("is_secure", isSecure);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildAdapterPosition(v);
        Data data = datas.get(position);
        openDataDialog(data);
    }
}
