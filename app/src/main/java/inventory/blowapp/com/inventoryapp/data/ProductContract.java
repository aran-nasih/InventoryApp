package inventory.blowapp.com.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Aran on 11/15/2017.
 */

public class ProductContract {
    public static final String CONTENT_AUTHORITY = "inventory.blowapp.com.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCT = "products";

    public ProductContract() {
    }

    public static final class ProductEntry implements BaseColumns {
        public static final String TABLE = "products";
        public static final String _ID = BaseColumns._ID;
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String QUANTITY = "quantity";
        public static final String IMAGE = "image";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);
        public static final String[] ALL_FIELDS = {_ID, NAME, PRICE, QUANTITY, IMAGE};
    }
}
