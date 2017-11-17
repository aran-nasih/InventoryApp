package inventory.blowapp.com.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.provider.BaseColumns._ID;

/**
 * Created by Aran on 11/16/2017.
 */

public class ProductProvider extends ContentProvider {
    private static final int PRODUCTS = 100;
    private static final int PRODUCTS_ID = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT, PRODUCTS);
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT + "/#", PRODUCTS_ID);
    }

    private ProductDbHelper productDbHelper;

    @Override
    public boolean onCreate() {
        productDbHelper = new ProductDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = productDbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = uriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductContract.ProductEntry.TABLE, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCTS_ID:
                selection = ProductContract.ProductEntry._ID + "/?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductContract.ProductEntry.TABLE, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                System.out.println("Failed query request");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(ProductContract.ProductEntry.NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        // Check that the price is valid
        Double price = values.getAsDouble(ProductContract.ProductEntry.PRICE);
        if (String.valueOf(price) == null || String.valueOf(price) == "" || price < 0) {
            throw new IllegalArgumentException("Product price is not valid");
        }

        SQLiteDatabase database = productDbHelper.getWritableDatabase();
        long id = database.insert(ProductContract.ProductEntry.TABLE, null, values);
        if (id == -1) {
            System.out.println("Failed to insert to row " + id);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        if (match == PRODUCTS) {
            return productDbHelper.getWritableDatabase().delete(ProductContract.ProductEntry.TABLE, selection, selectionArgs);
        } else if (match == PRODUCTS_ID) {
            long id = ContentUris.parseId(uri);
            selection = _ID + "=?";
            selectionArgs = new String[]{String.valueOf(id)};
            int rowsDeleted = productDbHelper.getWritableDatabase().delete(ProductContract.ProductEntry.TABLE, selection, selectionArgs);
            if (rowsDeleted != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsDeleted;
        } else {
            return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        if (match == PRODUCTS) {
            return productDbHelper.getWritableDatabase().update(ProductContract.ProductEntry.TABLE, values, selection, selectionArgs);
        } else if (match == PRODUCTS_ID) {
            long id = ContentUris.parseId(uri);
            selection = _ID + "=?";
            selectionArgs = new String[]{String.valueOf(id)};
            int rowsUpdate = productDbHelper.getWritableDatabase().update(ProductContract.ProductEntry.TABLE, values, selection, selectionArgs);
            if (rowsUpdate != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsUpdate;
            }
        }
        return 0;
    }
}
