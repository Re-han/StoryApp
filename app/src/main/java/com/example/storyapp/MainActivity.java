package com.example.storyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.storyapp.Login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView text;
    RecyclerView rv;
    FloatingActionButton fab, logout;
    String name = "", email = "", location = "";
    List<StoryModel> models = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.text);
        rv = findViewById(R.id.rv);
        fab = findViewById(R.id.fab);
        logout = findViewById(R.id.logout);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Story");

        SharedPreferences sharedPreferences = getSharedPreferences("name", MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = getSharedPreferences("getiffirst", MODE_PRIVATE);
        String user = sharedPreferences.getString("uname", "");
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("getiffirsttime", "No");
        editor.apply();
        setTitle("Welcome " + user);

        FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("Username").equalTo(user)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot d : snapshot.getChildren()) {
                                name = d.child("Username").getValue().toString();
                                email = d.child("Email").getValue().toString();
                                location = d.child("Location").getValue().toString();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CreateStory.class);
                i.putExtra("uname", user);
                i.putExtra("email", email);
                i.putExtra("location", location);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("name", MODE_PRIVATE);
                SharedPreferences sharedPreferences1 = getSharedPreferences("getiffirst", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor.clear().apply();
                editor1.clear().apply();
                finish();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    models.clear();
                    for (DataSnapshot d : snapshot.getChildren()) {
//                        Toast.makeText(MainActivity.this, d.getValue().toString(), Toast.LENGTH_SHORT).show();
                        models.add(new StoryModel(d.child("Username").getValue().toString(), d.child("Email").getValue().toString()
                                , d.child("Story").getValue().toString(), d.child("Location").getValue().toString()));
                    }
                    rv.setAdapter(new StoryAdapter(models));
                    rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}