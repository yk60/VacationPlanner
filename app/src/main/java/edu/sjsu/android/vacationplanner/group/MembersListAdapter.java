package edu.sjsu.android.vacationplanner.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import edu.sjsu.android.vacationplanner.R;
import edu.sjsu.android.vacationplanner.User;

public class MembersListAdapter extends ArrayAdapter<User> {

    public MembersListAdapter(@NonNull Context context, List<User> users) {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_layout_users, parent, false);
        }

        TextView username = convertView.findViewById(R.id.username);
        ImageView profileImage= convertView.findViewById(R.id.profileImage);

        username.setText(user.getUsername());
        profileImage.setImageResource(user.getProfilePicID());

        return convertView;
    }
}
