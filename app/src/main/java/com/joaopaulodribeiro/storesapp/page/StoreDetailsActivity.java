package com.joaopaulodribeiro.storesapp.page;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joaopaulodribeiro.storesapp.R;
import com.joaopaulodribeiro.storesapp.model.Store;
import com.joaopaulodribeiro.storesapp.page.camera.CameraActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StoreDetailsActivity extends AppCompatActivity {

    public static final String STORE_ID_BUNDLE_KEY = "store_bundle_key";
    private static final int PICK_IMAGE_REQUEST = 1;
    public static final int TAKE_PHOTO_REQUEST = 2;

    private TextView mStoreDetailsId;
    private TextView mStoreDetailsName;
    private TextView mStoreDetailsLogr;
    private TextView mStoreDetailsNumber;
    private TextView mStoreDetailsComplement;
    private TextView mStoreDetailsNeig;

    private ImageView mTakePhotoIv;
    private ImageView mCallGalleryIv;

    private LinearLayout mNoImageLayout;
    private FloatingActionButton mFabTelephone;

    private ImageView mStoreImagePreview;
    private Store store;

    private void bindViews() {
        mStoreDetailsId = findViewById(R.id.store_details_id_tv);
        mStoreDetailsName = findViewById(R.id.store_details_name_tv);
        mStoreDetailsLogr = findViewById(R.id.store_details_logr_tv);
        mStoreDetailsNumber = findViewById(R.id.store_details_number_tv);
        mStoreDetailsComplement = findViewById(R.id.store_details_complement_tv);
        mStoreDetailsNeig = findViewById(R.id.store_details_neig_tv);
        mNoImageLayout = findViewById(R.id.store_details_preview_no_img);
        mStoreImagePreview = findViewById(R.id.store_details_preview_img);
        mFabTelephone = findViewById(R.id.fab_telephone);

        mTakePhotoIv = findViewById(R.id.take_photo_image_view);
        mCallGalleryIv = findViewById(R.id.call_gallery_image_view);

        mTakePhotoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreDetailsActivity.this, CameraActivity.class);
                if (store != null) {
                    intent.putExtra(STORE_ID_BUNDLE_KEY, store.id);
                }
                startActivityForResult(intent, TAKE_PHOTO_REQUEST);
            }
        });

        mCallGalleryIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        mFabTelephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (store.telefone != null && !store.telefone.isEmpty()) {
                    dialPhoneNumber(store.telefone);
                } else {
                    Snackbar.make(findViewById(R.id.store_details_coordinator_layout),
                            R.string.no_telephone_number_added_string, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        mStoreImagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Matrix matrix = new Matrix();

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            setImageFromGallery(matrix, uri);

        } else if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            setImageFromCamera(data, matrix);
        }
    }

    private void setImageFromCamera(Intent data, Matrix matrix) {
        Bitmap bmp = BitmapFactory.decodeFile(data.getData().toString());

        matrix.postRotate(90);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        if (bmp != null) {
            mStoreImagePreview.setImageBitmap(bmp);
            mNoImageLayout.setVisibility(View.GONE);
        }
    }

    private void setImageFromGallery(Matrix matrix, Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            if (bitmap.getWidth() > bitmap.getHeight()) {
                matrix.postRotate(90);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            if (bitmap != null) {
                mStoreImagePreview.setImageBitmap(bitmap);
                mNoImageLayout.setVisibility(View.GONE);
                final Bitmap finalBitmap = bitmap;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveImageOnLocalStore(finalBitmap);
                    }
                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_picture_preview_menu:
                mTakePhotoIv.performClick();
                break;
            case R.id.change_picture_from_gallery_menu:
                mCallGalleryIv.performClick();
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_details);
        bindViews();

        store = getIntent().getParcelableExtra(STORE_ID_BUNDLE_KEY);

        if (verifyIfFieldsAreNotNull()) {
            mStoreDetailsId.setText("ID: " + store.id);
            mStoreDetailsName.setText(store.nome);
            mStoreDetailsLogr.setText(store.endereco.logradouro);
            mStoreDetailsNumber.setText(store.endereco.numero);
            mStoreDetailsComplement.setText(store.endereco.complemento);
            mStoreDetailsNeig.setText(store.endereco.bairro);
        }

        loadImageFromLocalStorage();

    }

    private void loadImageFromLocalStorage() {
        Matrix matrix = new Matrix();

        String child = "image" + store.id + "_pic.jpg";
        File loadImage = new File(getExternalFilesDir(null), child);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(loadImage.getAbsolutePath(), bmOptions);

        if (bitmap != null) {
            if (bitmap.getHeight()< bitmap.getWidth()) {
                matrix.postRotate(90);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            mStoreImagePreview.setImageBitmap(bitmap);
            mNoImageLayout.setVisibility(View.GONE);
        } else {
            mNoImageLayout.setVisibility(View.VISIBLE);
        }
    }

    private void saveImageOnLocalStore(Bitmap bitmap) {
        OutputStream outStream;

        String child = "image" + store.id + "_pic.jpg";
        File saveImage = new File(getExternalFilesDir(null), child);

        if (saveImage.exists()) {
            saveImage.delete();
            saveImage = new File(getExternalFilesDir(null), child);
        }
        try {
            outStream = new FileOutputStream(saveImage);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean verifyIfFieldsAreNotNull() {
        return mStoreDetailsId != null &&
                mStoreDetailsName != null &&
                mStoreDetailsLogr != null &&
                mStoreDetailsNumber != null &&
                mStoreDetailsComplement != null &&
                mStoreDetailsNeig != null;
    }


}
