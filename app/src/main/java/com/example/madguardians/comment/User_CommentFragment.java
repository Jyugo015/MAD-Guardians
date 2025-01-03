package com.example.madguardians.comment;

import static com.example.madguardians.comment.ReplyCommentFragment.newReplyComment;
import static com.example.madguardians.comment.ReportFragment.newReport;
import static com.example.madguardians.comment.RootCommentFragment.newRootComment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.comment.adapter.CommentViewModel;
import com.example.madguardians.comment.adapter.Listener;
import com.example.madguardians.comment.adapter.RootCommentAdapter;
import com.example.madguardians.database.Comments;
import com.example.madguardians.firebase.PostFB;

import java.util.List;

public class User_CommentFragment extends Fragment implements Listener.CommentListener,
                                                              Listener.OnDialogDismissListener,
                                                              Listener.OnItemPressedListener,
                                                              Listener.OnReportListener,
                                                              Listener.onHelpdeskListener{
    RecyclerView commentRecyclerView;
    ConstraintLayout replyBox;
    private PostFB post;
    private TextView input_root_comment;
    LinearLayout empty_comment;
    private RootCommentAdapter adapter;
    private CommentViewModel viewModel;
    LiveData<List<Comments>> commentLiveData;
    Comments comment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.comment_user_fragment, container, false);

        ConstraintLayout view = (ConstraintLayout) root;
        commentRecyclerView = view.findViewById(R.id.recyclerViewComments);
        replyBox = view.findViewById(R.id.replyBox);
        input_root_comment = view.findViewById(R.id.input_root_comment);
        empty_comment = view.findViewById(R.id.empty_comment);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        commentRecyclerView.setLayoutManager(layoutManager);

        adapter = new RootCommentAdapter(getContext(), this, getViewLifecycleOwner());
        adapter.setOnItemPressedListener(User_CommentFragment.this);
        adapter.setOnReportListener(User_CommentFragment.this);
        commentRecyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CommentViewModel.class);

        commentLiveData = viewModel.getRootComment(post.getPostId());
        Log.d("Debug", "getRootComment called with postId: " + post.getPostId());

        commentLiveData.observe(getViewLifecycleOwner(), comments -> {
            Log.d("Debug", "Observer triggered with comments size: " + comments.size());
            // Update adapter when data changes
            if (comments != null && !comments.isEmpty()) {
                commentRecyclerView.setVisibility(View.VISIBLE);
                empty_comment.setVisibility(View.GONE);
                adapter.setCommentList(comments);
//                if (getArguments().containsKey("comment")&&comment.getRootComment()==null) {
//                    commentRecyclerView.post(() -> {
//                        int position = adapter.findItemPosition(comment.getCommentId());
//                        if (position >= 0 && position < adapter.getItemCount()) {
//                            commentRecyclerView.smoothScrollToPosition(position);
//                        }
//                    });
//                }
//                else if(getArguments().containsKey("comment")&&comment.getRootComment()!=null){
//                    commentRecyclerView.post(() -> {
//                        int position = adapter.findItemPosition(comment.getRootComment());
//                        if (position >= 0 && position < adapter.getItemCount()) {
//                            commentRecyclerView.scrollToPosition(position);
//                            adapter.setFindMatchedComment(comment);
//                            adapter.findMatchedComment(true);
//                        }
//                    });
//                }
            }
            else {
                commentRecyclerView.setVisibility(View.GONE);
                empty_comment.setVisibility(View.VISIBLE);
            }
        });

        replyBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                NavController navController = Navigation.findNavController(requireActivity(), R.id.NavHostFragment);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("post", post);
//                navController.navigate(R.id.nav_user_comment, bundle);
//                at this point, this refers to the OnClickListener, not the User_CommentFragment
                if (replyBox.getVisibility() == View.VISIBLE) {
                    replyBox.setVisibility(View.GONE);
                } else {
                    replyBox.setVisibility(View.VISIBLE);
                }
                RootCommentFragment rootCommentFragment = newRootComment(post, input_root_comment.getText().toString());
                rootCommentFragment.setCommentListener(User_CommentFragment.this); // Set the listener
                rootCommentFragment.setOnDialogDismissListener(User_CommentFragment.this);
                rootCommentFragment.show(getParentFragmentManager(), "RootCommentFragment");
            }

            // Show the keyboard immediately
//            rootCommentFragment.getDialog().setOnShowListener(dialog -> {
//                EditText inputReply = rootCommentFragment.getDialog().findViewById(R.id.input_root_comment);
//                if (inputReply != null) {
//                    inputReply.requestFocus();
//                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if (imm != null) {
//                        imm.showSoftInput(inputReply, InputMethodManager.SHOW_IMPLICIT);
//                    }
//                }
//            });
        });


        return view;

//        // Initialize RecyclerView adapter and comment click handling
////        setupComments();
//
//        // Add click listener for permanent reply box
//        editTextPermanentReply.setOnClickListener(v -> showOverlayReplyBox(null));
//
//        overlayReplyBox.setOnTouchListener((v, event) -> true); // Prevent touches passing through
//        overlayReplyBox.findViewById(R.id.buttonSendOverlayReply).setOnClickListener(v -> {
////            sendReply(editTextOverlayReply.getText().toString());
//            dismissOverlayReplyBox();
//        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            post = (PostFB) getArguments().getSerializable("post");
            if (getArguments().containsKey("comment")) {
                comment = (Comments)getArguments().getSerializable("comment");
            }
        }
    }

    @Override
    public void onCommentTextProvided(String text) {
        // Set the text to the reply box
        input_root_comment.setText(text);
    }
    @Override
    public void onDialogDismiss() {
//            new Handler(Looper.getMainLooper()).post(() -> replyBox.setVisibility(View.VISIBLE));
//            requireActivity().runOnUiThread(() -> replyBox.setVisibility(View.VISIBLE));
        Log.d("DialogDismiss", "Showing reply box");
        requireActivity().runOnUiThread(() -> {
            replyBox.setVisibility(View.VISIBLE);
            Log.d("DialogDismiss", "Reply box visibility set");
        });
    }

    @Override
    public void onItemPressed(Comments repliedComment){
        Log.d("itemPressed", "Hiding reply box");
        requireActivity().runOnUiThread(() -> {
            replyBox.setVisibility(View.GONE);
        });
        adapter.setOnItemPressedListener(User_CommentFragment.this);
        ReplyCommentFragment replyCommentFragment = newReplyComment(post, repliedComment, input_root_comment.getText().toString());
        replyCommentFragment.setCommentListener(User_CommentFragment.this); // Set the listener
        replyCommentFragment.setOnDialogDismissListener(User_CommentFragment.this);
        replyCommentFragment.show(getParentFragmentManager(), "ReplyCommentFragment");
    }

    @Override
    public void onReport(Comments comment) {
        ReportFragment reportFragment = newReport(comment);
        reportFragment.show(getParentFragmentManager(), "ReportFragment");
        reportFragment.setHelpedeskListener(User_CommentFragment.this);
    }

    @Override
    public void helpdeskAdded() {
        Toast.makeText(getContext(), "Comment Reported", Toast.LENGTH_SHORT).show();
    }

}
//    private void showOverlayReplyBox(String replyingTo) {
//        overlayReplyBox.setVisibility(View.VISIBLE);
//
//        // Set the replying to text if provided
//        if (replyingTo != null) {
//            textViewReplyingTo.setText("Replying to: " + replyingTo);
//            textViewReplyingTo.setVisibility(View.VISIBLE);
//        } else {
//            textViewReplyingTo.setVisibility(View.GONE);
//        }
//
//        // Focus the overlay reply EditText and show the keyboard
//        editTextOverlayReply.requestFocus();
//        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(editTextOverlayReply, InputMethodManager.SHOW_IMPLICIT);
//    }

    // Dismiss overlay box
//    private void dismissOverlayReplyBox() {
//        overlayReplyBox.setVisibility(View.GONE);
//        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(editTextOverlayReply.getWindowToken(), 0);
//    }

//In the adapter's click listener for comments:
//commentsAdapter.setOnCommentClickListener((comment) -> {
//    showOverlayReplyBox(comment.getUsername());
//});
//In your Activity or Fragment's container activity, make sure the SoftInputMode is set to adjust the layout properly:
//<Fragment
//    android:name=".YourActivity"
//    android:windowSoftInputMode="adjustResize" />
