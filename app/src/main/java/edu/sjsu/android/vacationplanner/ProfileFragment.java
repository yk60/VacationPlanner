package edu.sjsu.android.vacationplanner;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.sjsu.android.vacationplanner.login.LoginActivity;

public class ProfileFragment extends Fragment {


    private final Uri CONTENT_URI = Uri.parse("content://edu.sjsu.android.vacationplanner");
    private TextView username;
    private CircleImageView profileImage;
    private Button editProfilePageButton;
    private Button editPasswordButton;
    private ImageButton editProfilePicButton;

    private Button doneButton;
    private Button logoutButton;


    public ProfileFragment() {
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // initialize widgets
        username = view.findViewById(R.id.username);
        profileImage = view.findViewById(R.id.profileImage);
        editProfilePageButton = view.findViewById(R.id.editProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        editPasswordButton = view.findViewById(R.id.editPasswordButton);
        doneButton = view.findViewById(R.id.doneButton);
        editProfilePicButton = view.findViewById(R.id.editProfilePic);

        // set text / image based on the user's info in database
        setProfileInformation();

        // set OnClickListeners
        editProfilePageButton.setOnClickListener(this::editProfile);
        logoutButton.setOnClickListener(view1 -> {
            NavController controller = NavHostFragment.findNavController(this);
            controller.navigate(R.id.loginActivity);
        });

        return view;
    }

    private void editProfile(View view) {
        // set visibility of profile information to GONE
        editProfilePageButton.setVisibility(View.GONE);
        logoutButton.setVisibility(View.GONE);

        // set visibility of editing layout to VISIBLE
        doneButton.setVisibility(View.VISIBLE);
        editProfilePicButton.setVisibility(View.VISIBLE);
        editPasswordButton.setVisibility(View.VISIBLE);

        editProfilePicButton.setOnClickListener( v -> {
            // TODO:
        });

        // set OnClickListener to done button
        doneButton.setOnClickListener(this::doneEditing);


    }

    private void doneEditing(View view) {

        // TODO: update to set new profile pic in users database
        ContentValues values = new ContentValues();
        //values.put("profilePicID", newImage);
        //requireActivity().getContentResolver().update(CONTENT_URI, values, "name = " + MainActivity.getUsername(), null);


        // set visibility of editing layout to GONE
        doneButton.setVisibility(View.GONE);
        editProfilePicButton.setVisibility(View.GONE);
        editPasswordButton.setVisibility(View.GONE);

        // set visibility of profile information to VISIBLE
        logoutButton.setVisibility(View.VISIBLE);
        editProfilePageButton.setVisibility(View.VISIBLE);

        setProfileInformation();
    }

    @Override
    public void onResume() {
        super.onResume();
        setProfileInformation();
    }

    private void setProfileInformation() {
        username.setText(MainActivity.getUsername());
        profileImage.setImageResource(MainActivity.getProfilePicID());
    }
}