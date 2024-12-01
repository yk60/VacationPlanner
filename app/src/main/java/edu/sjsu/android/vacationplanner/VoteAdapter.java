package edu.sjsu.android.vacationplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class VoteAdapter extends RecyclerView.Adapter<VoteAdapter.VoteViewHolder> {
    private ArrayList<MyPlace> placeList;
    private LinkedHashMap<String, Integer> voteCountMap;
    private SharedViewModel sharedViewModel;


    public VoteAdapter(ArrayList<MyPlace> placeList, SharedViewModel sharedViewModel) {
        this.placeList = placeList;
        this.sharedViewModel = sharedViewModel;

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

    public void updatePlaceList(ArrayList<MyPlace> placeList) {
        this.placeList = placeList;
        notifyDataSetChanged();
    }

    public void updateVoteCountMap(LinkedHashMap<String, Integer> voteCountMap) {
        this.voteCountMap = voteCountMap;
        notifyDataSetChanged();
    }


    public class VoteViewHolder extends RecyclerView.ViewHolder {
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
            itemView.setOnClickListener(v -> {
                String placeName = myPlace.getName();
                Toast.makeText(itemView.getContext(), placeName + " clicked", Toast.LENGTH_SHORT).show();
                LinkedHashMap<String, Integer> voteCountMap = sharedViewModel.getVoteCountMap().getValue();
                if (voteCountMap != null) {
                    int count = voteCountMap.get(placeName);
                    voteCountMap.put(placeName, count + 1);
                    sharedViewModel.setVoteCountMap(voteCountMap);
                }
            });
        }
    }
    
}
