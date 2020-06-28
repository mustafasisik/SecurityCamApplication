package com.securitycam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class BellActivity extends AppCompatActivity {

    WebView webView;
    Button buttonSafe, buttonSuspicious, buttonOpenDoor;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bell);

        FirebaseApp.initializeApp(this);

        Map<String, Object> map = new HashMap<>();
        map.put("bell", "off");
        reference.updateChildren(map);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.loadUrl("http://192.168.1.23/");

        buttonOpenDoor = findViewById(R.id.buttonOpenDoor);
        buttonSuspicious = findViewById(R.id.buttonSuspicious);
        buttonSafe = findViewById(R.id.buttonSafe);

        buttonOpenDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BellActivity.this, "Door Opened.", Toast.LENGTH_SHORT).show();
            }
        });
        buttonSuspicious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BellActivity.this, "Person and image saved s suspicious", Toast.LENGTH_SHORT).show();
            }
        });
        buttonSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BellActivity.this, "Person and image saved s safe", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
