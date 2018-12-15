package com.example.shivam.filelibrarycheck;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;
import com.sromku.simple.storage.helpers.OrderType;
import com.yalantis.phoenix.PullToRefreshView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    List<File> files;
    Storage storage = null;
    PullToRefreshView mPullToRefreshView;
    ArrayAdapter adapter;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                       // Toast.makeText(MainActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        startActivity(intent);
                        SnackbarManager.show(
                                Snackbar.with(MainActivity.this)
                                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                                        .swipeToDismiss(true)
                                        .text("File List Refreshed !"));
                    }
                }, 2000);
            }
        });

        if (SimpleStorage.isExternalStorageWritable()) {
            storage = SimpleStorage.getExternalStorage();
        }
        else {
            storage = SimpleStorage.getInternalStorage(this);
        }
        boolean dirExist = storage.isDirectoryExists("Attendance/Student_Data");
        if(!dirExist) {
            storage.createDirectory("Attendance/Student_Data");
        }
        List<String> fileName = new ArrayList<>();
        try {
            files = storage.getFiles("Attendance/Student_Data", OrderType.DATE);
            int i = 0;
            for (File file : files) {
                fileName.add(files.get(i).getName());
                i++;
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,fileName);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String  myFile=files.get(position).getName();
                //String myDir = files.get(position).getParent();
                //Toast.makeText(MainActivity.this,files.get(position).getParentFile().getParentFile().getName().toString(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),markAttendance.class);
                String studentData = storage.readTextFile("Attendance/Student_data", myFile);
                //Toast.makeText(MainActivity.this, studentData, Toast.LENGTH_SHORT).show();
                //i.putExtra("studentData",studentData);
                i.putExtra("path",files.get(position).getAbsolutePath());
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_search:
                logoutUser();
                return true;
            case R.id.create_data:
                createData();
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void createData() {
        Intent intent = new Intent(MainActivity.this, CreateData.class);
        startActivity(intent);
        finish();

    }
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
