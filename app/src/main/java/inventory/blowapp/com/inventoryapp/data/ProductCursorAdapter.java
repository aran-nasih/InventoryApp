package inventory.blowapp.com.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import inventory.blowapp.com.inventoryapp.R;

/**
 * Created by Aran on 11/16/2017.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        Button sellButton = (Button) view.findViewById(R.id.item_sell_button);
        TextView nameTextView = (TextView) view.findViewById(R.id.item_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.item_price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.item_quantity);

        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.QUANTITY);
        int idColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);

        final String productName = cursor.getString(nameColumnIndex);
        final String productPrice = cursor.getString(priceColumnIndex);
        final String productQuantity = cursor.getString(quantityColumnIndex);
        final int productId = cursor.getInt(idColumnIndex);

        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQuantity = Integer.parseInt(productQuantity) - 1;
                if (newQuantity < 0) {
                    Toast.makeText(view.getContext(), "Number is larger than stock quantity", Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri uri = Uri.withAppendedPath(ProductContract.ProductEntry.CONTENT_URI, "/" + productId);
                ContentValues values = new ContentValues();
                values.put(ProductContract.ProductEntry.NAME, productName);
                values.put(ProductContract.ProductEntry.PRICE, productPrice);
                values.put(ProductContract.ProductEntry.QUANTITY, newQuantity);
                context.getContentResolver().update(uri, values, null, null);
            }
        });
    }

}
