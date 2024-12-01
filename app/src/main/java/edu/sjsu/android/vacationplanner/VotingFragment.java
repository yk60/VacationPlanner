package edu.sjsu.android.vacationplanner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class VotingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private RecyclerView recyclerView;
    private VoteAdapter voteAdapter;
    private int page;
    private ArrayList<MyPlace> placeList;
    private SharedViewModel sharedViewModel;


    public VotingFragment() {
    }

    public static VotingFragment newInstance(int page) {
        VotingFragment fragment = new VotingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page = getArguments().getInt(ARG_PARAM1, -1);
        }
       
        placeList = new ArrayList<>();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voting, container, false);
        recyclerView = view.findViewById(R.id.grid_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns
        // reset vote count everytime fragment is created


        sharedViewModel.getVoteList().observe(getViewLifecycleOwner(), voteList -> {

            if (voteList != null && !voteList.isEmpty()) {
                placeList = new ArrayList<>(voteList);
                placeList.clear();
                for (MyPlace place : voteList) {
                    placeList.add(new MyPlace(place.getName(), place.getImage()));
                }
            } else {
                ArrayList<MyPlace> placeList = new ArrayList<>();
                placeList.add(new MyPlace("Place 1", null));
                placeList.add(new MyPlace("Place 2", null));
                placeList.add(new MyPlace("Place 3", null));
                placeList.add(new MyPlace("Place 4", null));
            }

            voteAdapter = new VoteAdapter(placeList, sharedViewModel);
            recyclerView.setAdapter(voteAdapter);
        });

        sharedViewModel.getVoteCountMap().observe(getViewLifecycleOwner(), voteCountMap -> {
            if (voteAdapter != null) {
                voteAdapter.updateVoteCountMap(voteCountMap);
            }
        });


        Button viewResultsButton = view.findViewById(R.id.view_results);
        viewResultsButton.setOnClickListener(v -> {
            StringBuilder results = new StringBuilder();
            results.append(" Results:\n");
            LinkedHashMap<String, Integer> voteCountMap = sharedViewModel.getVoteCountMap().getValue();
            if (voteCountMap != null) {
                for (String place : voteCountMap.keySet()) {
                    results.append(place).append(": ").append(voteCountMap.get(place)).append("\n");
                }
            }
            Toast.makeText(getContext(), results.toString(), Toast.LENGTH_LONG).show();
        });
        return view;
    }

    public void setPlaceList(ArrayList<MyPlace> placeList) {
        this.placeList = placeList;
        if (voteAdapter != null) {
            ArrayList<MyPlace> tempPlaceList = new ArrayList<>();
            for (MyPlace place : placeList) {
                tempPlaceList.add(new MyPlace(place.getName(), place.getImage()));
            }
            placeList = tempPlaceList;
            voteAdapter.updatePlaceList(this.placeList);
        }
    }
}