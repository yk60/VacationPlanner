package edu.sjsu.android.vacationplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VoteAdapter extends RecyclerView.Adapter<VoteAdapter.VoteViewHolder> {
    private final List<MyPlace> placeList;

    public VoteAdapter(List<MyPlace> placeList) {
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public VoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_vote, parent, false);
        return new VoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoteViewHolder holder, int position) {
        MyPlace myPlace = placeList.get(position);
        holder.bind(myPlace);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public static class VoteViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        ImageView imageView;

        public VoteViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.place_name);
            imageView = itemView.findViewById(R.id.place_image);
        }

        public void bind(MyPlace myPlace) {
            nameView.setText(myPlace.getName());
            if (myPlace.getImage() != null) {
                imageView.setImageBitmap(myPlace.getImage());
            } else {
                imageView.setImageResource(R.drawable.default_place_image);
            }
        }
    }
}
