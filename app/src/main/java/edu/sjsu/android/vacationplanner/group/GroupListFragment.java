package edu.sjsu.android.vacationplanner.group;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import edu.sjsu.android.vacationplanner.R;
import edu.sjsu.android.vacationplanner.User;


public class GroupListFragment extends Fragment {

    private ArrayList<User> usersList;
    public static final String TITLE = "Group Members";

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usersList = new ArrayList<>();

        // TODO: this list is just for testing
        usersList.add(new User("alice477", "Alice", "Green", "p@ssW0rd", R.drawable.ic_launcher_background));
        usersList.add(new User("bobcat", "Robert", "Frost", "p@ssW0rd2", R.drawable.ic_launcher_background));
        usersList.add(new User("cinnamonBagel", "Christy", "Berger", "p@ssW0rd3", R.drawable.ic_launcher_background));
        usersList.add(new User("donaldDuck", "Don", "Bluecheez", "p@ssW0rd4", R.drawable.ic_launcher_background));
    }

    public void onClick(int position){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new GroupListAdapter(usersList));
            GroupListAdapter adapter = new GroupListAdapter(usersList);
            adapter.setListener(this::onClick);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

}