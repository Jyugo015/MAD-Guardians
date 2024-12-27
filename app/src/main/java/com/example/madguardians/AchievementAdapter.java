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
        BadgeDao badgeDao = AppDatabase.getDatabase(holder.itemView.getContext()).badgeDao();
        Achievement achievement = achievementList.get(position);
        Executor.executeTask(() -> {
            Badge badge = badgeDao.getById(achievement.getBadgeId());

            // Use 'post' to run the UI update code on the main thread
            holder.itemView.post(() -> {
                holder.achievementTitle.setText(badge != null ? badge.getBadgeName() : "Unknown Badge");

                if (badge != null && badge.getBadgeImage() != null && !badge.getBadgeImage().isEmpty()) {
                    String imageUrl = badge.getBadgeImage();
                    Glide.with(holder.itemView.getContext())
                            .load(imageUrl)
                            .into(holder.achievementIcon);
                } else {
                    holder.achievementIcon.setImageResource(R.drawable.ic_achievement_icon);
                }
            });
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

