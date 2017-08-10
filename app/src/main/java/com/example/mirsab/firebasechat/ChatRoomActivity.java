package com.example.mirsab.firebasechat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity {
    TextView tvMessage;
    EditText etMessage;
    Button btnSend;
    String username, serverName, tempKey, chatMessage, chatUsername;
    DatabaseReference root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        tvMessage = (TextView) findViewById(R.id.tvSingleMessage);
        etMessage = (EditText) findViewById(R.id.etMessage);
        btnSend = (Button) findViewById(R.id.btnSendMessage);

        username = getIntent().getExtras().get("username").toString();
        serverName = getIntent().getExtras().get("serverName").toString();

        root = FirebaseDatabase.getInstance().getReference().child(serverName);

        setTitle("Server - "+serverName);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<String, Object>();
                tempKey = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference messageRoot = root.child(tempKey);
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("username", username);
                map2.put("message",etMessage.getText().toString());
                messageRoot.updateChildren(map2);
            }
        });
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendChat(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendChat(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                appendChat(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void appendChat(DataSnapshot dataSnapshot){
        Iterator i = dataSnapshot.getChildren().iterator();
        while(i.hasNext()){
            chatMessage = (String) ((DataSnapshot)i.next()).getValue();
            chatUsername = (String) ((DataSnapshot)i.next()).getValue();
            tvMessage.append(chatUsername + " : " + chatMessage + "\n");
        }
    }
}
