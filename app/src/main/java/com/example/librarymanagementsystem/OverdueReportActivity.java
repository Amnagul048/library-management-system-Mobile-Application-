package com.example.librarymanagementsystem;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class OverdueReportActivity extends AppCompatActivity {

    private static final String DB_URL =
            "https://librarymanagementsystem-6a1a9-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.simple_report);

        TextView txt = findViewById(R.id.txtReport);
        txt.setText("Loading...");

        long now = System.currentTimeMillis();

        FirebaseDatabase.getInstance(DB_URL)
                .getReference("issues")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        int overdue = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String status = ds.child("status").getValue(String.class);
                            Long due = ds.child("dueDate").getValue(Long.class);

                            if ("issued".equalsIgnoreCase(status) &&
                                    due != null && now > due) {
                                overdue++;
                            }
                        }
                        txt.setText("Overdue books: " + overdue);
                    }

                    @Override public void onCancelled(DatabaseError error) {
                        txt.setText(error.getMessage());
                    }
                });
    }
}
