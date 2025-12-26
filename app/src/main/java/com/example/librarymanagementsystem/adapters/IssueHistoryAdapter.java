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

public class IssueHistoryAdapter extends RecyclerView.Adapter<IssueHistoryAdapter.HVH> {

    private final List<Issue> list;
    private final SimpleDateFormat df =
            new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);

    // fine per day
    private static final int PER_DAY_FINE = 50;

    public IssueHistoryAdapter(List<Issue> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_issue_history, parent, false);
        return new HVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HVH h, int position) {
        Issue it = list.get(position);

        h.txtBookTitle.setText(it.bookTitle == null ? "" : it.bookTitle);
        String st = it.status == null ? "issued" : it.status;
        h.txtStatus.setText("Status: " + st);

        h.txtIssueDate.setText("Issued: " + fmt(it.issueDate));
        h.txtDueDate.setText("Due: " + fmt(it.dueDate));

        if ("returned".equalsIgnoreCase(st) && it.returnDate instanceof Long) {
            long rd = (Long) it.returnDate;
            h.txtReturnDate.setVisibility(View.VISIBLE);
            h.txtReturnDate.setText("Returned: " + fmt(rd));

            long fine = calcFine(it.dueDate, rd);
            if (fine > 0) {
                h.txtFine.setVisibility(View.VISIBLE);
                h.txtFine.setText("Fine: Rs " + fine);
            } else {
                h.txtFine.setVisibility(View.GONE);
            }
        } else {
            h.txtReturnDate.setVisibility(View.GONE);
            h.txtFine.setVisibility(View.GONE);
        }
    }

    private String fmt(long ms) {
        if (ms <= 0) return "-";
        return df.format(new Date(ms));
    }

    private long calcFine(long due, long ret) {
        if (ret <= due) return 0;
        long diffMs = ret - due;
        long daysLate = (diffMs / (24L * 60L * 60L * 1000L));
        return Math.max(daysLate, 1) * PER_DAY_FINE;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class HVH extends RecyclerView.ViewHolder {
        TextView txtBookTitle, txtStatus, txtIssueDate, txtDueDate, txtReturnDate, txtFine;

        HVH(View v) {
            super(v);
            txtBookTitle = v.findViewById(R.id.txtBookTitle);
            txtStatus = v.findViewById(R.id.txtStatus);
            txtIssueDate = v.findViewById(R.id.txtIssueDate);
            txtDueDate = v.findViewById(R.id.txtDueDate);
            txtReturnDate = v.findViewById(R.id.txtReturnDate);
            txtFine = v.findViewById(R.id.txtFine);
        }
    }
}
