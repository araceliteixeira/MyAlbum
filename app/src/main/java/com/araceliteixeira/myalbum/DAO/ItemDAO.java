package com.araceliteixeira.myalbum.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.araceliteixeira.myalbum.model.Item;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by araceliteixeira on 08/12/17.
 */

public class ItemDAO extends SQLiteOpenHelper {

    public ItemDAO(Context context) {
        super(context, "c0712150test2", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Items (id INTEGER PRIMARY KEY, image BLOB NOT NULL, label TEXT NOT NULL, photographer TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS Items";
        db.execSQL(sql);
        onCreate(db);
    }

    public void dbInsert(Item item) {
        SQLiteDatabase db = getWritableDatabase();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        item.getImage().compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bytes = bos.toByteArray();

        ContentValues itemData = new ContentValues();
        itemData.put("image", bytes);
        itemData.put("label", item.getLabel());
        itemData.put("photographer", item.getPhotographer());

        db.insert("Items", null, itemData);
    }

    public List<Item> dbSearch() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM Items;";

        Cursor c = db.rawQuery(sql, null);
        List<Item> items = new ArrayList<>();

        while (c.moveToNext()) {
            Item item = new Item();
            item.setId(c.getLong(c.getColumnIndex("id")));
            byte[] bytes = c.getBlob(c.getColumnIndex("image"));
            item.setImage(BitmapFactory.decodeByteArray(bytes, 0 , bytes.length));
            item.setLabel(c.getString(c.getColumnIndex("label")));
            item.setPhotographer(c.getString(c.getColumnIndex("photographer")));
            items.add(item);
        }
        c.close();
        return items;
    }

    public List<Item> dbSearchByPhotographer(String name) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM Items WHERE photographer = '" + name + "';";
        Cursor c = db.rawQuery(sql, null);
        List<Item> items = new ArrayList<>();

        while (c.moveToNext()) {
            Item item = new Item();
            item.setId(c.getLong(c.getColumnIndex("id")));
            byte[] bytes = c.getBlob(c.getColumnIndex("image"));
            item.setImage(BitmapFactory.decodeByteArray(bytes, 0 , bytes.length));
            item.setLabel(c.getString(c.getColumnIndex("label")));
            item.setPhotographer(c.getString(c.getColumnIndex("photographer")));
            items.add(item);
        }
        c.close();
        return items;
    }

    public void dbDelete() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Items", "", new String[]{});
    }
}
