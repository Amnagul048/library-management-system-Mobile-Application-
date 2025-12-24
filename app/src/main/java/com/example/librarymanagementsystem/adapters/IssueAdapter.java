package com.example.librarymanagementsystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librarymanagementsystem.R;
import com.example.librarymanagementsystem.models.Issue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.IssueVH> {

    private final List<Issue> issues;
    private final SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);

    public IssueAdapter(List<Issue> issues) {
        this.issues = issues;
    }

    @NonNull
    @Override
    public IssueVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issue, parent, false);
        return new IssueVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueVH h, int position) {
        Issue it = issues.get(position);

        h.txtBookTitle.setText(it.bookTitle == null ? "" : it.bookTitle);

        String st = (it.status == null) ? "issued" : it.status;
        h.txtStatus.setText("Status: " + st);

        h.txtIssueDate.setText("Issued: " + formatTime(it.issueDate));
        h.txtDueDate.setText("Due: " + formatTime(it.dueDate));
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
        TextView txtBookTitle, txtStatus, txtIssueDate, txtDueDate;

        IssueVH(View v) {
            super(v);
            txtBookTitle = v.findViewById(R.id.txtBookTitle);
            txtStatus = v.findViewById(R.id.txtStatus);
            txtIssueDate = v.findViewById(R.id.txtIssueDate);
            txtDueDate = v.findViewById(R.id.txtDueDate);
        }
    }
}
