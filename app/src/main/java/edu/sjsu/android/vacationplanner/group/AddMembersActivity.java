package edu.sjsu.android.vacationplanner.group;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import edu.sjsu.android.vacationplanner.R;
import edu.sjsu.android.vacationplanner.User;

public class AddMembersActivity extends AppCompatActivity {

    // list of ALL users in system
    private ArrayList<User> allUsersList;

    GroupListAdapter adapter;
    private SearchView searchView;
    private boolean listIsFiltered = false;
    ArrayList<User> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);

        // TODO: this list is just for testing
        User user1 = new User("alice477", "Alice", "Green", "p@ssW0rd", R.drawable.ic_launcher_background);
        User user2 = new User("bobcat", "Robert", "Frost", "p@ssW0rd2", R.drawable.ic_launcher_background);
        User user3 = new User("cinnamonBagel", "Christy", "Berger", "p@ssW0rd3", R.drawable.ic_launcher_background);
        User user4 = new User("donaldDuck", "Don", "Bluecheez", "p@ssW0rd4", R.drawable.ic_launcher_background);
        User user5 = new User("ellie800", "Robert", "Frost", "p@ssW0rd2", R.drawable.ic_launcher_background);
        User user6 = new User("florideToothpaste999", "Christy", "Berger", "p@ssW0rd3", R.drawable.ic_launcher_background);
        User user7 = new User("gloria264", "Don", "Bluecheez", "p@ssW0rd4", R.drawable.ic_launcher_background);

        allUsersList = new ArrayList<>();
        allUsersList.add(user1);
        allUsersList.add(user2);
        allUsersList.add(user3);
        allUsersList.add(user4);
        allUsersList.add(user5);
        allUsersList.add(user6);
        allUsersList.add(user7);

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
                User.usersList.add(user);
                Toast.makeText(this, user.getUsername() + " has been added", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("No", (dialog, id) -> {
                // When user selects no, do nothing
            });
            builder.create().show();
        }
    }


}