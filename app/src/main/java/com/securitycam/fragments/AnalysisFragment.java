package com.securitycam.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.securitycam.MainActivity;
import com.securitycam.R;
import com.securitycam.models.Data;
import com.securitycam.utils.MySingleton;
import com.securitycam.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalysisFragment extends Fragment {

    private float[] yData = {89, 11};
    private String[] xData = {"Safe", "Suspicious"};
    private PieChart pieChart;
    private BarChart chart;
    private RequestQueue queue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = MySingleton.getInstance(getContext()).getRequestQueue();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);
        setView(view);
        return view;
    }

    private void setView(View view) {
        chart = view.findViewById(R.id.barChart);

        Description desc ;
        Legend L;

        L = chart.getLegend();
        desc = chart.getDescription();
        desc.setText(""); // this is the weirdest way to clear something!!
        L.setEnabled(false);


        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        XAxis xAxis = chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);


        leftAxis.setTextSize(10f);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.setDescription(desc);
        chart.setDrawValueAboveBar(true);
        // set data

        // pie chart

        pieChart = view.findViewById(R.id.pieChart);

        pieChart.setDescription(desc);
        pieChart.setRotationEnabled(true);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.BLUE);
        //pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("You are safe");
        pieChart.setCenterTextSize(15);
        pieChart.setCenterTextColor(getResources().getColor(R.color.colorPrimary));
        //pieChart.setDrawEntryLabels(true);
        //pieChart.setEntryLabelTextSize(20);

        getAnalysis();
    }

    private void getAnalysis() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Urls.ANALYSIS_URL, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    response = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                    Log.d("RESPNN", response);
                    JSONObject jsonObject = new JSONObject(response);
                    int selected_membership_count = jsonObject.getInt("selected_membership_count");

                    if (selected_membership_count > 0){
                        int safetyAvg = jsonObject.getInt("safety_average");
                        int suspiciousAvg = jsonObject.getInt("suspicious_average");
                        JSONArray joDatas = jsonObject.getJSONArray("datas");

                        yData[0] = safetyAvg;
                        yData[1] = suspiciousAvg;
                        addDataSet();

                        int[] values = new int[7];
                        for (int i = 0; i < joDatas.length(); i++) {
                            JSONObject joData = joDatas.getJSONObject(i);
                            String safety = joData.getString("safety");
                            int safePercent = 0;
                            if (!safety.equals("")){
                                safePercent = Integer.valueOf(safety);
                            }
                            values[i] = safePercent;
                        }

                        BarData data = new BarData(setData(values));
                        data.setBarWidth(0.6f); // set custom bar width
                        chart.setData(data);

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

    private BarDataSet setData(int[] values) {

        ArrayList<BarEntry> entries = new ArrayList<>();

        entries.add(new BarEntry(1, values[6]));
        entries.add(new BarEntry(2, values[5]));
        entries.add(new BarEntry(3, values[4]));
        entries.add(new BarEntry(4, values[3]));
        entries.add(new BarEntry(5, values[2]));
        entries.add(new BarEntry(6, values[1]));
        entries.add(new BarEntry(7, values[0]));


        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.rgb(155, 155, 155));
        set.setValueTextColor(Color.rgb(155,155,155));

        return set;
    }

    private void addDataSet() {
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for(int i = 0; i < yData.length; i++){
            yEntrys.add(new PieEntry(yData[i] , i));
        }

        for(int i = 1; i < xData.length; i++){
            xEntrys.add(xData[i]);
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Security Status");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.colorGreen));
        colors.add(getResources().getColor(R.color.colorRed));

        pieDataSet.setColors(colors);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
