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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.araceliteixeira.myalbum.DAO.ItemDAO;
import com.araceliteixeira.myalbum.model.Item;

import java.io.File;
import java.io.IOException;
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

        //ItemDAO dao = new ItemDAO(this);
        //dao.dbDelete();
        //dao.close();

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
                Item item = new Item(bitmap, filename, photographerName.getText().toString());
                ItemDAO dao = new ItemDAO(this);
                dao.dbInsert(item);
                dao.close();
            } else {
                System.out.println("Null bitmap at " + dirPhoto);
            }
        }
    }
}
