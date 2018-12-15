package com.example.shivam.filelibrarycheck;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewAttendance extends AppCompatActivity {
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);
        tv = (TextView)findViewById(R.id.textView);
        String studentData = (String)getIntent().getExtras().getString("studentData");
        tv.setText(studentData);
    }
}
