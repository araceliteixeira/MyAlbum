package com.araceliteixeira.myalbum;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.araceliteixeira.myalbum.model.Item;

import java.util.ArrayList;
import java.util.List;

public class AlbumView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);

        GridView gridView = (GridView) findViewById(R.id.album_grid);
        List<Item> items = new ArrayList<>();

        AlbumAdapter adapter=new AlbumAdapter(this, R.layout.activity_gridview, items);
        gridView.setAdapter(adapter);
    }
}
