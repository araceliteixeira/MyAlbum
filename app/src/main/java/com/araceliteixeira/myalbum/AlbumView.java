package com.araceliteixeira.myalbum;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.araceliteixeira.myalbum.DAO.ItemDAO;
import com.araceliteixeira.myalbum.model.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlbumView extends AppCompatActivity {
    List<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);

        String name = getIntent().getStringExtra("photographer");

        TextView photographer = (TextView) findViewById(R.id.album_photographer);
        photographer.setText("Photographer: " + name);

        ItemDAO dao = new ItemDAO(this);
        items = dao.dbSearchByPhotographer(name);
        dao.close();

        AlbumAdapter adapter = new AlbumAdapter(this, R.layout.activity_gridview, items);

        final GridView gridView = (GridView) findViewById(R.id.album_grid);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                items.get(position).toggleSelected();
                ((AlbumAdapter) gridView.getAdapter()).notifyDataSetChanged();
            }
        });
        gridView.setAdapter(adapter);

        Button backButton = (Button) findViewById(R.id.album_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final Button smsButton = (Button) findViewById(R.id.album_send_sms);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "The photo session was fantastic, please look ";
                ArrayList<Uri> uris = new ArrayList<>();
                boolean sendSms = false;
                for (Item i: items) {
                    if (i.isSelected()) {
                        message += "photo " + i.getLabel() + ", ";

                        try {
                            File file = new File(getExternalFilesDir(null) + "/" + i.getLabel());
                            FileOutputStream fOut = new FileOutputStream(file);
                            i.getImage().compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                            fOut.flush();
                            fOut.close();
                            Uri photoURI = FileProvider.getUriForFile(AlbumView.this,
                                    AlbumView.this.getApplicationContext().getPackageName() +
                                            ".com.araceliteixeira.myalbum.provider", file);
                            uris.add(photoURI);
                            sendSms = true;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (sendSms) {
                    message += (uris.size() > 1 ? "they are incredible." : "it is incredible.");

                    Intent smsIntent = new Intent(Intent.ACTION_SEND);
                    smsIntent.setType("text/plain");
                    smsIntent.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(smsIntent);

                    Intent mmsIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    mmsIntent.setType("image/jpeg");
                    mmsIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    mmsIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(mmsIntent);
                } else {
                    Toast.makeText(AlbumView.this, "Select at least one photo",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
