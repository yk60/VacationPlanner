package edu.sjsu.android.vacationplanner.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.sjsu.android.vacationplanner.R;


public class ToDosFragment extends Fragment {

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

}