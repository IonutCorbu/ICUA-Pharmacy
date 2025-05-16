package upb.test.icuapharmacy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
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
            String productName = null;
            Cursor cursor = db.rawQuery("SELECT name FROM products WHERE id = ?", new String[]{String.valueOf(productId)});
            if (cursor.moveToFirst()) {
                productName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            }
            cursor.close();

            if (productName == null) {
                productName = "unknown_product";
            }


            String sanitizedProductName = productName.toLowerCase().replaceAll("[^a-z0-9]", "_");

            String fileName = "review_" + sanitizedProductName + ".txt";

            String contentToSave = "Product: " + productName + "\n\n" + reviewText;

            File file = new File(context.getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(contentToSave.getBytes());
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


    public ArrayList<String> getReviewPathsForUser(int userId) {
        ArrayList<String> paths = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT review_path FROM review WHERE id_user = ?";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId) });

        if (cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndexOrThrow("review_path"));
                paths.add(path);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return paths;
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT id, name, price, imageUrl FROM products";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"));

                products.add(new Product(id, name, price, imageUrl));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return products;
    }

    public boolean hasUserReviewedProduct(int userId, String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT r.id FROM review r JOIN products p ON r.id_product = p.id WHERE r.id_user = ? AND p.name = ?",
                new String[]{String.valueOf(userId), productName}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public void addProduct(String name, double price, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("price", price);
        values.put("imageUrl", imageUrl);
        db.insert("products", null, values);
        db.close();
    }

    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM user WHERE username = ?", new String[]{username});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        }
        cursor.close();
        return userId;
    }


    public ReviewInfo getReviewInfoByFilename(int userId, String filename) {
        SQLiteDatabase db = this.getReadableDatabase();
        ReviewInfo reviewInfo = null;

        String query = "SELECT p.name, r.grade FROM review r " +
                "JOIN products p ON r.id_product = p.id " +
                "WHERE r.id_user = ? AND r.review_path LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), "%" + filename});

        if (cursor.moveToFirst()) {
            String productName = cursor.getString(0);
            int grade = cursor.getInt(1);
            reviewInfo = new ReviewInfo(productName, grade);
        }
        cursor.close();
        return reviewInfo;
    }

    public static class ReviewInfo {
        public final String productName;
        public final int grade;

        public ReviewInfo(String productName, int grade) {
            this.productName = productName;
            this.grade = grade;
        }
    }
}
