package com.example.storyapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.storyapp.MainActivity;
import com.example.storyapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText name, pass;
    DatabaseReference db;
    Button login, signup;
    TextView usernotfound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = findViewById(R.id.username);
        pass = findViewById(R.id.pass);
        login = findViewById(R.id.button);
        signup = findViewById(R.id.button2);
        usernotfound = findViewById(R.id.notfound);
        db = FirebaseDatabase.getInstance().getReference().child("Users");

        SharedPreferences sharedPreferences1 = getSharedPreferences("getiffirst", MODE_PRIVATE);
        String getiffirst = sharedPreferences1.getString("getiffirsttime", "");
        if (getiffirst.equals("No")) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(name.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Enter Username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.orderByChild("Username").equalTo(name.getText().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    usernotfound.setText("");
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                        if (dataSnapshot.child("Password").getValue().toString().equals(pass.getText().toString())) {
                                            SharedPreferences sharedPreferences = getSharedPreferences("name", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("uname", name.getText().toString());
                                            editor.apply();
                                            Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                            finish();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        } else
                                            Toast.makeText(LoginActivity.this, "Login Failed! Entered Name or Password is invalid", Toast.LENGTH_SHORT).show();

                                } else {
                                    usernotfound.setText("User Doesn't Exist! Please Signup");
                                    name.setText("");
                                    pass.setText("");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }
}