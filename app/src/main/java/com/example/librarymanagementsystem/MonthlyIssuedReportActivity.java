package com.example.librarymanagementsystem;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.Calendar;

public class MonthlyIssuedReportActivity extends AppCompatActivity {

    private static final String DB_URL =
            "https://librarymanagementsystem-6a1a9-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.simple_report);

        TextView txt = findViewById(R.id.txtReport);
        txt.setText("Loading...");

        DatabaseReference ref = FirebaseDatabase.getInstance(DB_URL)
                .getReference("issues");

        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH);
        int year = now.get(Calendar.YEAR);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Long issueDate = ds.child("issueDate").getValue(Long.class);
                    String status = ds.child("status").getValue(String.class);

                    if (issueDate == null || !"issued".equalsIgnoreCase(status)) continue;

                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(issueDate);

                    if (c.get(Calendar.MONTH) == month &&
                            c.get(Calendar.YEAR) == year) {
                        count++;
                    }
                }
                txt.setText("Books issued this month: " + count);
            }

            @Override public void onCancelled(DatabaseError error) {
                txt.setText(error.getMessage());
            }
        });
    }
}
