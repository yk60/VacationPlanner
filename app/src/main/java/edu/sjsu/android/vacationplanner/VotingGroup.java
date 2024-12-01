package edu.sjsu.android.vacationplanner;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private boolean isResumed = false;
    private static boolean isAdapterSet = false;


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
            if (voteList != null && !voteList.isEmpty()) {
                if (!isResumed) {
                    vpAdapter.clearFragments();
                    Log.d("final_votelist", String.valueOf(sharedViewModel.getVoteList().getValue().size()));
                    if(voteList.size() > 0){
                        VotingFragment votingFragment = VotingFragment.newInstance(0);

                        ArrayList<MyPlace> singlePlaceList = new ArrayList<>();
                        singlePlaceList.add(voteList.get(0));
                        votingFragment.setPlaceList(singlePlaceList);
                        vpAdapter.addFragment(votingFragment, "test page 1");
                        Log.d("votinggroup", "created votingfragment");
                    }
                    vpAdapter.notifyDataSetChanged();
                    Log.d("votinggroup", "success");
                }
            } else {
                Log.d("votinggroup", "voteList is empty");
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;
    }
}