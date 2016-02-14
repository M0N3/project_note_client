package com.monz.project_note.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.monz.project_note.app.Note;
import com.monz.project_note.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Андрей on 13.02.2016.
 */
public class NoteDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "NOTE_APP";
    private static final int DB_VERSION = 24;
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
        db.insert("NOTE", null, values);
    }
    public void updateNote(Note note){
        ContentValues values = new ContentValues();
        values.put("USERNAME", note.getAuthor());
        values.put("COLOR", note.getColor());
        values.put("TITLE", note.getTitle());
        values.put("CONTENT", note.getText());
        values.put("NUMBER", note.getId());
        values.put("PRIVATE", !note.isCommon_access());
        values.put("DATE", note.getDate());
        values.put("UP_TO_DATE", false);
        db.update("NOTE",values, "NUMBER="+note.getId(), null );
        Log.i("TAG", "UPDATED");
    }

    public void deleteNote(Note note) {
        Log.v("NOTE_DB", db.delete("NOTE", "NUMBER=" + note.getId(), null) + "");
    }

    public List<Note> getNotes(String username) {
        List<Note> result = new ArrayList<Note>();
        String[] from = { "USERNAME", "COLOR", "TITLE", "CONTENT", "PRIVATE", "UP_TO_DATE", "DATE", "NUMBER"};
        String where = "USERNAME=?";
        String[] whereArgs = { username };
        Cursor cursor = db.query(true, "NOTE", from, where, whereArgs, null, null, null, null);
        while (cursor.moveToNext()) {
            Note note = new Note(
                    cursor.getInt(7),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4) == 0,
                    cursor.getString(1),
                    cursor.getString(0),
                    cursor.getString(6),
                    new ArrayList<String>()
            );
            // TODO: FIND TAGS AND ADD!
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
