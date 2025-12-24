package com.example.librarymanagementsystem.models;

public class Issue {

    public String id;
    public String bookId;
    public String bookTitle;
    public String studentUid;
    public String issuedBy;
    public long issueDate;
    public long dueDate;
    public Object returnDate;   // can be null
    public String status;       // "issued" or "returned"

    public Issue() { }
}
