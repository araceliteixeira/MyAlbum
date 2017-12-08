package com.araceliteixeira.myalbum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.araceliteixeira.myalbum.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by araceliteixeira on 08/12/17.
 */

public class AlbumAdapter extends ArrayAdapter {
    List<Item> list = new ArrayList<>();

    public AlbumAdapter(Context context, int textViewResourceId, List items) {
        super(context, textViewResourceId, items);
        list = items;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_gridview, null);
        TextView textView = (TextView) view.findViewById(R.id.grid_view_label);
        ImageView imageView = (ImageView) view.findViewById(R.id.grid_view_image);
        textView.setText(list.get(position).getLabel());
        imageView.setImageBitmap(list.get(position).getImage());
        return view;
    }
}
