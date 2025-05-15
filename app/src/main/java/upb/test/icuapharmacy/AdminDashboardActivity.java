package upb.test.icuapharmacy;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private ArrayList<Product> products = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private FloatingActionButton fabAddProduct;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        fabAddProduct = findViewById(R.id.fabAddProduct);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(this, products);
        recyclerView.setAdapter(adapter);

        dbHelper = new DatabaseHelper(this);
        loadProductsFromDb();

        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    private void loadProductsFromDb() {
        products.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name, price, imageUrl FROM products", null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(1);
                double price = cursor.getDouble(2);
                String imageUrl = cursor.getString(3);

                Product product = new Product(name, price, imageUrl);
                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductsFromDb();
    }
}
