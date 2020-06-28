package com.securitycam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.FirebaseApp;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.securitycam.models.System;
import com.securitycam.services.MyService;
import com.securitycam.utils.MySingleton;
import com.securitycam.utils.Urls;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static SpaceNavigationView spaceNavigationView;
    private NavController navController;

    public static ArrayList<System> systems = new ArrayList<>();
    public static System system;
    public static String user_token, email, name, surname;
    private RequestQueue queue;
    public static boolean isWorking;


    @Override
    protected void onStart() {
        super.onStart();
        isWorking = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isWorking = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isWorking = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isWorking = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isWorking = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = MySingleton.getInstance(this).getRequestQueue();

        startService(new Intent(this, MyService.class));

        systems.clear();
        FirebaseApp.initializeApp(this);
        user_token = getIntent().getStringExtra("user_token");

        spaceNavigationView = findViewById(R.id.spaceNavigationView);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);

        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.home));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.analysis));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.people));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.profile));
        spaceNavigationView.setCentreButtonColor(Color.RED);
        spaceNavigationView.setInActiveSpaceItemColor(Color.WHITE);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                navController.navigate(R.id.navigation_camera);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex) {
                    case 0:
                        navController.navigate(R.id.navigation_home);
                        break;
                    case 1:
                        navController.navigate(R.id.navigation_analysis);
                        break;
                    case 2:
                        navController.navigate(R.id.navigation_members);
                        break;
                    case 3:
                        navController.navigate(R.id.navigation_profile);
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                switch (itemIndex) {
                    case 0:
                        navController.navigate(R.id.navigation_home);
                        break;
                    case 1:
                        navController.navigate(R.id.navigation_analysis);
                        break;
                    case 2:
                        navController.navigate(R.id.navigation_members);
                        break;
                    case 3:
                        navController.navigate(R.id.navigation_profile);
                        break;
                }
            }
        });
        spaceNavigationView.hideAllBadges();

        getProfileInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit_profile) {
            return true;
        } else if (id == R.id.action_logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
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
                    name = jsonObjectUser.getString("first_name");
                    surname = jsonObjectUser.getString("last_name");
                    email = jsonObjectUser.getString("username");


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
                        if (isSelected){
                            MainActivity.this.getSupportActionBar().setSubtitle(system_name);
                        }
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + MainActivity.user_token);
                return headers;
            }
        };

        queue.add(stringRequest);
    }

}
