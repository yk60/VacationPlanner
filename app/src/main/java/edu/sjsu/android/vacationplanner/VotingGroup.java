package edu.sjsu.android.vacationplanner;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class VotingGroup extends Fragment {

    public TabLayout tabLayout;
    public ViewPager2 viewPager2;
    TabLayoutMediator tabLayoutMediator;
    private SharedViewModel sharedViewModel;
    private static ViewPagerAdapter vpAdapter;
    private static boolean isAdapterSet = false;
    private TextView initialVotingMessage;
    private ArrayList<MyPlace> placeList = new ArrayList<>();


    public VotingGroup() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("votinggroup", "oncreateview");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voting_group, container, false);
        initialVotingMessage = view.findViewById(R.id.initial_voting_message);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager2 = view.findViewById(R.id.VotingViewPager2);

        if (vpAdapter == null) {
            vpAdapter = new ViewPagerAdapter(requireActivity().getSupportFragmentManager(), getLifecycle());
        }
        if (!isAdapterSet) {
            viewPager2.setAdapter(vpAdapter);
            tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
                tab.setText(vpAdapter.getPageTitle(position));
            });
            tabLayoutMediator.attach();
            isAdapterSet = true;
        }

        sharedViewModel.getVoteList().observe(getViewLifecycleOwner(), voteList -> {
            // if voting started, update planning page
            if (sharedViewModel.isVotingStarted()) {
                initialVotingMessage.setVisibility(View.GONE);
                viewPager2.setVisibility(View.VISIBLE);

                placeList.clear();
                for (MyPlace place : voteList) {
                    placeList.add(new MyPlace(place.getName(), place.getImage()));
                }
                // create a voting fragment containing the placeList and notify the adapter
                VotingFragment votingFragment = VotingFragment.newInstance(0);
                votingFragment.setPlaceList(placeList);
                vpAdapter.clearFragments();
                vpAdapter.addFragment(votingFragment, "Voting");
                vpAdapter.notifyDataSetChanged();
            } else {
                Log.d("votinggroup", "voteList is empty");
                initialVotingMessage.setVisibility(View.VISIBLE);
                viewPager2.setVisibility(View.GONE);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check voting state when the fragment resumes
        if (sharedViewModel.isVotingStarted()) {
            initialVotingMessage.setVisibility(View.GONE);
            viewPager2.setVisibility(View.VISIBLE);

            placeList.clear();
            for (MyPlace place : sharedViewModel.getVoteList().getValue()) {
                placeList.add(new MyPlace(place.getName(), place.getImage()));
            }

            // create a voting fragment containing the placeList and notify the adapter
            VotingFragment votingFragment = VotingFragment.newInstance(0);
            votingFragment.setPlaceList(placeList);
            vpAdapter.clearFragments();
            vpAdapter.addFragment(votingFragment, "Voting");
            vpAdapter.notifyDataSetChanged();
        } else {
            initialVotingMessage.setVisibility(View.VISIBLE);
            viewPager2.setVisibility(View.GONE);
        }
    }
}