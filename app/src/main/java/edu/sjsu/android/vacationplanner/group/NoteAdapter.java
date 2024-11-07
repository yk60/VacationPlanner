package edu.sjsu.android.vacationplanner.group;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;
import edu.sjsu.android.vacationplanner.R;

public class NoteAdapter extends ArrayAdapter<Note> {


    public NoteAdapter(@NonNull Context context, List<Note> notes) {
        super(context, 0, notes);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Note note = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_cell, parent, false);
        }

        TextView title = convertView.findViewById(R.id.cellTitle);
        TextView desc = convertView.findViewById(R.id.cellDescription);

        LinearLayout linearLayout = convertView.findViewById(R.id.layout_note);

        title.setText(note.getTitle());
        desc.setText(note.getDescription());


        if (note.getColor() == R.color.grey_light) {
            linearLayout.setBackgroundResource(R.drawable.background_note);
        } else if (note.getColor() == R.color.aquamarine) {
            linearLayout.setBackgroundResource(R.drawable.background_note_2);
        } else if (note.getColor() == R.color.melon) {
            linearLayout.setBackgroundResource(R.drawable.background_note_3);
        } else {
            linearLayout.setBackgroundResource(R.drawable.background_note_4);
        }

        return convertView;
    }

}