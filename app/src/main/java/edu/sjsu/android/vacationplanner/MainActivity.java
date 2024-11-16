package edu.sjsu.android.vacationplanner;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import edu.sjsu.android.vacationplanner.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ImageButton calendarButton;
    private ImageButton savesButton;
    private boolean isCalendarOpen = false;
    private boolean isSavesOpen = false;
    FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);

        // implementing bottom navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(bottomNav, navController);

        calendarButton = (ImageButton)findViewById(R.id.CalendarButton);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCalendarOpen) {
                    fragmentManager.popBackStack();
                    isCalendarOpen = false;
                } else {
                    navController.navigate(R.id.navigation_calendar);
                    isCalendarOpen = true;
                }
            }
        });

        savesButton = (ImageButton)findViewById(R.id.SavesButton);
        savesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isSavesOpen) {
                    fragmentManager.popBackStack();
                    isSavesOpen = false;
                } else {
                    navController.navigate(R.id.navigation_saves);
                    isSavesOpen = true;
                }
            }
        });

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    navController.navigate(R.id.navigation_home);
                    return true;
                } else if (itemId == R.id.navigation_group) {
                    navController.navigate(R.id.navigation_group);
                    return true;
                } else if (itemId == R.id.navigation_budget) {
                    navController.navigate(R.id.navigation_budget);
                    return true;
                } else if (itemId == R.id.navigation_planning) {
                    navController.navigate(R.id.navigation_votingGroup);
                    return true;
                } else if (itemId == R.id.navigation_search) {
                    navController.navigate(R.id.navigation_search);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}