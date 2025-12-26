package com.example.librarymanagementsystem;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librarymanagementsystem.adapters.IssueHistoryAdapter;
import com.example.librarymanagementsystem.models.Issue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class StudentHistoryActivity extends AppCompatActivity {

    private TextView txtStatus;
    private RecyclerView rvHistory;

    private final List<Issue> list = new ArrayList<>();
    private IssueHistoryAdapter adapter;

    private static final String DB_URL =
            "https://librarymanagementsystem-6a1a9-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_history);

        txtStatus = findViewById(R.id.txtStatus);
        rvHistory = findViewById(R.id.rvHistory);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IssueHistoryAdapter(list);
        rvHistory.setAdapter(adapter);

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            txtStatus.setText("Not logged in");
            return;
        }

        loadHistory(uid);
    }

    private void loadHistory(String uid) {
        txtStatus.setText("Loading history...");

        DatabaseReference issuesRef =
                FirebaseDatabase.getInstance(DB_URL).getReference("issues");

        Query q = issuesRef.orderByChild("studentUid").equalTo(uid);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Issue it = ds.getValue(Issue.class);
                    if (it != null) {
                        it.id = ds.getKey();
                        list.add(it);
                    }
                }

                adapter.notifyDataSetChanged();
                txtStatus.setText(list.isEmpty()
                        ? "No history"
                        : "Total records: " + list.size());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                txtStatus.setText("Error: " + error.getMessage());
            }
        });
    }
}
