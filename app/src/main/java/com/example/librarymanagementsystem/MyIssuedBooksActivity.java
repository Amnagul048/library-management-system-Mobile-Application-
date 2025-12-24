package com.example.librarymanagementsystem;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librarymanagementsystem.adapters.IssueAdapter;
import com.example.librarymanagementsystem.models.Issue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MyIssuedBooksActivity extends AppCompatActivity {

    private RecyclerView rvIssues;
    private TextView txtStatus;

    private IssueAdapter adapter;
    private final List<Issue> issueList = new ArrayList<>();

    private DatabaseReference issuesRef;

    private static final String DB_URL =
            "https://librarymanagementsystem-6a1a9-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_issued_books);

        txtStatus = findViewById(R.id.txtStatus);
        rvIssues = findViewById(R.id.rvIssues);

        rvIssues.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IssueAdapter(issueList);
        rvIssues.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            txtStatus.setText("Not logged in.");
            return;
        }

        issuesRef = FirebaseDatabase.getInstance(DB_URL).getReference("issues");
        loadMyIssues(user.getUid());
    }

    private void loadMyIssues(String myUid) {
        txtStatus.setText("Loading...");

        // Query by studentUid
        Query q = issuesRef.orderByChild("studentUid").equalTo(myUid);

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                issueList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Issue it = ds.getValue(Issue.class);
                    if (it != null) {
                        it.id = ds.getKey();
                        // Only show currently issued (not returned)
                        String st = (it.status == null) ? "issued" : it.status;
                        if ("issued".equalsIgnoreCase(st)) {
                            issueList.add(it);
                        }
                    }
                }

                adapter.notifyDataSetChanged();

                if (issueList.isEmpty()) {
                    txtStatus.setText("No issued books.");
                } else {
                    txtStatus.setText("Issued books: " + issueList.size());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                txtStatus.setText("Error: " + error.getMessage());
            }
        });
    }
}
