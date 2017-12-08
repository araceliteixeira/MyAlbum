package com.araceliteixeira.myalbum;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.araceliteixeira.myalbum.model.Item;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_CODE = 990;
    private String dirPhoto;
    private ImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonCamera = (Button) findViewById(R.id.main_camera_button);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intentCamera.resolveActivity(getPackageManager()) != null) {
                    File filePhoto = null;
                    try {
                        filePhoto = createImageFile();
                    } catch (IOException ex) {
                        Toast.makeText(MainActivity.this, "Error occurred while creating the file",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (filePhoto != null) {
                        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(filePhoto));
                        startActivityForResult(intentCamera, CAMERA_CODE);
                    }
                }
            }
        });

        Button gotoGallery = (Button) findViewById(R.id.main_goto_gallery);
        gotoGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentGotoView = new Intent(MainActivity.this, AlbumView.class);
                startActivity(intentGotoView);

            }
        });
    }

    private File createImageFile() throws IOException {
        File image = File.createTempFile(
                "PHOTO_" + System.currentTimeMillis(),          /* prefix */
                ".jpg",                                         /* suffix */
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)     /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        dirPhoto = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_CODE) {
            Bitmap bitmap = BitmapFactory.decodeFile(dirPhoto);
            Item item = new Item(bitmap, dirPhoto);
        }
    }

    private void scaleBitmap() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

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

        Bitmap bitmap = BitmapFactory.decodeFile(dirPhoto, bmOptions);
        imageView.setImageBitmap(bitmap);
    }
}
