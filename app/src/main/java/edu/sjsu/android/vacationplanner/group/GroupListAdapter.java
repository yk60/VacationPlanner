package edu.sjsu.android.vacationplanner.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import edu.sjsu.android.vacationplanner.User;
import edu.sjsu.android.vacationplanner.databinding.RowLayoutUsersBinding;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder>{

    private List<User> usersList;
    private OnUserClickedListener listener;
    RowLayoutUsersBinding binding;
    Context context;


    public GroupListAdapter(List<User> items) {
        usersList = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        binding = RowLayoutUsersBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, listener);
    }

    public void setListener(OnUserClickedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        User user = usersList.get(position); // returns an object in list based on position
        holder.iconView.setImageResource(user.getProfilePicID());
        holder.nameView.setText(user.getUsername());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView iconView;
        public final TextView nameView;

        public ViewHolder(RowLayoutUsersBinding binding, OnUserClickedListener listener) {
            super(binding.getRoot());
            this.iconView = binding.profileImage;
            this.nameView = binding.username;
            binding.getRoot().setOnClickListener(v -> listener.onClick(getLayoutPosition()));
        }

    }

    public void setFilteredList(List<User> filteredList) {
        this.usersList = filteredList;
        notifyDataSetChanged();
    }

}
