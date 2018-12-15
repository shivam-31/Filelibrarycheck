package com.example.shivam.filelibrarycheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Handler;
import ru.katso.livebutton.LiveButton;

public class markAttendance extends AppCompatActivity {
    WifiP2pManager mManager;
    String mac;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    ListView listview;
    LiveButton done;
    List<String> studentNAME;
    List<String> student_mac_list;
    List<String> student_data_list;
    String finalResult = "";
    StringBuilder stringBuilder;
    Handler h;
    String server_URL_INSERT;
    int delay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
//*******************************************************************************************
        // Create an intent filter and add the same intents that your broadcast receiver checks for

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // Create an intent filter and add the same intents that your broadcast receiver checks for
        //*******************************************************************************************

        done = (LiveButton) findViewById(R.id.button);
        String path = (String)getIntent().getExtras().getString("path");
        BufferedReader br = null;
        String line = "";
        mac = new String();
        String cvsSplitBy = ",";
        server_URL_INSERT = "http://192.168.43.44:8080/file1.php";

        student_data_list= new ArrayList<>();
        student_mac_list = new ArrayList<>();
        studentNAME = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(path));
            while ((line = br.readLine()) != null) {
                String[] student_data = line.split(cvsSplitBy);
                student_data_list.add("   "+student_data[0]+"  "+"  "+student_data[1]+"\n"+"  MAC : "+student_data[2] );
                studentNAME.add(student_data[1]);
                student_mac_list.add(student_data[2]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        listview = (ListView)findViewById(R.id.listView2);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.checkedlist, student_data_list);
        listview.setAdapter(adapter);
        listview.setItemsCanFocus(false);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //listview.setItemChecked(1,true);

/**
 * **********************************************************************
 */





        h = new Handler();
        delay = 5000;

        h.postDelayed(new Runnable() {
            public void run() {
                {
                    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                 //           Toast.makeText(markAttendance.this, "Student Discovery Started !", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            Toast.makeText(markAttendance.this, "Discovery Unsuccessful !", Toast.LENGTH_LONG).show();
                        }
                    });

                    mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList peers) {

                            for (WifiP2pDevice device : peers.getDeviceList()) {
                               // Toast.makeText(markAttendance.this, device+"", Toast.LENGTH_SHORT).show();
                                    if (student_mac_list.contains(device.deviceAddress)){
                                        int position=0 ;
                                        for(int i=0 ; i < student_mac_list.size();i++){
                                            if(student_mac_list.get(i).equals(device.deviceAddress)){
                                                position = i;
                                            }
                                        }
                                        listview.setItemChecked(position,true);
                                    }
                            }
                        }
                    });
                }
                h.postDelayed(this, delay);
            }
        }, delay);

/**
 * **********************************************************************
 */



        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stringBuilder = new StringBuilder();
               SparseBooleanArray myarray = listview.getCheckedItemPositions();
                int present = listview.getCheckedItemCount();
                int total = listview.getCount();
                for (int i=0 ; i <listview.getAdapter().getCount(); i++)
                {
                    if(myarray.get(i))
                    {stringBuilder.append(studentNAME.get(i)+"--PRESENT"+"\n");
                        System.out.println( studentNAME.get(i)+"----"+myarray.get(i));}
                    else
                    { System.out.println( studentNAME.get(i) +"----"+myarray.get(i));
                        stringBuilder.append( studentNAME.get(i)+"--ABSENT"+"\n" );}
                }
                finalResult = stringBuilder.toString();
                insert(finalResult,finalResult);
                long time= System.currentTimeMillis();
                String timestr = String.valueOf(time);
                String FileName = timestr+".txt";
                Storage storage = SimpleStorage.getExternalStorage();
                storage.createDirectory("Attendance/Attendance_Record");
                //storage.deleteFile("Attendance/Attendance_Record", "finalResult.txt");
                storage.createFile("Attendance/Attendance_Record", FileName,finalResult);
                Intent piechart = new Intent(getApplicationContext(),Piechart.class);
                piechart.putExtra("present",present);
                piechart.putExtra("total",total);
                startActivity(piechart);
            }
        });
    }
    void insert(final String name , final String email){
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, server_URL_INSERT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(markAttendance.this, response + "", Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(markAttendance.this, "Error !!", Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", name);
                    params.put("email", email);
                    return params;
                }
            };
            MySingleton.getInstance(markAttendance.this).addToRequestQueue(stringRequest);
        }
    }
}
