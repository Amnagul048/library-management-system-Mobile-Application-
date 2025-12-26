package com.example.librarymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class StudentHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        Button btnViewBooks = findViewById(R.id.btnViewBooks);
        Button btnMyIssuedBooks = findViewById(R.id.btnMyIssuedBooks);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnHistory = findViewById(R.id.btnHistory);

        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(StudentHomeActivity.this, StudentHistoryActivity.class))
        );
        btnViewBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentHomeActivity.this, ViewBooksActivity.class);
                i.putExtra("isLibrarian", false);
                startActivity(i);
            }
        });

        btnMyIssuedBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentHomeActivity.this, MyIssuedBooksActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(StudentHomeActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
    }
}
