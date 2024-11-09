package edu.sjsu.android.vacationplanner.group;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import edu.sjsu.android.vacationplanner.R;
import edu.sjsu.android.vacationplanner.User;


public class GroupListFragment extends Fragment {

    public static final String TITLE = "Group Members";
    private ListView userListView;
    Activity context;

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);

        // Initialize widgets
        userListView = view.findViewById(R.id.membersListView);
        // Set adapter
        MembersListAdapter adapter = new MembersListAdapter(context, User.usersList);
        userListView.setAdapter(adapter);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Button button = context.findViewById(R.id.addMembers_button);
        button.setOnClickListener(this::editGroupList);
    }

    public void editGroupList(View view){
        Intent editGroup = new Intent(context, AddMembersActivity.class);
        startActivity(editGroup);
    }



    @Override
    public void onResume()
    {
        super.onResume();
        // Set adapter
        MembersListAdapter adapter = new MembersListAdapter(context, User.usersList);
        userListView.setAdapter(adapter);
    }




}