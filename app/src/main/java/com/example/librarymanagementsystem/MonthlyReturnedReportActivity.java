package com.example.librarymanagementsystem;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.Calendar;

public class MonthlyReturnedReportActivity extends AppCompatActivity {

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
                    String status = ds.child("status").getValue(String.class);
                    Long returnDate = ds.child("returnDate").getValue(Long.class);

                    if (!"returned".equalsIgnoreCase(status) || returnDate == null)
                        continue;

                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(returnDate);

                    if (c.get(Calendar.MONTH) == month &&
                            c.get(Calendar.YEAR) == year) {
                        count++;
                    }
                }

                txt.setText("Books returned this month: " + count);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                txt.setText("Error: " + error.getMessage());
            }
        });
    }
}
