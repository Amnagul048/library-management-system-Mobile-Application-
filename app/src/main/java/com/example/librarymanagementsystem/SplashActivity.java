package com.example.librarymanagementsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    // Use the SAME correct DB URL you used in Login/Register
    private static final String DB_URL = "https://librarymanagementsystem-6a1a9-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance(DB_URL).getReference("users");

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String uid = user.getUid();

        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    auth.signOut();
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                    return;
                }

                String role = snapshot.child("role").getValue(String.class);
                if ("librarian".equalsIgnoreCase(role)) {
                    startActivity(new Intent(SplashActivity.this, LibrarianHomeActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, StudentHomeActivity.class));
                }
                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                auth.signOut();
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
