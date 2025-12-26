package com.example.librarymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminReportsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_admin_reports);

        Button issued = findViewById(R.id.btnMonthlyIssued);
        Button returned = findViewById(R.id.btnMonthlyReturned);
        Button overdue = findViewById(R.id.btnOverdue);

        issued.setOnClickListener(v ->
                startActivity(new Intent(this, MonthlyIssuedReportActivity.class)));

        returned.setOnClickListener(v ->
                startActivity(new Intent(this, MonthlyReturnedReportActivity.class)));

        overdue.setOnClickListener(v ->
                startActivity(new Intent(this, OverdueReportActivity.class)));
    }
}
