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

import edu.sjsu.android.vacationplanner.R;


public class ToDosFragment extends Fragment {

    private GridView noteGridView;
    Activity context;

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
        View view = inflater.inflate(R.layout.fragment_to_dos, container, false);

        // Initialize Notes widgets
        noteGridView = view.findViewById(R.id.todos_gridView);
        // Set NoteAdapter
        NoteAdapter noteAdapter = new NoteAdapter(requireActivity().getApplicationContext(), Note.noteArrayList);
        noteGridView.setAdapter(noteAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ImageButton button = context.findViewById(R.id.newNoteButton);
        button.setOnClickListener(this::newNote);
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
        NoteAdapter noteAdapter = new NoteAdapter(requireActivity().getApplicationContext(), Note.noteArrayList);
        noteGridView.setAdapter(noteAdapter);
    }

}