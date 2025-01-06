package com.example.madguardians.comment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.comment.adapter.CommentViewModel;
import com.example.madguardians.comment.adapter.EduCommentAdapter;
import com.example.madguardians.comment.adapter.FirestoreComment;
import com.example.madguardians.database.Comments;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EduChildCommentFragment extends Fragment {
    RecyclerView commentRecyclerView;
    LinearLayout empty_comment;
    private EduCommentAdapter adapter;
    private CommentViewModel viewModel;
    SharedPreferences sharedPreferences;
    private String userId;
    private String isRead;
    LiveData<List<Comments>> commentLiveData;
    FragmentActivity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comment_edu_viewpager, container, false);
        commentRecyclerView = view.findViewById(R.id.comment_recyclerView);
        empty_comment = view.findViewById(R.id.empty_comment);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        commentRecyclerView.setLayoutManager(layoutManager);

        adapter = new EduCommentAdapter();
//        adapter = new EduCommentAdapter(new EduCommentAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position, String commentId) {
//                // Handle item click event here
//                // For example, show a Toast or navigate to another screen
//            }
//        });
        commentRecyclerView.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        sharedPreferences = getContext().getSharedPreferences("user_preferences", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);

        viewModel.setUser(userId);


        if (getArguments() != null) {
            isRead = getArguments().getString("isRead");
            switch(isRead){
                case "All":
                    commentLiveData = viewModel.getAllComment();
                    break;
                case "Unread":
                    commentLiveData = viewModel.getUnreadReadComment(false);
                    break;
                case "Read":
                    commentLiveData = viewModel.getUnreadReadComment(true);
                    break;
            }
        }

//        FirestoreComment firestoreManager = new FirestoreComment();;
//        LiveData<List<Comments>> allCommentLiveData = firestoreManager.getAllComments(userId);
//        allCommentLiveData.observe(getViewLifecycleOwner(), comments -> {
        // Observe LiveData

        commentLiveData.observe(getViewLifecycleOwner(), comments -> {
            // Update adapter when data changes
            if (comments != null && !comments.isEmpty()) {
                commentRecyclerView.setVisibility(View.VISIBLE);
                empty_comment.setVisibility(View.GONE);
                adapter.setCommentList(comments);
                adapter.setUserId(userId);
                adapter.setParentActivity(this.parentActivity);
                adapter.setContext(getContext());
            }
            else {
                commentRecyclerView.setVisibility(View.GONE);
                empty_comment.setVisibility(View.VISIBLE);
            }
        });

//        replyBox.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                RootCommentFragment rootCommentFragment = newRootComment(post);
//                rootCommentFragment.show(getParentFragmentManager(), "RootCommentFragment");
//            }
//        });

        return view;
    }

    public static EduChildCommentFragment newInstance (String readStatus, FragmentActivity parentActivity) {
        EduChildCommentFragment fragment = new EduChildCommentFragment();
        Bundle args = new Bundle();
        args.putString("isRead", readStatus);
        fragment.setArguments(args);
        fragment.setParentActivity(parentActivity);
        return fragment;
    }

    public void setParentActivity(FragmentActivity parentActivity) {
        this.parentActivity = parentActivity;
    }
}
