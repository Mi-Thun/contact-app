package edu.ewubd.contactform;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class ContactsDB extends SQLiteOpenHelper {
    public ContactsDB(Context context) {
        super(context, "ContactDB.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE contacts  ("
                + "ID TEXT PRIMARY KEY,"
                + "name TEXT,"
                + "email TEXT,"
                + "phone_home TEXT,"
                + "phone_office TEXT,"
                + "image TEXT"
                + ")";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("Write code to modify database schema here");
        // db.execSQL("ALTER table my_table  ......");
    }
    public void insertContact(String id, String name, String email, String phone_home, String phone_office, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();
        cols.put("id", id);
        cols.put("name", name);
        cols.put("email", email);
        cols.put("phone_home", phone_home);
        cols.put("phone_office", phone_office);
        cols.put("image", image);
        db.insert("contacts", null ,  cols);
        db.close();
    }
    public void updateContact(String id, String name, String email, String phone_home, String phone_office, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();
        cols.put("id", id);
        cols.put("name", name);
        cols.put("email", email);
        cols.put("phone_home", phone_home);
        cols.put("phone_office", phone_office);
        cols.put("image", image);
        db.update("contacts", cols, "id=?", new String[ ] {id} );
        db.close();
    }
    public void deleteContacts(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("contacts", "id=?", new String[ ] {id} );
        db.close();
    }
    public Cursor selectContacts(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=null;
        try {
            res = db.rawQuery(query, null);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}