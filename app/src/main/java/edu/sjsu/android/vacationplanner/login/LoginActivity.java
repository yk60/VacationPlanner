package edu.sjsu.android.vacationplanner.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.sjsu.android.vacationplanner.MainActivity;
import edu.sjsu.android.vacationplanner.R;
import edu.sjsu.android.vacationplanner.UserDB;
import edu.sjsu.android.vacationplanner.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private final Uri CONTENT_URI = Uri.parse("content://edu.sjsu.android.vacationplanner");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        binding.loginButton.setOnClickListener(view -> {
            String username = binding.usernameEnter.getText().toString();
            String password = binding.passwordEnter.getText().toString();

            if (username.isEmpty() && password.isEmpty()) {
                Toast.makeText(this, "Please enter in all fields", Toast.LENGTH_SHORT).show();
            } else {

                Boolean checkUser = checkUser(username, password);
                if (checkUser) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent startApp = new Intent(getApplicationContext(), MainActivity.class);
                    startApp.putExtra("username", username);
                    startActivity(startApp);
                } else {
                    Toast.makeText(this, "Incorrect username or password.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.signUpButton.setOnClickListener(view -> {
            Intent signupIntent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(signupIntent);
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @SuppressLint("Range")
    private Boolean checkUser(String username, String password) {
        String selection = "Select * from users where name = " + username + " and password = " + password;
        String result1 = "";
        String result2 = "";
        try (Cursor c = getContentResolver().
                query(CONTENT_URI, null, selection, null, "name")) {
            assert c != null;
            if (c.moveToFirst()) {
                do {
                    result1 = c.getString(c.getColumnIndex("name"));
                    if (result1.equals(username)) {
                        result2 = c.getString(c.getColumnIndex("password"));
                        break;
                    }
                } while (c.moveToNext());
            }
        }
        return result2.equals(password);
    }
}