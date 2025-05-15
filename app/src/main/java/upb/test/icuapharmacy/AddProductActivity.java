package upb.test.icuapharmacy;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddProductActivity extends AppCompatActivity {

    private EditText editTextName, editTextPrice, editTextImageUrl;
    private Button buttonAddProduct;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        editTextName = findViewById(R.id.editTextName);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextImageUrl = findViewById(R.id.editTextImageUrl);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);

        dbHelper = new DatabaseHelper(this);

        buttonAddProduct.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String priceText = editTextPrice.getText().toString().trim();
            String imageUrl = editTextImageUrl.getText().toString().trim();

            if (name.isEmpty() || priceText.isEmpty()) {
                Toast.makeText(this, "Name and price are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("price", price);
            values.put("imageUrl", imageUrl);

            long newRowId = db.insert("products", null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error adding product", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
