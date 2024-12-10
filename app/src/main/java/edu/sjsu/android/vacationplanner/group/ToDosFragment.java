package edu.sjsu.android.vacationplanner.group;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.sjsu.android.vacationplanner.MainActivity;
import edu.sjsu.android.vacationplanner.R;


public class ToDosFragment extends Fragment {

    private final Uri CONTENT_URI3 = Uri.parse("content://edu.sjsu.android.vacationplanner.group.NoteProvider");
    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    private GridView noteGridView;
    Activity context;
    View view;

    public ToDosFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_to_dos, container, false);

        // Initialize Notes widgets
        noteGridView = view.findViewById(R.id.todos_gridView);

        // Initialize notes array from database
        createNoteListArray();
        
        // Set NoteAdapter
        NoteAdapter noteAdapter = new NoteAdapter(requireActivity().getApplicationContext(), Note.nonDeletedNotes());
        noteGridView.setAdapter(noteAdapter);


        // show that todos are empty if there are none
        TextView emptyText = (TextView) view.findViewById(R.id.emptyList);
        noteGridView.setEmptyView(emptyText);

        // set listener on note clicked
        setOnClickListener();

        return view;
    }

    @SuppressLint("Range")
    public void createNoteListArray(){
        Note.noteArrayList.clear();

        final String[] selectColumns = {"id", "title", "desc", "color", "groupID", "deleted"};
        // Sort by groupID
        try (Cursor c = requireContext().getContentResolver().
                query(CONTENT_URI3, null, null, selectColumns, "groupID")) {
            assert c != null;
            if (c.moveToFirst()) {
                do {
                    int groupID = MainActivity.getGroupID();
                    if (c.getInt(c.getColumnIndex("groupID")) == groupID) {
                        int id = c.getInt(1);
                        String title = c.getString(2);
                        String desc = c.getString(3);
                        int color = c.getInt(4);
                        String dateDeleted = c.getString(6);

                        Date deleted = getDateFromString(dateDeleted);
                        Note note = new Note(id, title, desc, color, deleted);
                        Note.noteArrayList.add(note);
                    }
                } while (c.moveToNext());
            }
        }
    }


    private void setOnClickListener()
    {
        noteGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Note selectedNote = (Note) noteGridView.getItemAtPosition(position);
                Intent editNoteIntent = new Intent(context, NoteDetailActivity.class);
                editNoteIntent.putExtra(Note.NOTE_EDIT_EXTRA, selectedNote.getId());
                startActivity(editNoteIntent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ImageButton newNoteButton = view.findViewById(R.id.newNoteButton);
        newNoteButton.setOnClickListener(this::newNote);
    }

    public void newNote(View view){
        Intent newNoteIntent = new Intent(context, NoteDetailActivity.class);
        newNoteIntent.putExtra("isViewOrUpdate", false);
        startActivity(newNoteIntent);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // Set NoteAdapter
        NoteAdapter noteAdapter = new NoteAdapter(requireActivity().getApplicationContext(), Note.nonDeletedNotes());
        noteGridView.setAdapter(noteAdapter);
    }

    private Date getDateFromString(String s)
    {
        try {
            return dateFormat.parse(s);
        }
        catch (ParseException | NullPointerException e) {
            return null;
        }
    }

}