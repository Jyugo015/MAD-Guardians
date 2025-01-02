package com.example.madguardians.utilities;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madguardians.R;
import com.example.madguardians.firebase.CourseFB;
import com.example.madguardians.firebase.DomainFB;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AdapterCourse extends RecyclerView.Adapter<AdapterCourse.CourseViewHolder> {

    private List<CourseFB> courseFBList;
    private List<CourseFB> originalCourseFBList;
    private OnItemClickListener listener;
    public AdapterCourse(List<CourseFB> courseFBList, OnItemClickListener listener) {
        this.courseFBList = courseFBList;
        this.originalCourseFBList = courseFBList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.segment_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseFB courseFB = courseFBList.get(position);

        // Set the data
        holder.title.setText(courseFB.getTitle());
        FirebaseController.getMatchedCollection(FirebaseController.USER, FirebaseController.getIdName(FirebaseController.USER), courseFB.getAuthor(), new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> result) {
                if (! result.isEmpty())
                    holder.author.setText((String) result.get(0).get("name"));
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("TAG", "onFailure: failed to get username", e);
            }
        });
        holder.date.setText(courseFB.getDate());
//        holder.views.setText(course.getViews());
//        holder.comments.setText(course.getComments());
        showImage(holder, courseFB);
        // check if it is verified
        if (isVerified(courseFB)) {
            holder.verifyStatus.setImageResource(R.drawable.ic_verified);
        } else {
            holder.verifyStatus.setImageResource(R.drawable.ic_verifying);
        }
        // check if it id collected
        if (isCollected(courseFB)) {
            holder.button_collection.setChecked(true);
        }

        // Handle button clicks
        holder.button_start.setOnClickListener(v -> listener.onStartClick(courseFB));
        holder.button_collection.setOnClickListener(v -> listener.onCollectionClick(courseFB));
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // zw
    private boolean isVerified(CourseFB courseFB) {
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // yewoon

    private boolean isCollected(CourseFB courseFB) {
        return true;
    }
    ////////////////////////////////////////////////////////////////////////////////////

    private void showImage(CourseViewHolder holder, CourseFB courseFB) {
        Glide.with(holder.itemView.getContext()).load(courseFB.getCoverImage()).placeholder(R.drawable.placeholder_image).error(R.drawable.error_image).into(holder.image_cover);
    }

    @Override
    public int getItemCount() {
        return courseFBList.size();
    }

    public void filterCourseByDomain(List<DomainFB> domains) {
        if (domains == null || domains.isEmpty()) {
            courseFBList = originalCourseFBList;
        } else {
            courseFBList = originalCourseFBList.stream()
                .filter(course ->{
                        String domainId = course.getDomainId();
                        Log.d("TAG", "domainId: " + domainId);
                        return domains.stream().anyMatch(domain -> domain.getDomainId().equals(course.getDomainId()));
                }).collect(Collectors.toList());
        }
        Log.d("TAG", "filterCourseByDomain: " + courseFBList.toString());
        notifyDataSetChanged();
    }

    public void updateCourseList(List<CourseFB> cours) {
        courseFBList.clear();
        courseFBList.addAll(cours);
        notifyDataSetChanged();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, date, views, comments;
        ImageView image_cover, verifyStatus;
        Button button_start;
        ToggleButton button_collection;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.TVTitle);
            author = itemView.findViewById(R.id.TVAuthor);
            date = itemView.findViewById(R.id.TVDate);
            views = itemView.findViewById(R.id.TVView);
            comments = itemView.findViewById(R.id.TVComment);
            image_cover = itemView.findViewById(R.id.IVCover);
            verifyStatus = itemView.findViewById(R.id.IVVerify);
            button_start = itemView.findViewById(R.id.BTNStart);
            button_collection = itemView.findViewById(R.id.TBCollection);
        }
    }

    public interface OnItemClickListener {
        void onStartClick(CourseFB courseFB);
        void onCollectionClick(CourseFB courseFB);
    }
}
