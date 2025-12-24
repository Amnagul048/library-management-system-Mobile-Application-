package com.example.librarymanagementsystem;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class AddBookActivity extends AppCompatActivity {

    private EditText etTitle, etAuthor, etCategory, etTotalCopies;
    private Button btnSaveBook;
    private TextView txtStatus;

    private DatabaseReference booksRef;

    // Use the SAME correct DB URL you used in Login/Register/Splash
    private static final String DB_URL = "https://librarymanagementsystem-6a1a9-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etCategory = findViewById(R.id.etCategory);
        etTotalCopies = findViewById(R.id.etTotalCopies);
        btnSaveBook = findViewById(R.id.btnSaveBook);
        txtStatus = findViewById(R.id.txtStatus);

        booksRef = FirebaseDatabase.getInstance(DB_URL).getReference("books");

        btnSaveBook.setOnClickListener(v -> saveBook());
        txtStatus.setText("Ready.");
    }

    private void setStatus(String s) {
        txtStatus.setText(s);
    }

    private void saveBook() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String copiesStr = etTotalCopies.getText().toString().trim();

        if (TextUtils.isEmpty(title)) { etTitle.setError("Required"); return; }
        if (TextUtils.isEmpty(author)) { etAuthor.setError("Required"); return; }
        if (TextUtils.isEmpty(category)) { etCategory.setError("Required"); return; }
        if (TextUtils.isEmpty(copiesStr)) { etTotalCopies.setError("Required"); return; }

        int totalCopies;
        try {
            totalCopies = Integer.parseInt(copiesStr);
        } catch (Exception e) {
            etTotalCopies.setError("Enter a valid number");
            return;
        }

        if (totalCopies <= 0) {
            etTotalCopies.setError("Must be > 0");
            return;
        }

        btnSaveBook.setEnabled(false);
        setStatus("Saving book...");

        String bookId = booksRef.push().getKey();
        if (bookId == null) {
            btnSaveBook.setEnabled(true);
            setStatus("Failed: bookId null");
            return;
        }

        Map<String, Object> book = new HashMap<>();
        book.put("title", title);
        book.put("author", author);
        book.put("category", category);
        book.put("totalCopies", totalCopies);
        book.put("availableCopies", totalCopies);
        book.put("createdAt", ServerValue.TIMESTAMP);

        booksRef.child(bookId).setValue(book, (error, ref) -> {
            btnSaveBook.setEnabled(true);

            if (error != null) {
                setStatus("Save failed: " + error.getMessage());
                return;
            }

            setStatus("Saved successfully.");
            etTitle.setText("");
            etAuthor.setText("");
            etCategory.setText("");
            etTotalCopies.setText("");
        });
    }
}
