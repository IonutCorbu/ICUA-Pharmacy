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

- **AdminDashboardActivity**  
  Displays the list of products for admins.  
  **Vulnerabilities:**  
  - **Not exported** — protected from unauthorized external access.  

- **AddProductActivity**  
  Form for adding new products (admin only).  
  **Vulnerabilities:**  
  - Exported, accessible without authentication, enabling unauthorized product additions.  

---

## Project Components

### 1. Activities

- RegisterActivity  
- LoginActivity  
- AdminLoginActivity  
- AdminDashboardActivity  
- AddProductActivity  

### 2. Database

- SQLite with `users` and `products` tables.

### 3. UI

- RecyclerView, FloatingActionButton, Material Components.

### 4. Libraries

- Glide for image loading.

---

