package com.securitycam.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.securitycam.MainActivity;
import com.securitycam.R;
import com.securitycam.RegisterActivity;
import com.securitycam.adapters.PersonAdapter;
import com.securitycam.adapters.RequestAdapter;
import com.securitycam.adapters.UserAdapter;
import com.securitycam.models.System;
import com.securitycam.models.User;
import com.securitycam.utils.MySingleton;
import com.securitycam.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MembersFragment extends Fragment {


    private ArrayList<User> members = new ArrayList<>();
    private ArrayList<User> people = new ArrayList<>();

    private UserAdapter userAdapter;
    private PersonAdapter personAdapter;

    private RecyclerView rvMembers, rvPeople;

    private Button buttonAddMember;
    private RequestQueue queue;
    private boolean isHost = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = MySingleton.getInstance(getContext()).getRequestQueue();
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_members, container, false);
        setView(view);
        return view;
    }

    private void setView(View view) {


        rvMembers = view.findViewById(R.id.rvMembers);
        rvMembers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        userAdapter = new UserAdapter(getContext(), members);
        rvMembers.setAdapter(userAdapter);

        rvPeople = view.findViewById(R.id.rvPeople);
        rvPeople.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        personAdapter = new PersonAdapter(getContext(), people);
        rvPeople.setAdapter(personAdapter);

        userAdapter.notifyDataSetChanged();

        personAdapter.notifyDataSetChanged();

        buttonAddMember = view.findViewById(R.id.buttonAddMember);


        buttonAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMemberDialog();
            }
        });

        getPeople();
    }

    private void getPeople() {
        people.clear();
        members.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Urls.MEMBERS_URL, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                Log.d("RESXY", response);
                try {
                    response = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("knownpeople")){
                        JSONArray jsonArrayKnownPeople = jsonObject.getJSONArray("knownpeople");
                        for (int i = 0; i < jsonArrayKnownPeople.length(); i++) {
                            JSONObject jo = jsonArrayKnownPeople.getJSONObject(i);
                            String user_email = jo.getString("user_email");
                            JSONObject joPeople = jo.getJSONObject("data");
                            String id = joPeople.getString("id");
                            String text = joPeople.getString("text");
                            String image = joPeople.getString("image");
                            people.add(new User("", text, image, 0));
                        }
                        personAdapter.notifyDataSetChanged();
                    }
                    if (jsonObject.has("members")){
                        JSONArray jsonArrayMembers = jsonObject.getJSONArray("members");
                        for (int i = 0; i < jsonArrayMembers.length(); i++) {
                            JSONObject jo = jsonArrayMembers.getJSONObject(i);
                            String email = jo.getString("name");
                            int membership = jo.getInt("membership");
                            if (membership == 1 && email.equals(MainActivity.email)){
                                isHost = true;
                                buttonAddMember.setEnabled(true);
                                buttonAddMember.setVisibility(View.VISIBLE);
                            }
                            members.add(new User("", email, "", membership));
                        }
                        userAdapter.notifyDataSetChanged();
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

    private void addMemberDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.add_member_dialog);

        final EditText etEmail = dialog.findViewById(R.id.etEmail);
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
                String email = etEmail.getText().toString();
                if (!TextUtils.isEmpty(email)){
                    addMember(email, dialog);
                }
            }
        });
        dialog.show();
    }

    private void addMember(final String email, final Dialog dialog) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.ADD_MEMBER_URL,
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

                        getPeople();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", error.toString());
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
                params.put("email", email);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
