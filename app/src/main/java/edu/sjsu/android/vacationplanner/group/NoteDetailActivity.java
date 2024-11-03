package edu.sjsu.android.vacationplanner.group;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import edu.sjsu.android.vacationplanner.R;

public class NoteDetailActivity extends AppCompatActivity {

    private EditText titleEditText, descEditText;
    private int selectedNoteColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        initWidgets();

        // note color
        initColors();
        selectedNoteColor = R.color.grey_light; // default note color

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

        if (title.isEmpty()) {
            Toast.makeText(view.getContext(), "Please enter a title", Toast.LENGTH_LONG).show();
        } else if (desc.isEmpty()) {
            Toast.makeText(view.getContext(), "Please enter a note", Toast.LENGTH_LONG).show();
        } else {
            int id = Note.noteArrayList.size();
            Note newNote = new Note(id, title, desc, selectedNoteColor);
            Note.noteArrayList.add(newNote);
            finish();
        }
    }

   private void initColors(){
        final LinearLayout colorLayout = findViewById(R.id.colorLayout);
        final ImageView imageColor1 = colorLayout.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = colorLayout.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = colorLayout.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = colorLayout.findViewById(R.id.imageColor4);

        colorLayout.findViewById(R.id.viewColor1).setOnClickListener(view -> {
            selectedNoteColor=R.color.grey_light;
            imageColor1.setImageResource(R.drawable.round_check_circle);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
        });

       colorLayout.findViewById(R.id.viewColor2).setOnClickListener(view -> {
           selectedNoteColor=R.color.aquamarine;
           imageColor2.setImageResource(R.drawable.round_check_circle);
           imageColor1.setImageResource(0);
           imageColor3.setImageResource(0);
           imageColor4.setImageResource(0);
       });

       colorLayout.findViewById(R.id.viewColor3).setOnClickListener(view -> {
           selectedNoteColor=R.color.melon;
           imageColor3.setImageResource(R.drawable.round_check_circle);
           imageColor1.setImageResource(0);
           imageColor2.setImageResource(0);
           imageColor4.setImageResource(0);
       });

       colorLayout.findViewById(R.id.viewColor4).setOnClickListener(view -> {
           selectedNoteColor=R.color.wheat;
           imageColor4.setImageResource(R.drawable.round_check_circle);
           imageColor1.setImageResource(0);
           imageColor2.setImageResource(0);
           imageColor3.setImageResource(0);
       });
    }

}