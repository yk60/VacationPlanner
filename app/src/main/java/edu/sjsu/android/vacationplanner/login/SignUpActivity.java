package edu.sjsu.android.vacationplanner.login;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import edu.sjsu.android.vacationplanner.R;
import edu.sjsu.android.vacationplanner.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private final Uri CONTENT_URI = Uri.parse("content://edu.sjsu.android.vacationplanner");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signupButton.setOnClickListener(this::signUp);
        binding.loginButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
        });


    }

    private void signUp(View view) {
        String username = binding.usernameEdit.getText().toString();
        if (inputIsValid()) {
            if (nameAvailable(username)) {
                addUser(view);
            } else {
                Toast.makeText(this, "Username already exists.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("Range")
    private boolean nameAvailable(String username) {
        String selection = "Select * from users where name = " + username;
        String result1 = "";
        try (Cursor c = getContentResolver().
                query(CONTENT_URI, null, selection, null, "name")) {
            assert c != null;
            if (c.moveToFirst()) {
                do {
                    result1 = c.getString(c.getColumnIndex("name"));
                    if (result1.equals(username)) {
                        break;
                    }
                } while (c.moveToNext());
            }
        }
        return !result1.equals(username);
    }

    public void addUser(View view) {
        ContentValues values = new ContentValues();
        values.put("name", binding.usernameEdit.getText().toString());
        values.put("password", binding.passwordEdit.getText().toString());

        // initialize with random profile pic
        int[] profileImgs = new int[]{R.drawable.profile5, R.drawable.profile2,R.drawable.profile3,R.drawable.profile4, R.drawable.profile1};
        int rnd = new Random().nextInt(profileImgs.length);
        values.put("profilePicID", profileImgs[rnd]);

        values.put("groupID", 0); // indicates no group
        values.put("hostID", 0); // indicates they're not host

        // Toast message if successfully inserted
        if (getContentResolver().insert(CONTENT_URI, values) != null) {
            Toast.makeText(this, "Signed up successfully!", Toast.LENGTH_LONG).show();
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    private boolean inputIsValid() {
        String username = binding.usernameEdit.getText().toString();
        String password = binding.passwordEdit.getText().toString();
        String passwordConfirm = binding.confirmPasswordEdit.getText().toString();

        if (!username.isEmpty() && !password.isEmpty() && !passwordConfirm.isEmpty()) {
            if (password.equals(passwordConfirm)) {
                return true;
            }
            else {
                Toast.makeText(this, "Passwords do not match, try again", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else  {
            Toast.makeText(this, "Please enter in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}