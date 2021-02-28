package com.maad.notaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends ParentActivity {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private ArrayList<NoteModel> notes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.all_notes);
    }

    public void openAddNoteActivity(View view) {
        Intent i = new Intent(this, NoteDetailsActivity.class);
        startActivity(i);
    }

    private void getNotes() {
        notes.clear();
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM note", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String desc = cursor.getString(2);
            notes.add(new NoteModel(id, title, desc));
        }
        listNotes();
    }

    private void listNotes() {
        View view = findViewById(R.id.layout_no_notes);
        if (notes.size() == 0)
            view.setVisibility(View.VISIBLE);
        else {
            view.setVisibility(View.INVISIBLE);
            adapter = new NoteAdapter(this, notes);
            recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setAdapter(adapter);
            swipeToDelete();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNotes();
    }

    private void swipeToDelete() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0
                , ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView
                    , @NonNull RecyclerView.ViewHolder viewHolder
                    , @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder
                    , int direction) {
                int position = viewHolder.getAdapterPosition();
                showDeleteDialog(position);
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(R.string.delete_dialog_title)
                .setMessage(R.string.delete_dialog_message)
                .setPositiveButton(R.string.delete_dialog_positive, (dialog, which) -> {
                    deleteFromDB(position);
                    notes.remove(position);
                    adapter.notifyDataSetChanged();
                    if (notes.size() == 0) {
                        View view = findViewById(R.id.layout_no_notes);
                        view.setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton(R.string.delete_dialog_negative, (dialog, which) -> {
                    adapter.notifyItemChanged(position);
                })
                .setCancelable(false)
                .show();
    }

    private void deleteFromDB(int position) {
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] args = {String.valueOf(notes.get(position).getId())};
        int deletedRows = db.delete("note", "_id==?", args);
        if (deletedRows > 0)
            Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}