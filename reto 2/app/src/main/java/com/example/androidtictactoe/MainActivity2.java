package com.example.androidtictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    ListView mListView;
    Button mButton;

    List<String> rooms;

    String playerName="";
    String roomName="";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference roomReference;
    DatabaseReference roomsListReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        firebaseDatabase = FirebaseDatabase.getInstance();
        // Get the player name and assign his room name to the player name
        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");
        roomName = playerName;

        mListView = findViewById(R.id.ListView);
        mButton = findViewById(R.id.button);

        // All existing available rooms
        rooms = new ArrayList<>();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create room and add yourself as players
                mButton.setText("Creating room");
                mButton.setEnabled(false);
                roomName = playerName;
                roomReference = firebaseDatabase.getReference("rooms/" + roomName + "/player1");
                addRoomEventListener();
                roomReference.setValue(playerName);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Join an existing room and add yourself as player2
                roomName = rooms.get(i);
                roomReference = firebaseDatabase.getReference("rooms/" + roomName + "/player2");
                addRoomEventListener();
                roomReference.setValue(playerName);
            }
        });

        addRoomsEventListener();
    }

    private void addRoomEventListener(){
        roomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Join the room
                mButton.setText("Create room");
                mButton.setEnabled(true);
                Intent intent = new Intent(getApplicationContext(), MainActivity3.class);
                intent.putExtra("roomName", roomName);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error
                mButton.setText("Create room");
                mButton.setEnabled(true);
                Toast.makeText(MainActivity2.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addRoomsEventListener(){
        roomsListReference = firebaseDatabase.getReference("rooms");
        roomsListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Show list of rooms
                rooms.clear();
                Iterable<DataSnapshot> roomsL = snapshot.getChildren();
                for(DataSnapshot snapshot1 : roomsL){
                    rooms.add(snapshot1.getKey());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity2.this,
                            android.R.layout.simple_list_item_1, rooms);
                    mListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error - nothing
            }
        });
    }
}