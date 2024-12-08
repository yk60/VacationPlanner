package edu.sjsu.android.vacationplanner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class VotingFragment extends Fragment {
    private RecyclerView recyclerView;
    private VoteAdapter voteAdapter;
    private ArrayList<MyPlace> placeList = new ArrayList<>();
    private SharedViewModel sharedViewModel;
    private TextView initialVotingMessage;

    public VotingFragment() {
    }

    public static VotingFragment newInstance(int page) {
        VotingFragment fragment = new VotingFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        // reset votelist when app restarts
//        sharedViewModel.setVoteList(new ArrayList<>());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voting, container, false);
        recyclerView = view.findViewById(R.id.grid_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns
        initialVotingMessage = view.findViewById(R.id.initial_voting_message);

        voteAdapter = new VoteAdapter(placeList, sharedViewModel);
        recyclerView.setAdapter(voteAdapter);

        sharedViewModel.getVoteList().observe(getViewLifecycleOwner(), voteList -> {
            if (voteList != null) {
                Log.d("VotingFragment", "Vote list size: " + voteList.size());
                if (!voteList.isEmpty()) {
                    initialVotingMessage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    placeList.clear();
                    for (MyPlace place : voteList) {
                        placeList.add(new MyPlace(place.getName(), place.getImage()));
                    }
                    Log.d("VotingFragment", "Success. Updated placeList: " + placeList);
                } else {
                    Log.d("VotingFragment", "Vote list is empty");
                    initialVotingMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    placeList.clear();
                }
            } else {
                Log.d("VotingFragment", "Vote list is null");
                initialVotingMessage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                placeList.clear();
            }

            voteAdapter.notifyDataSetChanged();
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
            voteAdapter.updatePlaceList(this.placeList);
        }
    }
}