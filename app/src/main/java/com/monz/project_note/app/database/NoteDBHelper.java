package com.monz.project_note.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.monz.project_note.app.Label;
import com.monz.project_note.app.Note;
import com.monz.project_note.app.R;
import com.monz.project_note.app.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoteDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "NOTE_APP";

    private static final int DB_VERSION = 65;

    private Context context;

    private SQLiteDatabase db;

    // Создаем объект ДБ
    public NoteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        db = getWritableDatabase();
    }

    // Создаем таблицы БД
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(context.getResources().getString(R.string.create_user));
        db.execSQL(context.getResources().getString(R.string.create_note));
        db.execSQL(context.getResources().getString(R.string.create_tag));
    }

    // Разделитель, который используется для хранения строкового массива
    // в БД как одного объекта типа String
    private static String strSeparator = "__,__";

    // Преобразуем массив в строку
    private static String convertArrayToString(String[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length - 1; i++) {
            sb.append(array[i]);
            sb.append(strSeparator);
        }
        if (array.length > 0)
            sb.append(array[array.length - 1]);
        return sb.toString();
    }

    // Преобразуем строку в массив
    public static String[] convertStringToArray(String str) {
        String[] arr = str.split(strSeparator);
        return arr;
    }

    public boolean userExist(String name){
        String[] from = {"NAME"};
        String where = "NAME = ?";
        String[] whereArgs = { name };
        Cursor cursor = db.query(true, "USER", from, where, whereArgs, null, null, null, null);
        while (cursor.moveToNext()) {
            return true;
        }
        return false;
    }
    public void addUser(User user, Boolean isActive) {
        ContentValues values = new ContentValues();
        values.put("NAME", user.getUsername());
        values.put("PASSWORD", user.getPassword());
        values.put("IS_ACTIVE", isActive);
        db.insert("USER", null, values);
    }

    public boolean updateUser(String name, boolean isActive) {
        ContentValues values = new ContentValues();
        values.put("IS_ACTIVE", isActive);
        int i = db.update("USER", values, "NAME=" + "\"" + name + "\"", null);
        return i > 0;
    }

    // Активный юзер - юзер, который в данный момент залогинен в приложении
    public String getActiveUser() {
        String result = "";
        String[] from = {"IS_ACTIVE", "NAME", "PASSWORD"};
        String where = "IS_ACTIVE = ?";
        String[] whereArgs = {Integer.toString(1)};
        Cursor cursor = db.query(true, "USER", from, where, whereArgs, null, null, null, null);
        while (cursor.moveToNext()) {
            result = cursor.getString(1);
        }
        return result;
    }

    public void addLabels(String name) {
        ContentValues values = new ContentValues();
        String[] arr = Label.getLabels().toArray(new String[0]);
        values.put("TAGS", convertArrayToString(arr));
        values.put("USERNAME", name);
        db.insert("TAG", null, values);
    }

    public void updateLabels(String name) {
        ContentValues values = new ContentValues();
        String[] arr = Label.getLabels().toArray(new String[0]);
        values.put("TAGS", convertArrayToString(arr));
        db.update("TAG", values, "USERNAME=" + "\"" + name + "\"", null);
    }

    public Boolean isLabelsCreated(String username) {
        String str = "";
        String[] from = {"TAGS", "USERNAME"};
        String where = "USERNAME=?";
        String[] whereArgs = {username};
        Cursor cursor = db.query(true, "TAG", from, where, whereArgs, null, null, null, null);
        while (cursor.moveToNext()) {
            str = cursor.getString(1);
        }
        return str.equals("");
    }

    public ArrayList<String> getLabels(String username) {
        ArrayList<String> result = new ArrayList<>();
        String[] from = {"TAGS", "USERNAME"};
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
        values.put("LABEL", convertArrayToString(note.getLabels().toArray(new String[0])));
        db.update("NOTE", values, "NUMBER=" + note.getId(), null);
    }

    public void deleteNote(Note note) {
        db.delete("NOTE", "NUMBER=" + note.getId(), null);
    }

    public List<Note> getNotes(String username) {
        List<Note> result = new ArrayList<>();
        String[] from = {"USERNAME", "COLOR", "TITLE", "CONTENT", "PRIVATE",  "DATE", "NUMBER", "LABEL"};
        String where = "USERNAME=?";
        String[] whereArgs = {username};
        Cursor cursor = db.query(true, "NOTE", from, where, whereArgs, null, null, null, null);
        while (cursor.moveToNext()) {
            ArrayList<String> arr = new ArrayList<>();
            if (!cursor.getString(7).equals("")) {
                arr = new ArrayList<>(Arrays.asList(convertStringToArray(cursor.getString(7))));
            }
            Note note = new Note(
                    cursor.getInt(6),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4) == 0,
                    cursor.getString(1),
                    cursor.getString(0),
                    cursor.getString(5),
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
