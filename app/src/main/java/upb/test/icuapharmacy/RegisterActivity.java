package upb.test.icuapharmacy;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword, editConfirmPassword;
    Button buttonRegister, buttonLogin, buttonAdminLogin;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonAdminLogin=findViewById(R.id.buttonAdminLogin);
        dbHelper = new DatabaseHelper(this);

        buttonRegister.setOnClickListener(view -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editConfirmPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query("user",null,"username=?",new String[]{username},null,null,null);
            if(cursor.getCount()!=0)
            {
                Toast.makeText(RegisterActivity.this,"Username already existing",Toast.LENGTH_SHORT).show();
                cursor.close();
                return;
            }
            cursor.close();
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);

            long newRowId = db.insert("user", null, values);
            if (newRowId != -1) {
                Log.v("RegisterActivity", "User " + username + " logged in successfully");

                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
                editTextUsername.setText("");
                editTextPassword.setText("");
                editConfirmPassword.setText("");
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });

        buttonLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        buttonAdminLogin.setOnClickListener(v->{
            Intent intent = new Intent(RegisterActivity.this,AdminLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}