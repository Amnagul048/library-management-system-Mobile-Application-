package com.example.librarymanagementsystem;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librarymanagementsystem.adapters.BookAdapter;
import com.example.librarymanagementsystem.models.Book;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ViewBooksActivity extends AppCompatActivity {

    private RecyclerView rvBooks;
    private BookAdapter adapter;
    private final List<Book> bookList = new ArrayList<>();

    private DatabaseReference booksRef;

    private static final String DB_URL = "https://librarymanagementsystem-6a1a9-default-rtdb.asia-southeast1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_books);

        boolean isLibrarian = getIntent().getBooleanExtra("isLibrarian", false);

        rvBooks = findViewById(R.id.rvBooks);
        rvBooks.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookAdapter(bookList);
        adapter.setLibrarian(isLibrarian);
        rvBooks.setAdapter(adapter);

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
        });

        booksRef = FirebaseDatabase.getInstance(DB_URL).getReference("books");
        loadBooks();
    }

    private void loadBooks() {
        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                bookList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Book b = ds.getValue(Book.class);
                    if (b != null) {
                        b.id = ds.getKey();
                        bookList.add(b);
                    }
                }

                adapter.setData(bookList);
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }
}
