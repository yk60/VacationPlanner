package edu.sjsu.android.vacationplanner.group;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import edu.sjsu.android.vacationplanner.MainActivity;
import edu.sjsu.android.vacationplanner.R;
import edu.sjsu.android.vacationplanner.User;
import edu.sjsu.android.vacationplanner.UserDB;

public class AddMembersActivity extends AppCompatActivity {

    // list of ALL users in system
    private ArrayList<User> allUsersList = new ArrayList<>();

    private final Uri CONTENT_URI1 = Uri.parse("content://edu.sjsu.android.vacationplanner");
    private final Uri CONTENT_URI2 = Uri.parse("content://edu.sjsu.android.vacationplanner.GroupProvider");


    GroupListAdapter adapter;
    private SearchView searchView;
    private boolean listIsFiltered = false;
    ArrayList<User> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);


        getAllUsers();

        // set recycler view
        RecyclerView recyclerView = findViewById(R.id.allUsersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupListAdapter(allUsersList);
        recyclerView.setAdapter(adapter);
        adapter.setListener(this::onClick);

        // set listener for done button
        ImageButton doneButton = findViewById(R.id.done);
        doneButton.setOnClickListener(view -> finish());

        // searchview
        searchView = findViewById(R.id.user_search);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
    }

    private void filterList(String text) {
        filteredList = new ArrayList<>();
        for (User user: allUsersList) {
            if (user.getUsername().toLowerCase().contains((text.toLowerCase()))){
                filteredList.add(user);
            }
        }

        if (filteredList.isEmpty()) {
            listIsFiltered = false;
            adapter.setFilteredList(allUsersList);
            Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
        } else {
            listIsFiltered = true;
            adapter.setFilteredList(filteredList);
        }
    }

    public void onClick(int position){
        addOrDeleteMember(position);
    }

    private void addOrDeleteMember(int position) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        User user;
        boolean isMember = false;

        if (listIsFiltered) {
            user = this.filteredList.get(position);
        } else {
            user = this.allUsersList.get(position);
        }

        for (User searchUser: User.usersList) {
            if (Objects.equals(searchUser.getUsername(), user.getUsername())) {
                isMember = true;
                break;
            }
        }

        if (isMember) {
            builder.setTitle("Delete Member");
            builder.setMessage("Delete this member from your group?");

            builder.setPositiveButton("Yes", (dialog, id) -> {
                // When user selects yes
                int index = User.findUserPosition(user);
                removeMember(user);
                User.usersList.remove(index);
                Toast.makeText(this, user.getUsername() + " has been removed", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("No", (dialog, id) -> {
                // When user selects no, do nothing
            });
            builder.create().show();
        } else {
            builder.setTitle("Add New Member");
            builder.setMessage("Add this member to your group?");

            builder.setPositiveButton("Yes", (dialog, id) -> {
                // When user selects yes
                boolean hasGroup = userHasGroup(user);
                if (hasGroup) {
                    Toast.makeText(this, user.getUsername() + " is already part of another group", Toast.LENGTH_SHORT).show();
                } else {
                    User.usersList.add(user);
                    addMember(user);
                    Toast.makeText(this, user.getUsername() + " has been added", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("No", (dialog, id) -> {
                // When user selects no, do nothing
            });
            builder.create().show();
        }
    }

    @SuppressLint("Range")
    private boolean userHasGroup(User user) {
        // Sort by username
        final String[] selectColumns = {"name", "groupID"};
        int groupID = -1;

        try (Cursor c = getContentResolver().
                query(CONTENT_URI1, null, null, selectColumns, "name")) {

            assert c != null;
            if (c.moveToFirst()) {
                do {
                    String userName = c.getString(c.getColumnIndex("name"));
                    if (userName.equals(user.getUsername())) {
                        groupID = c.getInt(c.getColumnIndex("groupID"));
                    }
                } while (c.moveToNext());
            } }

        return (groupID > 0);
    }


    @SuppressLint("Range")
    private void removeMember(User user) {
        // to remove from host's group list
        getContentResolver().delete(CONTENT_URI2,"memberName = ?", new String[] {user.getUsername()});

        // update usersDatabase
        ContentValues updateMem = new ContentValues();
        updateMem.put("groupID", 0); // zero means no group
        getContentResolver().update(CONTENT_URI1, updateMem, "name = ?", new String[] {user.getUsername()});
    }

    @SuppressLint("Range")
    private void addMember(User user) {
        // if user is not host yet (forming new group)
        if (MainActivity.getHostID() == 0 && MainActivity.getGroupID() == 0) {
            int id_group = (int)(Math.random() * 5000); // groupID = randomly generated number
            // update usersDatabase
            ContentValues updateHost = new ContentValues();
            updateHost.put("groupID", (id_group));
            updateHost.put("hostID", 1); // 1 means they are the host of the group, 0 means not host
            getContentResolver().update(CONTENT_URI1, updateHost, "name = ?", new String[] {MainActivity.getUsername()});

            // update groupsDatabase
            ContentValues addHost = new ContentValues();
            addHost.put("memberName", MainActivity.getUsername());
            addHost.put("memProfilePicID", MainActivity.getProfilePicID());
            addHost.put("groupID", id_group);
            addHost.put("isHost", 1); // 1 means they are the host
            getContentResolver().insert(CONTENT_URI2, addHost);

            // to insert into host's group list
            ContentValues addMember = new ContentValues();
            addMember.put("memberName", user.getUsername());
            addMember.put("memProfilePicID", user.getProfilePicID());
            addMember.put("groupID", id_group);
            addMember.put("isHost", 0); // 0 means they are not the host
            getContentResolver().insert(CONTENT_URI2, addMember);

            // update usersDatabase
            ContentValues updateMem = new ContentValues();
            updateMem.put("groupID", id_group);
            getContentResolver().update(CONTENT_URI1, updateMem, "name = ?", new String[] {user.getUsername()});

            MainActivity.updateGroupID(id_group);

        } else {
            // to insert into host's group list
            ContentValues addMember = new ContentValues();
            addMember.put("memberName", user.getUsername());
            addMember.put("memProfilePicID", user.getProfilePicID());
            addMember.put("groupID", MainActivity.getGroupID()); // groupID = hosts' groupID
            addMember.put("isHost", 0); // 0 means they are not the host
            getContentResolver().insert(CONTENT_URI2, addMember);

            // update usersDatabase
            ContentValues updateMem = new ContentValues();
            updateMem.put("groupID", MainActivity.getGroupID());
            getContentResolver().update(CONTENT_URI1, updateMem, "name = ?", new String[] {user.getUsername()});
        }

    }



    @SuppressLint("Range")
    public void getAllUsers() {

        // Sort by username
        final String[] selectColumns = {"name", "profilePicID"};

        try (Cursor c = getContentResolver().
                query(CONTENT_URI1, null, null, selectColumns, "name")) {

            assert c != null;
            if (c.moveToFirst()) {
                do {
                    String userName = c.getString(c.getColumnIndex("name"));
                    if (!userName.equals(MainActivity.getUsername())) {
                        int profilePic = c.getInt(c.getColumnIndex("profilePicID"));
                        allUsersList.add(new User(userName, profilePic));
                    }
                } while (c.moveToNext());

            } }
    }




}