package edu.sjsu.android.vacationplanner;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;


public class NotificationAdapter  extends ArrayAdapter<Notification> {

    private final Uri CONTENT_URI2 = Uri.parse("content://edu.sjsu.android.vacationplanner.GroupProvider");
    private final Uri CONTENT_URI_notifications = CONTENT_URI2.buildUpon().appendPath("notifications").build();

    public NotificationAdapter(@NonNull Context context, List<Notification> notificationList) {
        super(context, 0, notificationList);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Notification notification = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_list_item, parent, false);
        }

        TextView title = convertView.findViewById(R.id.notification_title);
        TextView desc = convertView.findViewById(R.id.notification_desc);
        Button button = convertView.findViewById(R.id.notificationDismissButton);

        FrameLayout frameLayout = convertView.findViewById(R.id.notification_layout);

        assert notification != null;
        title.setText(notification.getTitle());
        desc.setText(notification.getDescription());
        button.setOnClickListener(view -> {
            Context context = this.getContext();
            ContentResolver result = (ContentResolver) context.getContentResolver();
            result.delete(CONTENT_URI_notifications,"notifID = ?", new String[] {String.valueOf(notification.getId())});

            Notification.notificationsList.remove(Notification.getNotificationForID(notification.getId()));
            this.notifyDataSetChanged();
            MainActivity.checkNotifications();
        });


        return convertView;
    }
}
