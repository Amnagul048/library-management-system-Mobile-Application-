package com.example.librarymanagementsystem;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class AdminStatsActivity extends AppCompatActivity {

    private TextView txtUsers, txtBooks, txtIssued, txtReturned, txtOverdue;

    private static final String DB_URL =
            "https://librarymanagementsystem-6a1a9-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_stats);

        txtUsers = findViewById(R.id.txtUsers);
        txtBooks = findViewById(R.id.txtBooks);
        txtIssued = findViewById(R.id.txtIssued);
        txtReturned = findViewById(R.id.txtReturned);
        txtOverdue = findViewById(R.id.txtOverdue);

        FirebaseDatabase db = FirebaseDatabase.getInstance(DB_URL);

        loadUsers(db);
        loadBooks(db);
        loadIssues(db);
    }

    private void loadUsers(FirebaseDatabase db) {
        db.getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        txtUsers.setText("Total Users: " + snapshot.getChildrenCount());
                    }
                    @Override public void onCancelled(DatabaseError error) { }
                });
    }

    private void loadBooks(FirebaseDatabase db) {
        db.getReference("books")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        txtBooks.setText("Total Books: " + snapshot.getChildrenCount());
                    }
                    @Override public void onCancelled(DatabaseError error) { }
                });
    }

    private void loadIssues(FirebaseDatabase db) {
        db.getReference("issues")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        int issued = 0;
                        int returned = 0;
                        int overdue = 0;

                        long now = System.currentTimeMillis();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String status = ds.child("status").getValue(String.class);
                            Long due = ds.child("dueDate").getValue(Long.class);

                            if ("returned".equalsIgnoreCase(status)) {
                                returned++;
                            } else {
                                issued++;
                                if (due != null && now > due) overdue++;
                            }
                        }

                        txtIssued.setText("Currently Issued: " + issued);
                        txtReturned.setText("Returned: " + returned);
                        txtOverdue.setText("Overdue: " + overdue);
                    }

                    @Override public void onCancelled(DatabaseError error) { }
                });
    }
}
