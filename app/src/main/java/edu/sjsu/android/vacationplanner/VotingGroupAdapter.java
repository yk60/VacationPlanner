package edu.sjsu.android.vacationplanner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class VotingGroupAdapter extends FragmentStateAdapter {

    public VotingGroupAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return VotingFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
