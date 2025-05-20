package upb.test.icuapharmacy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {

    EditText editTextAdminUser, editTextAdminPass;
    Button buttonAdminLogin, buttonLogin, buttonRegister;

    private final String ADMIN_USERNAME = "admin";
    private final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        editTextAdminUser = findViewById(R.id.editTextAdminUser);
        editTextAdminPass = findViewById(R.id.editTextAdminPass);
        buttonAdminLogin = findViewById(R.id.buttonAdminLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonAdminLogin.setOnClickListener(v -> {
            String username = editTextAdminUser.getText().toString().trim();
            String password = editTextAdminPass.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
            }
        });

        buttonLogin.setOnClickListener(v -> {
            Intent intent = new Intent(AdminLoginActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(AdminLoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
