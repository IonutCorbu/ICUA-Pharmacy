package upb.test.icuapharmacy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AddReviewActivity extends AppCompatActivity {

    private Spinner productSpinner;
    private EditText inputReview, inputGrade;
    private Button submitReview;
    private ArrayList<Product> productList;
    private DatabaseHelper dbHelper;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        productSpinner = findViewById(R.id.spinnerProducts);
        inputReview = findViewById(R.id.inputReviewContent);
        inputGrade = findViewById(R.id.inputReviewGrade);
        submitReview = findViewById(R.id.buttonSubmitReview);

        dbHelper = new DatabaseHelper(this);
        userId = getLoggedInUserId();

        if (userId == -1) {
            Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadProductsIntoSpinner();

        submitReview.setOnClickListener(v -> {
            String review = inputReview.getText().toString();
            String gradeStr = inputGrade.getText().toString();

            if (review.isEmpty() || gradeStr.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            int grade;
            try {
                grade = Integer.parseInt(gradeStr);
                if (grade < 1 || grade > 5) {
                    Toast.makeText(this, "Grade must be between 1 and 5", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid grade", Toast.LENGTH_SHORT).show();
                return;
            }

            Product selectedProduct = productList.get(productSpinner.getSelectedItemPosition());

            if (dbHelper.hasUserReviewedProduct(userId, selectedProduct.getName())) {
                Toast.makeText(this, "You already reviewed this product", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.insertReview(userId, selectedProduct.getId(), grade, review);
            Toast.makeText(this, "Review saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadProductsIntoSpinner() {
        productList = dbHelper.getAllProducts();
        ArrayList<String> productNames = new ArrayList<>();
        for (Product p : productList) {
            productNames.add(p.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(adapter);
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
