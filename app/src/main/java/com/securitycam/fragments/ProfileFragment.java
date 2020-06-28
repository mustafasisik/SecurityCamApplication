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
import android.widget.LinearLayout;
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
import com.securitycam.adapters.SystemAdapter;
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

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private SystemAdapter systemAdapter;
    private TextView tvSystemCount, tvName, tvEmail;
    private ImageView ivProfile;
    private ArrayList<System> systems = new ArrayList<>();
    private RequestQueue queue;
    private Button buttonCreateSystem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = MySingleton.getInstance(getContext()).getRequestQueue();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setView(view);
        return view;
    }

    private void setView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        systemAdapter = new SystemAdapter(getContext(), systems, ProfileFragment.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(systemAdapter);
        tvSystemCount = view.findViewById(R.id.tvSystemCount);
        ivProfile = view.findViewById(R.id.ivProfile);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);

        getProfileInfo();

        buttonCreateSystem = view.findViewById(R.id.buttonCreateSystem);

        buttonCreateSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateDialog();
            }
        });
    }

    private void openCreateDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.create_system_dialog);

        final EditText etCode = dialog.findViewById(R.id.etCode);
        final EditText etName = dialog.findViewById(R.id.etName);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        Button buttonAdd= dialog.findViewById(R.id.buttonAdd);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etCode.getText().toString();
                String name = etName.getText().toString();
                if (!TextUtils.isEmpty(code) && !TextUtils.isEmpty(name)){
                    createSystem(name, code, dialog);
                }
            }
        });
        dialog.show();
    }

    private void createSystem(final String name, final String code, final Dialog dialog) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.CREATE_SYSTEM_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getContext(),jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERRII", error.toString());
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
                params.put("name", name);
                params.put("code", code);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void getProfileInfo() {
        systems.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Urls.PROFILE_URL, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    response = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObjectUser = jsonObject.getJSONObject("user");
                    JSONArray jsonArraySystems = jsonObject.getJSONArray("systems");
                    String name = jsonObjectUser.getString("first_name");
                    String surname = jsonObjectUser.getString("last_name");
                    String email = jsonObjectUser.getString("username");

                    tvName.setText(name + " " + surname);
                    tvEmail.setText(email);

                    if (jsonArraySystems.length() > 0) {
                        for (int i = 0; i < jsonArraySystems.length(); i++) {
                            JSONObject jo = jsonArraySystems.getJSONObject(i);
                            String id = jo.getString("id");
                            String code = jo.getString("code");
                            String system_name = jo.getString("name");
                            String is_selected = jo.getString("is_selected");
                            int membership = jo.getInt("membership");

                            boolean isSelected = false, isHost = false;
                            if (is_selected.equals("true"))
                                isSelected = true;
                            if (membership == 1)
                                isHost = true;

                            systems.add(new System(id, system_name, code, isSelected, isHost));
                        }
                        systemAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "No camera system! Please create one with your SYSTEM CODE or wait a host to add you as a member.", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("RESPONSEEERR", error.toString());
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

    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildAdapterPosition(v);
        System system = systems.get(position);
        openSelectSystemDialog(system);
    }

    private void openSelectSystemDialog(final System system) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.select_system_dialog);

        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        Button buttonAdd= dialog.findViewById(R.id.buttonAdd);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SYSCODE", system.getCode());
                selectSystem(system, dialog);
            }
        });
        dialog.show();
    }

    private void selectSystem(final System system, final Dialog dialog) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.SELECT_SYSTEM_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getContext(),jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            getProfileInfo();
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
                params.put("code", system.getCode());
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
