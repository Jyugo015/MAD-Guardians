package com.example.madguardians;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madguardians.database.Achievement;
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Badge;
import com.example.madguardians.database.BadgeDao;
import com.example.madguardians.database.Executor;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {

    private List<Achievement> achievementList;

    public AchievementAdapter(List<Achievement> achievementList) {
        this.achievementList = achievementList;
    }

    @Override
    public AchievementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_achievement_item, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AchievementViewHolder holder, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Find badge by badgeId
        Achievement achievement = achievementList.get(position);
        db.collection("badge").document(achievement.getBadgeId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Badge badge = documentSnapshot.toObject(Badge.class);

                        // Update UI
                        holder.achievementTitle.setText(badge != null ? badge.getBadgeName() : "Unknown Badge");

                        if (badge != null && badge.getBadgeImage() != null && !badge.getBadgeImage().isEmpty()) {
                            Glide.with(holder.itemView.getContext())
                                    .load(badge.getBadgeImage())
                                    .into(holder.achievementIcon);
                        } else {
                            holder.achievementIcon.setImageResource(R.drawable.ic_achievement_icon); // 使用默认图标
                        }
                    } else {
                        holder.achievementTitle.setText("Unknown Badge");
                        holder.achievementIcon.setImageResource(R.drawable.ic_achievement_icon); // 使用默认图标
                    }
                })
                .addOnFailureListener(e -> {
                    holder.achievementTitle.setText("Error loading badge");
                    holder.achievementIcon.setImageResource(R.drawable.ic_achievement_icon); // 使用默认图标
                });
    }

    @Override
    public int getItemCount() {
        return achievementList != null ? achievementList.size() : 0;
    }

    public static class AchievementViewHolder extends RecyclerView.ViewHolder {
        TextView achievementTitle;
        ImageView achievementIcon;

        public AchievementViewHolder(View itemView) {
            super(itemView);
            achievementTitle = itemView.findViewById(R.id.achievementTitle);
            achievementIcon = itemView.findViewById(R.id.achievementIcon);
        }
    }

}

