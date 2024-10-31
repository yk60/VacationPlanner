package edu.sjsu.android.vacationplanner.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;
import java.util.Objects;

import edu.sjsu.android.vacationplanner.MapsActivity;
import edu.sjsu.android.vacationplanner.R;


public class ToDosFragment extends Fragment {

    private ListView noteListView;
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
        noteListView = view.findViewById(R.id.todos_listView);
        // Set NoteAdapter
        NoteAdapter noteAdapter = new NoteAdapter(requireActivity().getApplicationContext(), Note.noteArrayList);
        noteListView.setAdapter(noteAdapter);

        return view;
    }

    public void newNote(View view){
        Intent newNoteIntent = new Intent(context, NoteDetailActivity.class);
        startActivity(newNoteIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        ImageButton button = (ImageButton) context.findViewById(R.id.newNoteButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newNote(view);
            }
        });
    }

}