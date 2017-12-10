package com.araceliteixeira.myalbum;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.araceliteixeira.myalbum.DAO.ItemDAO;
import com.araceliteixeira.myalbum.model.Item;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_CODE = 990;
    private String dirPhoto;
    private EditText photographerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photographerName = (EditText) findViewById(R.id.main_name);

        Button buttonCamera = (Button) findViewById(R.id.main_camera_button);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photographerName.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Insert the photographer's name first",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intentCamera.resolveActivity(getPackageManager()) != null) {
                        dirPhoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
                        File filePhoto = new File(dirPhoto);

                        intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                MainActivity.this.getApplicationContext().getPackageName() +
                                        ".com.araceliteixeira.myalbum.provider", filePhoto);
                        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                        startActivityForResult(intentCamera, CAMERA_CODE);
                    }
                }
            }
        });

        Button gotoGallery = (Button) findViewById(R.id.main_goto_gallery);
        gotoGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photographerName.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Insert the photographer's name first",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentGotoView = new Intent(MainActivity.this, AlbumView.class);
                    intentGotoView.putExtra("photographer", photographerName.getText().toString());
                    startActivity(intentGotoView);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_CODE) {
            Bitmap bitmap = BitmapFactory.decodeFile(dirPhoto);
            if (bitmap != null) {
                String filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
                Item item = new Item(getScaledBitmap(), filename, photographerName.getText().toString());
                ItemDAO dao = new ItemDAO(this);
                dao.dbInsert(item);
                dao.close();
            } else {
                System.out.println("Null bitmap at " + dirPhoto);
            }
        }
    }

    private Bitmap getScaledBitmap() {
        // Get the dimensions of the View
        int targetW = 300;
        int targetH = 300;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(dirPhoto, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap img = BitmapFactory.decodeFile(dirPhoto, bmOptions);
        Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                MainActivity.this.getApplicationContext().getPackageName() +
                        ".com.araceliteixeira.myalbum.provider", new File(dirPhoto));
        try {
            img = rotateImageIfRequired(img, photoURI);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = MainActivity.this.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
}
