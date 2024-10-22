package edu.sjsu.android.vacationplanner.group;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.sjsu.android.vacationplanner.R;


public class GroupListFragment extends Fragment {


    public GroupListFragment() {
        // Required empty public constructor
    }

    // TODO: create an ArrayList for group members (to be used for RecyclerView)
    //  create a method for adding/removing group members on list

    public static GroupListFragment newInstance(String param1, String param2) {
        GroupListFragment fragment = new GroupListFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_list, container, false);
    }
}