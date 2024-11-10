package edu.sjsu.android.vacationplanner;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import edu.sjsu.android.vacationplanner.databinding.ActivityMapsBinding;

public class VotingFragment extends Fragment  {
    Activity context;
    RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private VoteAdapter voteAdapter;
    private ActivityMapsBinding binding;

    public VotingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_voting);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voting, container, false);
        recyclerView = view.findViewById(R.id.grid_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns

        List<MyPlace> placeList = new ArrayList<>();
        placeList.add(new MyPlace("Place 1", null));
        placeList.add(new MyPlace("Place 2", null));
        placeList.add(new MyPlace("Place 3", null));
        placeList.add(new MyPlace("Place 4", null));

        voteAdapter = new VoteAdapter(placeList);
        recyclerView.setAdapter(voteAdapter);

        return view;
    }

//    public void onClick(){
//        goPlanning();
//    }

//    public void goPlanning(){
//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.planningLayout);
//        NavController navController = navHostFragment.getNavController();
//        navController.navigate(R.id.goToPlanning);
//    }
}