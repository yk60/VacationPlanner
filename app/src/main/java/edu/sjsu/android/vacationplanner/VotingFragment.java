package edu.sjsu.android.vacationplanner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class VotingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private RecyclerView recyclerView;
    private VoteAdapter voteAdapter;
    private LinkedHashMap<String, Integer> voteCountMap;
    private int page;

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
        voteCountMap = new LinkedHashMap<>();
        voteCountMap.put("Place 1", 0);
        voteCountMap.put("Place 2", 0);
        voteCountMap.put("Place 3", 0);
        voteCountMap.put("Place 4", 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voting, container, false);
        recyclerView = view.findViewById(R.id.grid_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns

        List<MyPlace> placeList = new ArrayList<>();
        placeList.add(new MyPlace("Place 1", null));
        placeList.add(new MyPlace("Place 2", null));
        placeList.add(new MyPlace("Place 3", null));
        placeList.add(new MyPlace("Place 4", null));

        voteAdapter = new VoteAdapter(placeList, voteCountMap);
        recyclerView.setAdapter(voteAdapter);

        Button viewResultsButton = view.findViewById(R.id.view_results);
        viewResultsButton.setOnClickListener(v -> {
            StringBuilder results = new StringBuilder();
//            results.append("Page ").append(page).append(" Results:\n");
            results.append(" Results:\n");

            for (String place : voteCountMap.keySet()) {
                results.append(place).append(": ").append(voteCountMap.get(place)).append("\n");
            }
            Toast.makeText(getContext(), results.toString(), Toast.LENGTH_LONG).show();
        });
        return view;
    }
}