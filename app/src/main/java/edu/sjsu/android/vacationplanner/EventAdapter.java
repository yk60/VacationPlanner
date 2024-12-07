package edu.sjsu.android.vacationplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<MyEvent> events;

    public EventAdapter(List<MyEvent> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_row_layout, parent, false);
        return new EventViewHolder(view);
    }

   @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        MyEvent event = events.get(position);
        if (!event.getTitle().isEmpty()) {
            holder.eventDetailTextView.setText("Title: " + event.getTitle() + "\nStarts: " + event.getStartTime() + "\nEnds: " + event.getEndTime());
        } else {
            holder.eventDetailTextView.setText(" \n \n ");
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
    public void setEvents(List<MyEvent> events) {
        this.events = events;
        notifyDataSetChanged();

    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public final TextView eventDetailTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventDetailTextView = itemView.findViewById(R.id.event_detail);
        }
    }

}
