package upb.test.icuapharmacy;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DisplayReviewActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView fileContentTextView;
    private DatabaseHelper dbHelper;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_review);

        titleTextView = findViewById(R.id.textReviewTitle);
        fileContentTextView = findViewById(R.id.textReviewOutput);
        dbHelper = new DatabaseHelper(this);

        userId = getLoggedInUserId();

        String userInput = getIntent().getStringExtra("userInput");
        int stars = getIntent().getIntExtra("stars", -1);
        String productName = getIntent().getStringExtra("productName");

        if (userInput != null && userId != -1) {
            String title = ((productName != null ? productName : "<no_product>") + " — " +
                    (stars == -1 ? "<no_stars>" : stars) + " ★");
            titleTextView.setText(title);
            executeCommand(userInput);
        }
    }

    private void executeCommand(String filename) {
        try {
            String basePath = getFilesDir().getAbsolutePath();
            String command = "cat " + basePath + "/" + filename;

            Log.d("DisplayReviewActivity", "Running command: " + command);

            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});

            BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = stdOut.readLine()) != null) {
                output.append(line).append("\n");
            }

            while ((line = stdErr.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();

            stdOut.close();
            stdErr.close();

            fileContentTextView.setText(output.toString());

        } catch (Exception e) {
            fileContentTextView.setText("Error executing command: " + e.getMessage());
        }
    }

    private int getLoggedInUserId() {
        String credentials = getSharedPreferences("my_app_prefs", MODE_PRIVATE)
                .getString("user_credentials", null);
        if (credentials != null) {
            String username = credentials.split(":")[0];
            return dbHelper.getUserIdByUsername(username);
        }
        return -1;
    }
}
