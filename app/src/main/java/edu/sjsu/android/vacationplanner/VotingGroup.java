package edu.sjsu.android.vacationplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class VotingGroup extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    TabLayoutMediator tabLayoutMediator;

    public VotingGroup() {
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
        View view = inflater.inflate(R.layout.fragment_voting_group, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager2 = view.findViewById(R.id.VotingViewPager2);

        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(requireActivity().getSupportFragmentManager(), getLifecycle());
        vpAdapter.addFragment(new VotingFragment(), "Hotel");
        vpAdapter.addFragment(new VotingFragment(), "Restaurant");
        vpAdapter.addFragment(new VotingFragment(), "Things to do");

        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(vpAdapter.getPageTitle(position));
        });

        // set adapter
        viewPager2.setAdapter(vpAdapter);

        // attach mediator
        if (!tabLayoutMediator.isAttached()) {
            tabLayoutMediator.attach();
        }
        int currentItem = viewPager2.getCurrentItem();


        return view;
    }
    

}