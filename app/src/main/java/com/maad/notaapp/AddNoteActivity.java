package com.maad.notaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class AddNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_save_note) {
            saveNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNote() {
        EditText titleET = findViewById(R.id.et_title);
        EditText descET = findViewById(R.id.et_description);

        String writtenTitle = titleET.getText().toString();
        String writtenDesc = descET.getText().toString();

        if (writtenTitle.isEmpty())
            titleET.setError(getString(R.string.required_field));
        else {
            ContentValues values = new ContentValues();
            values.put("title", writtenTitle);
            values.put("description", writtenDesc);

            DBHelper helper = new DBHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();
            long id = db.insert("note", null, values);
            if (id != -1){
                Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show();
                finish();
            }


        }

    }

}