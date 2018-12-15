package com.example.shivam.filelibrarycheck;

import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class Piechart extends AppCompatActivity {
    TextView presentView;
    TextView absentView;
    TextView percentageView;
    TextView totalView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart);
        presentView = (TextView)findViewById(R.id.textView4);
        absentView = (TextView)findViewById(R.id.textView5);
        totalView = (TextView)findViewById(R.id.textView3);
        percentageView = (TextView)findViewById(R.id.textView2);

        PieChart mPieChart = (PieChart) findViewById(R.id.piechart);
        int present = getIntent().getExtras().getInt("present");
        int total = getIntent().getExtras().getInt("total");
        float percentage= Math.round((float)present/total*100);
        String present_student = "Present : "+present;
        String absent_student = "Absent : "+(total-present);
        String total_student = "Total : "+total;
        String percentage_present = percentage+" %";
        mPieChart.addPieSlice(new PieModel("Present", present, Color.parseColor("#7CFC00")));
        mPieChart.addPieSlice(new PieModel("Absent",total-present , Color.parseColor("#696969")));

        mPieChart.startAnimation();
        presentView.setText(present_student);
        absentView.setText(absent_student);
        totalView.setText(total_student);
        percentageView.setText(percentage_present);

        mPieChart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(Piechart.this,AttendanceList.class);
                startActivity(i);
                return false;
            }
        });
    }
}
