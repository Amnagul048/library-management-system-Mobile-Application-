package com.example.librarymanagementsystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librarymanagementsystem.R;
import com.example.librarymanagementsystem.models.Issue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IssueAdapterLibrarian extends RecyclerView.Adapter<IssueAdapterLibrarian.IssueVH> {

    public interface OnReturnClick {
        void onReturn(Issue issue);
    }

    private final List<Issue> issues;
    private final OnReturnClick callback;
    private final SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);

    public IssueAdapterLibrarian(List<Issue> issues, OnReturnClick callback) {
        this.issues = issues;
        this.callback = callback;
    }

    @NonNull
    @Override
    public IssueVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issue_librarian, parent, false);
        return new IssueVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueVH h, int position) {
        Issue it = issues.get(position);

        h.txtBookTitle.setText(it.bookTitle == null ? "" : it.bookTitle);
        h.txtStudentUid.setText("Student UID: " + (it.studentUid == null ? "" : it.studentUid));
        h.txtIssueDate.setText("Issued: " + formatTime(it.issueDate));
        h.txtDueDate.setText("Due: " + formatTime(it.dueDate));

        // Only show return if currently issued
        String st = (it.status == null) ? "issued" : it.status;
        if ("issued".equalsIgnoreCase(st)) {
            h.btnReturn.setVisibility(View.VISIBLE);
        } else {
            h.btnReturn.setVisibility(View.GONE);
        }

        h.btnReturn.setOnClickListener(v -> {
            if (callback != null) callback.onReturn(it);
        });
    }

    private String formatTime(long ms) {
        if (ms <= 0) return "-";
        return df.format(new Date(ms));
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    static class IssueVH extends RecyclerView.ViewHolder {
        TextView txtBookTitle, txtStudentUid, txtIssueDate, txtDueDate;
        Button btnReturn;

        IssueVH(View v) {
            super(v);
            txtBookTitle = v.findViewById(R.id.txtBookTitle);
            txtStudentUid = v.findViewById(R.id.txtStudentUid);
            txtIssueDate = v.findViewById(R.id.txtIssueDate);
            txtDueDate = v.findViewById(R.id.txtDueDate);
            btnReturn = v.findViewById(R.id.btnReturn);
        }
    }
}
