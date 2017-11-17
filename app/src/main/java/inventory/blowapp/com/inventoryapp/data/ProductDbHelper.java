package inventory.blowapp.com.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static inventory.blowapp.com.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Aran on 11/16/2017.
 */

public class ProductDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "product.db";
    public static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableCommand =
                "CREATE TABLE " + ProductEntry.TABLE + "(" +
                        ProductEntry._ID + " INTEGER PRIMARY KEY, " +
                        ProductEntry.NAME + " TEXT NOT NULL, " +
                        ProductEntry.PRICE + " REAL NOT NULL, " +
                        ProductEntry.QUANTITY + " INTEGER NOT NULL, " +
                        ProductEntry.IMAGE + " TEXT " + ");";

        sqLiteDatabase.execSQL(createTableCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
