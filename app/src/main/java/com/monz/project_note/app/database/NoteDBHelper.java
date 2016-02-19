package com.monz.project_note.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.monz.project_note.app.Label;
import com.monz.project_note.app.Note;
import com.monz.project_note.app.R;
import com.monz.project_note.app.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Андрей on 13.02.2016.
 */
public class NoteDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "NOTE_APP";
    private static final int DB_VERSION = 56;
    private Context context;
    private SQLiteDatabase db;

    public NoteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        db = getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("DB", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        db.execSQL(context.getResources().getString(R.string.create_user));
        db.execSQL(context.getResources().getString(R.string.create_note));
        db.execSQL(context.getResources().getString(R.string.create_tag_name));
        db.execSQL(context.getResources().getString(R.string.create_tag));
    }

    private static String strSeparator = "__,__";

    private static String convertArrayToString(String[] array) {
        String str = "";
        for (int i = 0; i < array.length; i++) {
            str = str + array[i];
            // Do not append comma at the end of last element
            if (i < array.length - 1) {
                str = str + strSeparator;
            }
        }
        return str;
    }

    public static String[] convertStringToArray(String str) {
        String[] arr = str.split(strSeparator);
        return arr;
    }

    public void addUser(User user, Boolean isActive) {
        ContentValues values = new ContentValues();
        values.put("NAME", user.getUsername());
        values.put("PASSWORD", user.getPassword());
        values.put("IS_ACTIVE", isActive);
        values.put("UP_TO_DATE", false);
        db.insert("USER", null, values);
    }

    public boolean updateUser(String name, boolean isActive) {
        ContentValues values = new ContentValues();
        values.put("IS_ACTIVE", isActive);
      int i =  db.update("USER", values, "NAME=" + "\"" + name + "\"", null);
        return i > 0;
    }

    //    int value = 10;
//    Cursor cursor = database.query(
//            "TABLE_X",
//            new String[] { "COLUMN_A", "COLUMN_B" },
//            "COLUMN_C = ?",
//            new String[] { Integer.toString(value) },
//            null,
//            null,
//            null);
    public String getActiveUser() {
        String result = "";
        String[] from = {"IS_ACTIVE", "NAME", "PASSWORD", "UP_TO_DATE"};
        String where = "IS_ACTIVE = ?";
        String[] whereArgs = {Integer.toString(1)};
        Log.i("TAG", "GETUSER");
        Cursor cursor = db.query(true, "USER", from, where, whereArgs, null, null, null, null);
        // Log.i("TAG", Boolean.toString(cursor.moveToNext()));
        while (cursor.moveToNext()) {
            result = cursor.getString(1);
            Log.i("TAG", "USERNAME");
            Log.i("TAG", result);
        }
        return result;
    }

    public void addLabels(String name) {
        ContentValues values = new ContentValues();
        String[] arr = Label.getLabels().toArray(new String[0]);
        values.put("TAGS", convertArrayToString(arr));
        values.put("USERNAME", name);
        values.put("UP_TO_DATE", false);
        db.insert("TAG", null, values);
        Log.i("TAG", "ONCREATELABELS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    public void updateLabels(String name) {
        ContentValues values = new ContentValues();
        String[] arr = Label.getLabels().toArray(new String[0]);
        values.put("TAGS", convertArrayToString(arr));
        values.put("UP_TO_DATE", false);
        db.update("TAG", values, "USERNAME=" + "\"" + name + "\"", null);
    }

    public Boolean isLabelsCreated(String username) {
        String str = "";
        String[] from = {"TAGS", "UP_TO_DATE", "USERNAME"};
        String where = "USERNAME=?";
        String[] whereArgs = {username};
        Cursor cursor = db.query(true, "TAG", from, where, whereArgs, null, null, null, null);
        while (cursor.moveToNext()) {
            str = cursor.getString(2);
        }
        Log.i("TAG", "PREPARE");
        Log.i("TAG", str);
        return str.equals("");
    }

    public ArrayList<String> getLabels(String username) {
        ArrayList<String> result = new ArrayList<>();
        String[] from = {"TAGS", "UP_TO_DATE", "USERNAME"};
        String where = "USERNAME=?";
        String[] whereArgs = {username};
        Cursor cursor = db.query(true, "TAG", from, where, whereArgs, null, null, null, null);
        while (cursor.moveToNext()) {
            result = new ArrayList<>(Arrays.asList(convertStringToArray(cursor.getString(0))));
        }
        return result;
    }

    public void addNote(Note note) {
        ContentValues values = new ContentValues();
        values.put("USERNAME", note.getAuthor());
        values.put("COLOR", note.getColor());
        values.put("TITLE", note.getTitle());
        values.put("CONTENT", note.getText());
        values.put("NUMBER", note.getId());
        values.put("PRIVATE", !note.isCommon_access());
        values.put("DATE", note.getDate());
        values.put("UP_TO_DATE", false);
        String[] arr = note.getLabels().toArray(new String[0]);
        values.put("LABEL", convertArrayToString(arr));
        db.insert("NOTE", null, values);
    }

    public void updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put("USERNAME", note.getAuthor());
        values.put("COLOR", note.getColor());
        values.put("TITLE", note.getTitle());
        values.put("CONTENT", note.getText());
        values.put("NUMBER", note.getId());
        values.put("PRIVATE", !note.isCommon_access());
        values.put("DATE", note.getDate());
        values.put("LABEL", convertArrayToString((String[]) note.getLabels().toArray(new String[0])));
        values.put("UP_TO_DATE", false);
        db.update("NOTE", values, "NUMBER=" + note.getId(), null);
        Log.i("TAG", "UPDATED");
    }

    public void deleteNote(Note note) {
        Log.v("NOTE_DB", db.delete("NOTE", "NUMBER=" + note.getId(), null) + "");
    }

    public List<Note> getNotes(String username) {
        List<Note> result = new ArrayList<Note>();
        String[] from = {"USERNAME", "COLOR", "TITLE", "CONTENT", "PRIVATE", "UP_TO_DATE", "DATE", "NUMBER", "LABEL"};
        String where = "USERNAME=?";
        String[] whereArgs = {username};
        Cursor cursor = db.query(true, "NOTE", from, where, whereArgs, null, null, null, null);
        while (cursor.moveToNext()) {
            ArrayList<String> arr = new ArrayList<>();
            if (!cursor.getString(8).equals("")) {
                arr = new ArrayList<>(Arrays.asList(convertStringToArray(cursor.getString(8))));
            }
            Note note = new Note(
                    cursor.getInt(7),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4) == 0,
                    cursor.getString(1),
                    cursor.getString(0),
                    cursor.getString(6),
                    arr
            );
            result.add(note);
        }
        return result;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS USER");
        db.execSQL("DROP TABLE IF EXISTS NOTE");
        db.execSQL("DROP TABLE IF EXISTS TAG");
        db.execSQL("DROP TABLE IF EXISTS TAG_NAME");
        onCreate(db);
    }
}
