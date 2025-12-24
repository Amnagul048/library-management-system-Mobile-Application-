package com.example.librarymanagementsystem;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class IssueBookActivity extends AppCompatActivity {

    private TextView txtBookTitle, txtStatus;
    private EditText etStudentInput, etDueDays;
    private Button btnConfirmIssue;

    private String bookId, bookTitle;

    private DatabaseReference usersRef;
    private DatabaseReference booksRef;
    private DatabaseReference issuesRef;

    // SAME DB URL YOU USED EVERYWHERE
    private static final String DB_URL = "https://librarymanagementsystem-6a1a9-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_book);

        txtBookTitle = findViewById(R.id.txtBookTitle);
        txtStatus = findViewById(R.id.txtStatus);
        etStudentInput = findViewById(R.id.etStudentInput);
        etDueDays = findViewById(R.id.etDueDays);
        btnConfirmIssue = findViewById(R.id.btnConfirmIssue);

        bookId = getIntent().getStringExtra("bookId");
        bookTitle = getIntent().getStringExtra("bookTitle");

        txtBookTitle.setText("Book: " + (bookTitle == null ? "" : bookTitle));
        txtStatus.setText("Ready.");

        FirebaseDatabase db = FirebaseDatabase.getInstance(DB_URL);
        usersRef = db.getReference("users");
        booksRef = db.getReference("books");
        issuesRef = db.getReference("issues");

        btnConfirmIssue.setOnClickListener(v -> resolveStudentAndIssue());
    }

    private void setStatus(String s) {
        txtStatus.setText(s);
    }

    private void resolveStudentAndIssue() {
        String input = etStudentInput.getText().toString().trim();
        String daysStr = etDueDays.getText().toString().trim();

        if (TextUtils.isEmpty(bookId)) {
            setStatus("Error: bookId missing");
            return;
        }

        if (TextUtils.isEmpty(input)) {
            etStudentInput.setError("Enter UID or Email");
            return;
        }

        if (TextUtils.isEmpty(daysStr)) {
            etDueDays.setError("Required");
            return;
        }

        int dueDays;
        try {
            dueDays = Integer.parseInt(daysStr);
        } catch (Exception e) {
            etDueDays.setError("Enter valid number");
            return;
        }

        if (dueDays <= 0) {
            etDueDays.setError("Must be > 0");
            return;
        }

        btnConfirmIssue.setEnabled(false);
        setStatus("Resolving student...");

        // If input is email -> find UID, else treat as UID
        if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            findUidByEmail(input, uid -> issueBook(uid, dueDays));
        } else {
            issueBook(input, dueDays);
        }
    }

    private interface UidCallback {
        void onFound(String uid);
    }

    private void findUidByEmail(String email, UidCallback cb) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String dbEmail = ds.child("email").getValue(String.class);
                    if (dbEmail != null && email.equalsIgnoreCase(dbEmail)) {
                        cb.onFound(ds.getKey());
                        return;
                    }
                }
                btnConfirmIssue.setEnabled(true);
                setStatus("No user found with this email.");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                btnConfirmIssue.setEnabled(true);
                setStatus("Error: " + error.getMessage());
            }
        });
    }

    private void issueBook(String studentUid, int dueDays) {
        if (TextUtils.isEmpty(studentUid)) {
            btnConfirmIssue.setEnabled(true);
            setStatus("Invalid student UID.");
            return;
        }

        setStatus("Checking availability...");

        DatabaseReference bookNode = booksRef.child(bookId);

        bookNode.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {

                // Read availableCopies atomically
                Object availObj = currentData.child("availableCopies").getValue();
                Object totalObj = currentData.child("totalCopies").getValue();

                long available = (availObj instanceof Long) ? (Long) availObj : 0L;
                long total = (totalObj instanceof Long) ? (Long) totalObj : 0L;

                if (total <= 0) {
                    return Transaction.abort();
                }

                if (available <= 0) {
                    return Transaction.abort();
                }

                // Decrement availability
                currentData.child("availableCopies").setValue(available - 1);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {

                if (error != null) {
                    btnConfirmIssue.setEnabled(true);
                    setStatus("Transaction error: " + error.getMessage());
                    return;
                }

                if (!committed) {
                    btnConfirmIssue.setEnabled(true);
                    setStatus("Cannot issue: no copies available.");
                    return;
                }

                // Availability decremented successfully; now create issue record
                setStatus("Creating issue record...");

                String issueId = issuesRef.push().getKey();
                if (issueId == null) {
                    // Rollback is complex; for MVP, just inform. (Next enhancement: compensate)
                    btnConfirmIssue.setEnabled(true);
                    setStatus("Error: issueId null. Book count already changed.");
                    return;
                }

                long now = System.currentTimeMillis();
                long dueDate = now + (dueDays * 24L * 60L * 60L * 1000L);

                String librarianUid = (FirebaseAuth.getInstance().getCurrentUser() != null)
                        ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                        : "";

                Map<String, Object> issue = new HashMap<>();
                issue.put("bookId", bookId);
                issue.put("bookTitle", bookTitle == null ? "" : bookTitle);
                issue.put("studentUid", studentUid);
                issue.put("issuedBy", librarianUid);
                issue.put("issueDate", now);
                issue.put("dueDate", dueDate);
                issue.put("returnDate", null);
                issue.put("status", "issued");

                issuesRef.child(issueId).setValue(issue, (err2, ref2) -> {
                    btnConfirmIssue.setEnabled(true);

                    if (err2 != null) {
                        setStatus("Issue save failed: " + err2.getMessage()
                                + " (Note: availability already decreased)");
                        return;
                    }

                    setStatus("Issued successfully.");
                    etStudentInput.setText("");
                    etDueDays.setText("");
                });
            }
        });
    }
}
