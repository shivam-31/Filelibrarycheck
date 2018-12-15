package com.example.shivam.filelibrarycheck;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateData extends AppCompatActivity {
    Button btnSelectData;
    Button btnFetchFile;
    EditText etBatch;
    EditText etBranch;
    EditText etSection;
    String batch;
    String section;
    String branch;
    String fileNAME;
    String server_URL_RETRIEVE = "http://192.168.43.44:8080/android_login_api/studentdata.php";
    StringBuilder stringBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_data);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        etBatch = (EditText)findViewById(R.id.batch);
        etBranch = (EditText)findViewById(R.id.branch);
        etSection = (EditText)findViewById(R.id.section);
        btnFetchFile = (Button)findViewById(R.id.btnFetch);
        btnSelectData = (Button)findViewById(R.id.btnSelectStudentData);
        btnSelectData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateData.this,MainActivity.class);
                startActivity(i);
            }
        });
        btnFetchFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                branch = etBranch.getText().toString();
                batch = etBatch.getText().toString();
                section = etSection.getText().toString();
                fileNAME = batch+"-"+branch+"-"+section+".txt";
                if(branch.isEmpty() || section.isEmpty() || section.isEmpty())
                    Toast.makeText(CreateData.this, "Fields are empty !", Toast.LENGTH_SHORT).show();
                else{
                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, server_URL_RETRIEVE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    showJson(response,fileNAME);

                                    //Toast.makeText(MainActivity.this, response+"", Toast.LENGTH_SHORT).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(CreateData.this, "Error !!", Toast.LENGTH_LONG).show();
                                }
                            })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("batch", batch);
                            params.put("branch",branch);
                            params.put("branch",section);
                            return params;
                        }
                    };
                    MySingleton.getInstance(CreateData.this).addToRequestQueue(stringRequest2);

                }
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_data_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
    public void showJson(String response , String filename ){

        stringBuilder = new StringBuilder();
        try {
            Toast.makeText(this, response+"", Toast.LENGTH_SHORT).show();
            JSONObject jsonobject = new JSONObject(response);
            JSONArray result = jsonobject.getJSONArray("result");
            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String name = jo.getString("name");
                String macaddr = jo.getString("macaddr");
                String str = "1,"+name+","+macaddr+"\n";
                stringBuilder.append(str);
            }
         }
        catch (JSONException e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Storage storage = SimpleStorage.getExternalStorage();
        storage.createDirectory("Attendance/Student_Data");
        storage.createFile("Attendance/Student_Data",filename,stringBuilder.toString());
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                // app icon in action bar clicked; goto parent activity.
                //this.finish();
                return true;
            case R.id.ToMain:
                goToMain();
                return  true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void goToMain() {

        Intent intent = new Intent(CreateData.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
