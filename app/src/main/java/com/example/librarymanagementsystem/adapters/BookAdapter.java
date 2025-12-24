package com.example.librarymanagementsystem.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librarymanagementsystem.IssueBookActivity;
import com.example.librarymanagementsystem.R;
import com.example.librarymanagementsystem.models.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookVH> {

    private final List<Book> allBooks = new ArrayList<>();
    private final List<Book> visibleBooks = new ArrayList<>();

    private boolean isLibrarian = false;

    public BookAdapter(List<Book> initial) {
        setData(initial);
    }

    public void setLibrarian(boolean librarian) {
        this.isLibrarian = librarian;
        notifyDataSetChanged();
    }

    public void setData(List<Book> data) {
        allBooks.clear();
        allBooks.addAll(data);

        visibleBooks.clear();
        visibleBooks.addAll(data);

        notifyDataSetChanged();
    }

    public void filter(String q) {
        String query = (q == null) ? "" : q.trim().toLowerCase(Locale.US);

        visibleBooks.clear();

        if (query.isEmpty()) {
            visibleBooks.addAll(allBooks);
        } else {
            for (Book b : allBooks) {
                String t = safe(b.title);
                String a = safe(b.author);
                String c = safe(b.category);

                if (t.contains(query) || a.contains(query) || c.contains(query)) {
                    visibleBooks.add(b);
                }
            }
        }

        notifyDataSetChanged();
    }

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase(Locale.US);
    }

    @NonNull
    @Override
    public BookVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookVH h, int position) {
        Book b = visibleBooks.get(position);

        h.txtTitle.setText(b.title);
        h.txtAuthor.setText("Author: " + b.author);
        h.txtCategory.setText("Category: " + b.category);
        h.txtCopies.setText("Available: " + b.availableCopies + "/" + b.totalCopies);

        // Librarian only + must have availability
        if (isLibrarian && b.availableCopies > 0) {
            h.btnIssue.setVisibility(View.VISIBLE);
        } else {
            h.btnIssue.setVisibility(View.GONE);
        }

        h.btnIssue.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), IssueBookActivity.class);
            i.putExtra("bookId", b.id);
            i.putExtra("bookTitle", b.title);
            v.getContext().startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return visibleBooks.size();
    }

    static class BookVH extends RecyclerView.ViewHolder {
        TextView txtTitle, txtAuthor, txtCategory, txtCopies;
        Button btnIssue;

        BookVH(View v) {
            super(v);
            txtTitle = v.findViewById(R.id.txtTitle);
            txtAuthor = v.findViewById(R.id.txtAuthor);
            txtCategory = v.findViewById(R.id.txtCategory);
            txtCopies = v.findViewById(R.id.txtCopies);
            btnIssue = v.findViewById(R.id.btnIssue);
        }
    }
}
