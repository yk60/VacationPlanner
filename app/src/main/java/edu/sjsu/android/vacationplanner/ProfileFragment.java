package edu.sjsu.android.vacationplanner;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private TextView username;
    private CircleImageView profileImage;

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
        logoutButton = view.findViewById(R.id.logoutButton);

        // set text / image based on the user's info in database
        setProfileInformation();

        // set OnClickListener
        logoutButton.setOnClickListener(view1 -> {
            NavController controller = NavHostFragment.findNavController(this);
            controller.navigate(R.id.loginActivity);
        });

        return view;
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