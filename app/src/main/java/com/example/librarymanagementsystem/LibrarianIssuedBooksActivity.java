package com.example.librarymanagementsystem;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librarymanagementsystem.adapters.IssueAdapterLibrarian;
import com.example.librarymanagementsystem.models.Issue;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibrarianIssuedBooksActivity extends AppCompatActivity {

    private TextView txtStatus;
    private RecyclerView rvIssued;

    private final List<Issue> issueList = new ArrayList<>();
    private IssueAdapterLibrarian adapter;

    private DatabaseReference issuesRef;
    private DatabaseReference booksRef;

    private static final String DB_URL =
            "https://librarymanagementsystem-6a1a9-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_issued_books);

        txtStatus = findViewById(R.id.txtStatus);
        rvIssued = findViewById(R.id.rvIssued);

        rvIssued.setLayoutManager(new LinearLayoutManager(this));

        adapter = new IssueAdapterLibrarian(issueList, issue -> {
            if (issue == null || issue.id == null || issue.bookId == null) return;
            returnBook(issue.id, issue.bookId);
        });

        rvIssued.setAdapter(adapter);

        FirebaseDatabase db = FirebaseDatabase.getInstance(DB_URL);
        issuesRef = db.getReference("issues");
        booksRef = db.getReference("books");

        loadIssuedOnly();
    }

    private void setStatus(String s) {
        txtStatus.setText(s);
    }

    private void loadIssuedOnly() {
        setStatus("Loading issued books...");

        // For MVP: load all issues and show only status == "issued"
        issuesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                issueList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Issue it = ds.getValue(Issue.class);
                    if (it != null) {
                        it.id = ds.getKey();
                        String st = (it.status == null) ? "issued" : it.status;
                        if ("issued".equalsIgnoreCase(st)) {
                            issueList.add(it);
                        }
                    }
                }

                adapter.notifyDataSetChanged();

                if (issueList.isEmpty()) {
                    setStatus("No issued books.");
                } else {
                    setStatus("Issued books: " + issueList.size());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                setStatus("Error: " + error.getMessage());
            }
        });
    }

    private void returnBook(String issueId, String bookId) {
        setStatus("Returning...");

        long now = System.currentTimeMillis();

        // 1) Mark issue returned
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "returned");
        updates.put("returnDate", now);

        issuesRef.child(issueId).updateChildren(updates, (err, ref) -> {
            if (err != null) {
                setStatus("Return failed: " + err.getMessage());
                return;
            }

            // 2) Increment book availability (transaction-safe)
            booksRef.child(bookId).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData currentData) {
                    Object availObj = currentData.child("availableCopies").getValue();
                    long available = (availObj instanceof Long) ? (Long) availObj : 0L;

                    currentData.child("availableCopies").setValue(available + 1);
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                    if (error != null) {
                        setStatus("Book count update failed: " + error.getMessage()
                                + " (Issue already marked returned)");
                        return;
                    }

                    if (!committed) {
                        setStatus("Book count update not committed.");
                        return;
                    }

                    setStatus("Returned successfully.");
                }
            });
        });
    }
}
