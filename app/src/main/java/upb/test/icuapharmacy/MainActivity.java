package upb.test.icuapharmacy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView reviewList;
    private EditText inputFileName;
    private ArrayList<String> reviewFileNames;
    private ArrayList<String> reviewFilePaths;
    private DatabaseHelper dbHelper;
    private int userId = -1;
    private int selectedGrade = -1;
    private String selectedFileName = null;
    private String selectedProductName = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reviewList = findViewById(R.id.reviewList);
        inputFileName = findViewById(R.id.inputFileName);
        dbHelper = new DatabaseHelper(this);
        userId = getLoggedInUserId();

        if (userId == -1) {
            Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        reviewList.setOnItemClickListener((parent, view, position, id) -> {
            selectedFileName = reviewFileNames.get(position);
            inputFileName.setText(selectedFileName);

            DatabaseHelper.ReviewInfo info = dbHelper.getReviewInfoByFilename(userId, selectedFileName);
            if (info != null) {
                selectedGrade = info.grade;
                selectedProductName = info.productName;
            } else {
                selectedGrade = -1;
                selectedProductName = null;
            }
        });

        findViewById(R.id.buttonViewFile).setOnClickListener(v -> {
            String currentInput = inputFileName.getText().toString().trim();
            if (currentInput.isEmpty()) {
                Toast.makeText(this, "Please enter a filename", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, DisplayReviewActivity.class);
            intent.putExtra("userInput", currentInput);
            intent.putExtra("stars", selectedGrade);
            intent.putExtra("productName", selectedProductName);
            startActivity(intent);
        });

        findViewById(R.id.buttonAddReview).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddReviewActivity.class);
            startActivity(intent);
        });

        loadReviews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReviews();
    }

    private void loadReviews() {
        reviewFileNames = new ArrayList<>();
        reviewFilePaths = new ArrayList<>();

        ArrayList<String> paths = dbHelper.getReviewPathsForUser(userId);
        for (String path : paths) {
            File file = new File(path);
            reviewFileNames.add(file.getName());
            reviewFilePaths.add(path);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, reviewFileNames);
        reviewList.setAdapter(adapter);
    }

    private int getLoggedInUserId() {
        SharedPreferences prefs = getSharedPreferences("my_app_prefs", MODE_PRIVATE);
        String credentials = prefs.getString("user_credentials", null);
        if (credentials != null) {
            String username = credentials.split(":")[0];
            return dbHelper.getUserIdByUsername(username);
        }
        return -1;
    }
}
