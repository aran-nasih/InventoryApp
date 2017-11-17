package inventory.blowapp.com.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import inventory.blowapp.com.inventoryapp.data.ProductContract.ProductEntry;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText productNameInput;
    EditText productPriceInput;
    EditText productQuantityInput;
    EditText productQuantityChangeInput;
    TextView productQuantityView;
    ImageView imageView;
    Button increaseButton;
    Button decreaseButton;
    Button orderButton;
    Button deleteButton;
    Button addButton;
    Button updateButton;
    Button imageButton;

    public static final int EXISTING_PRODUCT_LOADER = 0;
    boolean changesMade = false;
    Uri currentProductUri;
    Uri imageUri;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            changesMade = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        productNameInput = (EditText) findViewById(R.id.product_name_input);
        productPriceInput = (EditText) findViewById(R.id.product_price_input);
        productQuantityInput = (EditText) findViewById(R.id.product_quantity_input);
        productQuantityChangeInput = (EditText) findViewById(R.id.product_quantity_change_input);
        productQuantityView = (TextView) findViewById(R.id.product_quantity_view);
        imageView = (ImageView) findViewById(R.id.product_image);
        increaseButton = (Button) findViewById(R.id.product_increase);
        decreaseButton = (Button) findViewById(R.id.product_decrease);
        orderButton = (Button) findViewById(R.id.product_order);
        deleteButton = (Button) findViewById(R.id.product_delete);
        addButton = (Button) findViewById(R.id.product_add);
        updateButton = (Button) findViewById(R.id.product_update);
        imageButton = (Button) findViewById(R.id.add_image);

        Intent intent = getIntent();
        currentProductUri = intent.getData();

        if (currentProductUri == null) {
            setTitle("Add Product");
            productQuantityInput.setText("0");
            increaseButton.setVisibility(View.GONE);
            decreaseButton.setVisibility(View.GONE);
            productQuantityChangeInput.setVisibility(View.GONE);
            orderButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.GONE);
            productQuantityView.setVisibility(View.GONE);

        } else {
            setTitle("View Product");
            productQuantityChangeInput.setText("1");
            addButton.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            productQuantityInput.setVisibility(View.GONE);
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("mailto:" + "test@test")
                        .buildUpon()
                        .appendQueryParameter("subject", "subject")
                        .appendQueryParameter("body", "body")
                        .build();

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(Intent.createChooser(emailIntent, "title"));
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProduct(0);
            }
        });

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProduct(-1);
            }
        });

        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProduct(-2);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }
        });

        productNameInput.setOnTouchListener(touchListener);
        productPriceInput.setOnTouchListener(touchListener);
        productQuantityInput.setOnTouchListener(touchListener);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    imageUri = imageReturnedIntent.getData();
                    Picasso.with(this).load(imageUri).into(imageView);
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    imageUri = imageReturnedIntent.getData();
                    Picasso.with(this).load(imageUri).into(imageView);
                }
                break;
        }
    }


    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private void addProduct() {
        String nameString = productNameInput.getText().toString().trim();
        String priceString = productPriceInput.getText().toString().trim();
        String quantityString = productQuantityInput.getText().toString().trim();
        if (nameString == null || priceString == null || nameString.equals("") || priceString.equals("")) {
            Toast.makeText(this, "Name and price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantityString.equals("")) quantityString = "0";
        Integer quantityInt = 0;
        Double priceDouble = 0.0;
        try {
            quantityInt = Integer.parseInt(quantityString);
            priceDouble = Double.parseDouble(priceString);
        } catch (Exception e) {
            Toast.makeText(this, "Price and Quantity must be number", Toast.LENGTH_SHORT).show();
        }

        if (quantityInt < 0) {
            Toast.makeText(this, "Quantity can't be below zero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (priceDouble <= 0) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageString = "";
        if (imageUri != null)
            imageString = imageUri.toString();

        ContentValues values = new ContentValues();
        values.put(ProductEntry.NAME, nameString);
        values.put(ProductEntry.PRICE, priceDouble);
        values.put(ProductEntry.QUANTITY, quantityInt);
        values.put(ProductEntry.IMAGE, imageString);

        if (currentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!changesMade) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.NAME,
                ProductEntry.PRICE,
                ProductEntry.QUANTITY,
                ProductEntry.IMAGE};

        return new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.IMAGE);

            String name = cursor.getString(nameColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String image = cursor.getString(imageColumnIndex);
            Uri currentImageUri = Uri.parse(image);
            imageUri = currentImageUri;
            productNameInput.setText(name);
            productPriceInput.setText(price + "");
            productQuantityView.setText(quantity + "");
            imageView = (ImageView) findViewById(R.id.product_image);
            Picasso.with(this).load(imageUri).into(imageView);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productNameInput.setText("");
        productPriceInput.setText("");
        productQuantityInput.setText("");
    }

    private void showUnsavedChangesDialog
            (DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateProduct(int quantityHandler) {
        String nameString = productNameInput.getText().toString().trim();
        String priceString = productPriceInput.getText().toString().trim();
        String quantityString = productQuantityView.getText().toString().trim();
        String quantityInputString = productQuantityChangeInput.getText().toString().trim();
        if (nameString.equals("") || priceString.equals("")) {
            Toast.makeText(this, "Name and price are required", Toast.LENGTH_SHORT).show();
            return;
        }
        double price = 0;
        int quantity = 0;
        Integer oldQuantity = Integer.parseInt(quantityString);
        try {
            price = Double.parseDouble(priceString);
            quantity = Integer.parseInt(quantityInputString);
        } catch (Exception e) {
            Toast.makeText(this, "Quantity and Price values must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        int newQuantity = oldQuantity;
        switch (quantityHandler) {
            case -1:
                newQuantity = oldQuantity + quantity;
                changesMade = false;
                break;
            case -2:
                newQuantity = oldQuantity - quantity;
                if (newQuantity < 0) {
                    Toast.makeText(this, "Number is larger than stock quantity", Toast.LENGTH_SHORT).show();
                    changesMade = false;
                    return;
                }
                break;
        }

        ContentValues values = new ContentValues();
        values.put(ProductEntry.NAME, nameString);
        values.put(ProductEntry.PRICE, price);
        values.put(ProductEntry.QUANTITY, newQuantity);
        values.put(ProductEntry.IMAGE, imageUri.toString());

        int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);

        if (rowsAffected < 0) {
            Toast.makeText(this, getString(R.string.editor_update_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_update_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
        if (quantityHandler >= 0)
            finish();
    }

    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (currentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful), Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
