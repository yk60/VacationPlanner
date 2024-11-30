package edu.sjsu.android.vacationplanner.group;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.sjsu.android.vacationplanner.MainActivity;
import edu.sjsu.android.vacationplanner.R;
import edu.sjsu.android.vacationplanner.User;


public class GroupListFragment extends Fragment {

    private final Uri CONTENT_URI2 = Uri.parse("content://edu.sjsu.android.vacationplanner.GroupProvider");
    private final Uri CONTENT_URI_members = CONTENT_URI2.buildUpon().appendPath("members").build();

    private ListView userListView;
    Activity context;
    View view;
    private LinearLayout hostNameLayout;
    private TextView nameOfHost;
    private String hostname;

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
        view = inflater.inflate(R.layout.fragment_group_list, container, false);

        userListView = view.findViewById(R.id.membersListView);

        // Set adapter
        MembersListAdapter adapter = new MembersListAdapter(context, User.usersList);
        userListView.setAdapter(adapter);

        // show that todos are empty if there are none
        TextView emptyText = view.findViewById(R.id.emptyMembersList);
        userListView.setEmptyView(emptyText);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // initialize group members list by getting the information from database
        User.usersList = initializeGroup();

        Button button = view.findViewById(R.id.addMembers_button);
        hostNameLayout = view.findViewById(R.id.hostNameLayout);
        // if (user doesn't have a group yet) OR (user has a group, and is the host)
        if ((MainActivity.getGroupID() == 0) || (MainActivity.getHostID() > 0)) {
            button.setOnClickListener(v -> {
                NavController controller = NavHostFragment.findNavController(this);
                controller.navigate(R.id.addMembersActivity);
            });
            hostNameLayout.setVisibility(View.GONE);
        } else {
            // if user has a group, but is not the host
            button.setVisibility(View.GONE);
            hostNameLayout.setVisibility(View.VISIBLE);
            // Initialize widgets
            nameOfHost = view.findViewById(R.id.hostName);
            String s = "Host: " + hostname;
            nameOfHost.setText(s);

        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        // Set adapter
        MembersListAdapter adapter = new MembersListAdapter(context, User.usersList);
        userListView.setAdapter(adapter);
    }

    @SuppressLint("Range")
    private ArrayList<User> initializeGroup() {
        // Sort by username
        ArrayList<User> groupList = new ArrayList<>();
        int isHost = 0;
        String memberName = "";

        final String[] selectColumns = {"memberName", "memProfilePicID", "groupID", "isHost"};
        try (Cursor c = requireActivity().getContentResolver().
                query(CONTENT_URI_members, null, null, selectColumns, "groupID")) {

            assert c != null;
            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(c.getColumnIndex("groupID"));
                    if (id == MainActivity.getGroupID()) {
                        memberName = c.getString(c.getColumnIndex("memberName"));
                        int profilePic = c.getInt(c.getColumnIndex("memProfilePicID"));
                        isHost = c.getInt(c.getColumnIndex("isHost"));
                        if (isHost > 0) {
                            hostname = memberName;
                        }
                        groupList.add(new User(memberName, profilePic));
                    }
                } while (c.moveToNext());
            }
        }

        return groupList;
    }



}