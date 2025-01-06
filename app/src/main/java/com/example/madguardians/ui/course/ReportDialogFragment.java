package com.example.madguardians.ui.course;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.comment.adapter.IssueViewModel;
import com.example.madguardians.comment.adapter.Listener;
import com.example.madguardians.comment.adapter.ReportAdapter;
import com.example.madguardians.database.Comments;
import com.example.madguardians.database.Issue;
import com.example.madguardians.firebase.PostFB;

import java.util.List;


public class ReportDialogFragment extends DialogFragment implements Listener.OnIssueListener{
    SharedPreferences sharedPreferences;
    private String userId;
    private String reportedItemId;
    private PostFB post;
    RecyclerView recyclerView;
    ReportPostAdapter adapter;
    IssueViewModel viewModel;
    LiveData<List<Issue>> issueLiveData;
    private Listener.onHelpdeskListener listener;
    private static final String ARG_POST = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.issue_recycler_view);
        sharedPreferences = getContext().getSharedPreferences("user_preferences", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);
        // Retrieve the post object
        if (getArguments() != null) {
            post = (PostFB) getArguments().getSerializable(ARG_POST);
            reportedItemId = post.getPostId();
        }
        adapter = new ReportPostAdapter();
        adapter.setUserId(userId);
        adapter.setReportedItemId(reportedItemId);
        adapter.setIssueListener(ReportDialogFragment.this);
        adapter.setHelpdeskListener(listener);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        viewModel = new ViewModelProvider(this).get(IssueViewModel.class);

        issueLiveData = viewModel.getIssue();

        issueLiveData.observe(getViewLifecycleOwner(), issues -> {
            // Update adapter when data changes
            if (issues != null && !issues.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setIssueList(issues);
            }
            else {
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {super.onDismiss(dialog);}


    public static ReportDialogFragment newReport(PostFB post) {
        ReportDialogFragment fragment = new ReportDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POST, post); // Pass the post object, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true); // Enable dismiss on outside touch
    }

    @Override
    public void onStart() {
        super.onStart();
        // Ensure the dialog window cancels on outside touch
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.6f; // Adjust the dimming level (0.0f to 1.0f)
            window.setAttributes(params);
            // Set window flags to apply dim effect only to the area outside the dialog
            window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//            params.width = 300; // Match parent width
//            params.height = WindowManager.LayoutParams.WRAP_CONTENT; // Wrap content height
//            window.setAttributes(params);
            window.setGravity(Gravity.CENTER);

            getDialog().setCanceledOnTouchOutside(true); // Allow dismissing the dialog by tapping outside

        }
    }

    @Override
    public void issueClicked() {
        dismiss();
    }

    public void setHelpedeskListener(Listener.onHelpdeskListener listener) {
        this.listener = listener;
    }
}

