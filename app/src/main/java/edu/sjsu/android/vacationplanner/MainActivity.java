package edu.sjsu.android.vacationplanner;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.sjsu.android.vacationplanner.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ImageButton calendarButton;
    private ImageButton savesButton;
    private ImageButton notificationsButton;
    private boolean isCalendarOpen = false;
    private boolean isSavesOpen = false;
    FragmentManager fragmentManager = getSupportFragmentManager();

    private CircleImageView userProfile;
    private boolean isProfileOpen = false;
    private static ImageView notificationDot;

    private final Uri CONTENT_URI = Uri.parse("content://edu.sjsu.android.vacationplanner");
    private final Uri CONTENT_URI2 = Uri.parse("content://edu.sjsu.android.vacationplanner.GroupProvider");
    private final Uri CONTENT_URI_notifications = CONTENT_URI2.buildUpon().appendPath("notifications").build();

    private static String username = "";
    private static int profilePicID = 0;
    private static int userID = 0;
    private static int groupID = 0;
    private static int hostID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);

        // call method to get user info
        getUserProfileInfo();

        // get notifications from db
        getNotificationsFromDB();

        // initialize notification dot
        notificationDot = findViewById(R.id.notification_dot);
        checkNotifications();

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

        notificationsButton = findViewById(R.id.home_notifications);
        notificationsButton.setOnClickListener(view -> {
            showNotifications();
        });

        userProfile = findViewById(R.id.home_profile);
        userProfile.setImageResource(profilePicID);
        userProfile.setOnClickListener(view -> {
            if (isProfileOpen) {
                fragmentManager.popBackStack();
                isProfileOpen = false;
            } else {
                navController.navigate(R.id.profileFragment);
                isProfileOpen = true;
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
                    navController.navigate(R.id.navigation_voting);
                    return true;
                } else if (itemId == R.id.navigation_search) {
                    navController.navigate(R.id.mapsActivity);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.unin){
            Intent delete = new Intent(Intent.ACTION_DELETE,
                    Uri.parse("package:" + getPackageName()));
            startActivity(delete);
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }

    @SuppressLint("Range")
    private void getNotificationsFromDB() {
        Notification.notificationsList.clear();
        // if this is a new user, send notification for new user
        if (groupID == 0) {
            Notification newUserNotification = new Notification(1, "Welcome to the Travel Planner App!", "Get started by forming a group!");
            Notification.notificationsList.add(newUserNotification);
        }

        final String[] selectColumns = {"notifID", "notifTitle", "notifDescription", "groupID"};
        try (Cursor c = getContentResolver().
                query(CONTENT_URI_notifications, null, null, selectColumns, "groupID")) {
            assert c != null;
            if (c.moveToFirst()) {
                do {
                    int idGroup = c.getInt(c.getColumnIndex("groupID"));
                    if (idGroup == MainActivity.getGroupID()) {
                        int notifID = c.getInt(c.getColumnIndex("notifID"));
                        String notifTitle = c.getString(c.getColumnIndex("notifTitle"));
                        String notifDesc = c.getString(c.getColumnIndex("notifDescription"));
                        Notification.notificationsList.add(new Notification(notifID, notifTitle, notifDesc));
                    }
                } while (c.moveToNext());
            } }
    }

    public static void checkNotifications() {
        if (Notification.notificationsList.isEmpty()){
            notificationDot.setVisibility(View.GONE);
        } else {
            notificationDot.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("Range")
    private void getUserProfileInfo() {
        Intent previousIntent = getIntent();
        username = previousIntent.getStringExtra("username");

        String selection = "Select * from users where name = " + username;
        String result = "";
        try (Cursor c = getContentResolver().
                query(CONTENT_URI, null, selection, null, "name")) {
            assert c != null;
            if (c.moveToFirst()) {
                do {
                    result = c.getString(c.getColumnIndex("name"));
                    if (result.equals(username)) {
                        userID = c.getInt(c.getColumnIndex("_id"));
                        profilePicID = c.getInt(c.getColumnIndex("profilePicID"));
                        groupID = c.getInt(c.getColumnIndex("groupID"));
                        hostID = c.getInt(c.getColumnIndex("hostID"));
                        break;
                    }
                } while (c.moveToNext());
            }
        }
    }


    private void showNotifications() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_layout);

        // set up notifications list
        ListView listView = dialog.findViewById(R.id.notificationsList);
        NotificationAdapter notificationAdapter = new NotificationAdapter(getApplicationContext(), Notification.notificationsList);
        listView.setAdapter(notificationAdapter);

        TextView emptyText = dialog.findViewById(R.id.emptyNotifications);
        listView.setEmptyView(emptyText);


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomSheetAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    // for use by other classes
    public static String getUsername() { return username; }
    public static int getProfilePicID() { return profilePicID; }
    public static int getHostID() { return hostID; }
    public static int getGroupID() { return groupID; }
    public static int getUserID() { return userID; }
    public static boolean isHost() { return hostID > 0; }

    public static void updateGroupID(int idGroup) {
        groupID = idGroup;
    }
    public static void updateHostID() { hostID = 1; }

    @Override
    protected void onResume() {
        super.onResume();
        checkNotifications();
    }
}