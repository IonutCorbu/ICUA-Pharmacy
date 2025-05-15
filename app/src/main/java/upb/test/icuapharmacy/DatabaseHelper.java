package upb.test.icuapharmacy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AppDatabase.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "password TEXT)");

        db.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "price REAL, " +
                "imageUrl TEXT)");

        db.execSQL("CREATE TABLE review (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_user INTEGER, " +
                "id_product INTEGER, " +
                "grade INTEGER CHECK(grade >= 1 AND grade <= 5), " +
                "review_path TEXT, " +
                "FOREIGN KEY(id_user) REFERENCES user(id), " +
                "FOREIGN KEY(id_product) REFERENCES products(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS review");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }

    public void insertReview(int userId, int productId, int grade, String reviewText) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String fileName = "review_" + System.currentTimeMillis() + ".txt";
            File file = new File(context.getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(reviewText.getBytes());
            fos.close();

            ContentValues values = new ContentValues();
            values.put("id_user", userId);
            values.put("id_product", productId);
            values.put("grade", grade);
            values.put("review_path", file.getAbsolutePath());

            db.insert("review", null, values);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<Product>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT id, name, price, imageUrl FROM products";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"));

                products.add(new Product(name, price, imageUrl));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return products;
    }
}
