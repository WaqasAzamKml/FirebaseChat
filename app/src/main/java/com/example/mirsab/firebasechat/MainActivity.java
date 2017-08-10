package com.example.mirsab.firebasechat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ListView lvServers;
    EditText etServerName;
    Button btnCreateServer;
    String username, serverName;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> serversList = new ArrayList<>();
    DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvServers = (ListView) findViewById(R.id.lvServers);
        etServerName = (EditText) findViewById(R.id.etServerName);
        btnCreateServer = (Button) findViewById(R.id.btnCreateServer);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, serversList);
        lvServers.setAdapter(arrayAdapter);

        username = "guest";
        requestUsername();

        btnCreateServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(etServerName.getText().toString(),"");
                root.updateChildren(map);
            }
        });

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                while(i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                serversList.clear();
                serversList.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        lvServers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ChatRoomActivity.class);
                serverName = ((TextView)view).getText().toString();
                i.putExtra("serverName", serverName);
                i.putExtra("username", username);
                startActivity(i);
                finish();
            }
        });
    }

    private void requestUsername(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter User Name");
        final EditText etUsername = new EditText(this);
        builder.setView(etUsername);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                username = etUsername.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                requestUsername();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
}
