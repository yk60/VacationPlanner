package edu.sjsu.android.vacationplanner.group;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import edu.sjsu.android.vacationplanner.R;

public class NoteDetailActivity extends AppCompatActivity {

    private EditText titleEditText, descEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        initWidgets();

        // setOnClickListener for save note button
        ImageButton saveNoteButton = findViewById(R.id.saveNoteButton);
        saveNoteButton.setOnClickListener(this::saveNote);
    }

    private void initWidgets() {
        titleEditText = findViewById(R.id.edit_noteTitle);
        descEditText = findViewById(R.id.edit_noteContent);
    }


    public void saveNote(View view){
        String title = String.valueOf(titleEditText.getText());
        String desc = String.valueOf(descEditText.getText());
        int id = Note.noteArrayList.size();
        Note newNote = new Note(id, title, desc);
        Note.noteArrayList.add(newNote);
        finish();
    }

}