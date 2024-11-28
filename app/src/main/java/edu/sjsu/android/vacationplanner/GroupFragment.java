package edu.sjsu.android.vacationplanner;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


import edu.sjsu.android.vacationplanner.group.GroupListFragment;
import edu.sjsu.android.vacationplanner.group.ToDosFragment;


public class GroupFragment extends Fragment {

    TabLayoutMediator tabLayoutMediator;

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

        // Initialize widgets
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.group_viewpager);

        // Initialize the tab layout
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(requireActivity().getSupportFragmentManager(), getLifecycle());
        vpAdapter.addFragment(new GroupListFragment(), "Group Members");
        vpAdapter.addFragment(new ToDosFragment(), "To Dos");

        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(vpAdapter.getPageTitle(position));
        });

        // set adapter
        viewPager.setAdapter(vpAdapter);

        // attach mediator
        if (!tabLayoutMediator.isAttached()) {
            tabLayoutMediator.attach();
        }
        return view;
    }

}