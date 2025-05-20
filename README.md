# ICUA Pharmacy Android App

## Overview & Vulnerabilities

This Android application supports user registration, user login, admin login, and product management (viewing and adding).

### Activities & Vulnerabilities


- **RegisterActivity**  
  Allows new users to create accounts.  
  **Vulnerabilities:**  
  - Credentials are logged in plaintext (risk of sensitive data exposure via logs).

- **LoginActivity**  
  Allows existing users to log in.  
  **Vulnerabilities:**  
  - Credentials are stored in SharedPreferences without encryption (risk of credential theft).

- **AdminLoginActivity**  
  Special login for admins.  
  **Vulnerabilities:**  
  - Admin credentials are hardcoded in the app (easy to extract and misuse).

- **AddProductActivity**  
  Form for adding new products (admin only).  
  **Vulnerabilities:**  
  - Exported, accessible without authentication, enabling unauthorized product additions.

- **DisplayReviewActivity**  
  Shows user review content by filename input.  
  **Vulnerabilities:**  
  - Executes shell command `cat <filename>` directly using user input without sanitization, leading to **command injection vulnerability**.  
  - An attacker can craft malicious input to execute arbitrary commands on the device.

- **Database**
  - Stores unhashed passwords which could imply a leak of users' passwords.

---

## Project Components

### 1. Activities

- RegisterActivity  
- LoginActivity  
- AdminLoginActivity  
- AdminDashboardActivity  
- AddProductActivity  
- DisplayReviewActivity

### 2. Database

- SQLite with `user`, `products`, and `review` tables.

### 3. UI

- RecyclerView, FloatingActionButton, Material Components.

### 4. Libraries

- Glide for image loading.

---
