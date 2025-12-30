# 📚 Library Management System (Android Application)

A robust and feature-rich Android application designed to automate library operations. This system manages book inventories, tracks issues and returns, and provides detailed administrative reporting through three distinct user roles: **Admin**, **Librarian**, and **Student**.

---

## 🚀 Features

### 🛡️ Admin Module
*   **Analytics Dashboard:** Real-time statistics of total books, issued books, and registered users.
*   **User Management:** Add, view, and manage Librarians and Students.
*   **Advanced Reporting:**
    *   **Monthly Issued Report:** Track book circulation per month.
    *   **Monthly Returned Report:** Monitor book return trends.
    *   **Overdue Report:** Identify users with pending returns and fines.

### 📋 Librarian Module
*   **Inventory Management:** Add new books to the catalog and update existing records.
*   **Circulation Control:** Issue books to students and process returns.
*   **History Tracking:** View global history of all issued books.

### 🎓 Student Module
*   **Book Catalog:** Browse available books in the library.
*   **Personal History:** Track personal reading history and currently issued books.
*   **Due Date Alerts:** Monitor status of borrowed books.

---

## 🛠️ Tech Stack
*   **Platform:** Android
*   **Development Environment:** Android Studio
*   **Language:** Java / Kotlin
*   **Target SDK:** 31 (Android 12)
*   **UI Framework:** Material Design Components (XML)

---

## 📁 Project Architecture
The application follows a structured activity-based navigation:
*   **Authentication:** `LoginActivity`, `RegisterActivity`, `SplashActivity`
*   **Admin Screens:** `AdminHomeActivity`, `AdminStatsActivity`, `AdminReportsActivity`, `AddLibrarianActivity`
*   **Librarian Screens:** `LibrarianHomeActivity`, `AddBookActivity`, `IssueBookActivity`, `ViewBooksActivity`
*   **Reporting:** `MonthlyIssuedReportActivity`, `MonthlyReturnedReportActivity`, `OverdueReportActivity`

---

## ⚙️ Installation

### 1. Clone the Repository
```bash
git clone github.com
