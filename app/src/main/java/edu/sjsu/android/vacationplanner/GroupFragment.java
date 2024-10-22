package edu.sjsu.android.vacationplanner;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import edu.sjsu.android.vacationplanner.group.GroupListFragment;
import edu.sjsu.android.vacationplanner.group.ItineraryFragment;


public class GroupFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.group_viewpager);

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(requireActivity().getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        vpAdapter.addFragment(new GroupListFragment(), "Group Members");
        vpAdapter.addFragment(new ItineraryFragment(), "Itinerary");

        //TODO: change last fragment on list (this was just for testing purposes)
        vpAdapter.addFragment(new SearchFragment(), "To Dos");
        viewPager.setAdapter(vpAdapter);


        return view;
    }

}