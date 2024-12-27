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
import com.example.madguardians.ui.course.CourseElement;
import com.example.madguardians.ui.course.Domain;
import com.example.madguardians.ui.course.Post;

import java.util.List;
import java.util.stream.Collectors;

public class AdapterCourse extends RecyclerView.Adapter<AdapterCourse.CourseViewHolder> {

    private List<CourseElement> courseList;
    private List<CourseElement> originalCourseList;
    private OnItemClickListener listener;
    public AdapterCourse(List<CourseElement> courseList, OnItemClickListener listener) {
        this.courseList = courseList;
        this.originalCourseList = courseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.segment_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCourse.CourseViewHolder holder, int position) {
        CourseElement course = courseList.get(position);

        // Set the data
        holder.title.setText(course.getTitle());
        holder.author.setText(course.getAuthor());
        holder.date.setText(course.getDate());
//        holder.views.setText(course.getViews());
//        holder.comments.setText(course.getComments());
        showImage(holder, course);
        // check if it is verified
        // check if it id collected

        // Handle button clicks
        holder.button_start.setOnClickListener(v -> listener.onStartClick(course));
        holder.button_collection.setOnClickListener(v -> listener.onCollectionClick(course));
    }

    private void showImage(CourseViewHolder holder, CourseElement course) {
        Glide.with(holder.itemView.getContext()).load(course.getCoverImage()).placeholder(R.drawable.placeholder_image).error(R.drawable.error_image).into(holder.image_cover);
    }


    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void filterCourseByDomain(List<Domain> domains) {
        if (domains == null || domains.isEmpty()) {
            courseList = originalCourseList;
        } else {
            courseList = originalCourseList.stream()
                    .filter(course -> domains.contains(Domain.getDomainById(Post.getPost(course.getPost1()).getDomainId())))
                    .collect(Collectors.toList());
        }
        Log.d("TAG", "filterCourseByDomain: " + courseList.toString());
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
        void onStartClick(CourseElement course);
        void onCollectionClick(CourseElement course);
    }
}