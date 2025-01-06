package com.example.madguardians.comment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.madguardians.R;
import com.example.madguardians.comment.adapter.FirestoreComment;
import com.example.madguardians.comment.adapter.Listener;
import com.example.madguardians.database.Comments;
import com.example.madguardians.firebase.PostFB;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;

public class ReplyCommentFragment extends DialogFragment {

    private EditText commentText;
    private ShapeableImageView sendButton;
    private TextView repliedUser;
    private TextView repliedCommentText;
    private FirestoreComment firestoreManager;
    SharedPreferences sharedPreferences;
    private String userId;
    private static final String ARG_POST = "post";
    private static final String ARG_REPLIED_COMMENT = "replied_comment";
    private static final String ARG_UNCOMMITED_COMMENT = "";
    private PostFB post;
    private Comments repliedComment;
    private Listener.CommentListener listener;
    private Listener.OnDialogDismissListener dismissListener;
    String currentText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comment_item_reply_child, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        commentText = view.findViewById(R.id.input_child_comment);
        sendButton = view.findViewById(R.id.send);
        repliedUser = view.findViewById(R.id.username);
        repliedCommentText = view.findViewById(R.id.replied_comment);

        firestoreManager = new FirestoreComment();
        sharedPreferences = getContext().getSharedPreferences("user_preferences", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);

        // Retrieve the post object
        if (getArguments() != null) {
            post = (PostFB) getArguments().getSerializable(ARG_POST);
            repliedComment = (Comments) getArguments().getSerializable(ARG_REPLIED_COMMENT);
            firestoreManager.getUser(repliedComment.getUserId(), user -> {
                repliedUser.setText(user.getName());
            });
            repliedCommentText.setText(repliedComment.getComment());
            commentText.setText(getArguments().getString(ARG_UNCOMMITED_COMMENT));
            // Move the cursor to the end of the text
            commentText.setSelection(commentText.getText().length());
        }

        // Show the keyboard when dialog appears
//        commentText.requestFocus();
//        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.showSoftInput(commentText, InputMethodManager.SHOW_IMPLICIT);
//        }
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_comment = commentText.getText().toString();
                String rootComment = (repliedComment.getRootComment()==null?repliedComment.getCommentId():repliedComment.getRootComment());
                if (!input_comment.isEmpty()) {
                    firestoreManager.getLastDocumentId("comment",lastCommentId -> {
                        if (lastCommentId != null) {
                            Log.d("Result", "Last Comment ID: " + lastCommentId);
                            Comments comment = new Comments(
                                    getNumericPart(lastCommentId)>9999?"COM"+ String.valueOf(getNumericPart(lastCommentId)):"COM" + String.format("%04d", getNumericPart(lastCommentId)),
                                    userId,
                                    post.getPostId(),
                                    input_comment,
                                    rootComment,
                                    repliedComment.getUserId(),
                                    repliedComment.getComment(),
                                    false,
                                    false,
                                    post.getUserId(),
                                    Timestamp.now());
                            firestoreManager.insertComment(comment);
                            Log.d("Firestore Write", "Comment successfully added");
                        } else {
                            Log.d("Firestore Write", "Error adding comment");
                        }
                    });
                    currentText = "";
                    dismiss();
                }
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {

        // Pass the text back to the listener
        if (listener != null) {
            listener.onCommentTextProvided(currentText);
        }

        if (dismissListener != null) {
            dismissListener.onDialogDismiss(); // Notify parent to show the reply box
        }

        super.onDismiss(dialog);
        // Optionally hide the keyboard when dialog is dismissed
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
        }
    }

    @Override
    public int getTheme() {
        return R.style.DialogTheme;
    }

    public int getNumericPart(String documentId) {
        // Remove the 'COM' prefix
        String numericPart = documentId.substring(3);

        // Trim leading zeros by parsing the string to an integer and then back to a string
        int numericValue = Integer.parseInt(numericPart)+1;
        return numericValue; // Convert back to string
    }

    public static ReplyCommentFragment newReplyComment(PostFB post, Comments repliedComment, String uncommitedComment) {
        ReplyCommentFragment fragment = new ReplyCommentFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POST, post); // Pass the post object
        args.putSerializable(ARG_REPLIED_COMMENT, repliedComment);  // Pass the replied comment object
        args.putString(ARG_UNCOMMITED_COMMENT, uncommitedComment);
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

            params.width = WindowManager.LayoutParams.MATCH_PARENT; // Match parent width
            params.height = WindowManager.LayoutParams.WRAP_CONTENT; // Wrap content height
            window.setAttributes(params);
            // Align the dialog to the bottom of the screen
            window.setGravity(Gravity.BOTTOM);

            // Ensure the keyboard pushes the dialog up
            // Ensure the dialog stays above the keyboard
//            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            //            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            // Optional: Add margins to avoid full-screen coverage
            window.setLayout(params.width, params.height);
//            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getDialog().setCanceledOnTouchOutside(true); // Allow dismissing the dialog by tapping outside

            EditText inputReply = getDialog().findViewById(R.id.input_child_comment);
            if (inputReply != null) {
                inputReply.postDelayed(() -> {
                    inputReply.requestFocus();
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                          imm.showSoftInput(inputReply, InputMethodManager.SHOW_IMPLICIT);
                    }
                    Log.d("FocusState", "EditText has focus: " + inputReply.hasFocus());
                    Log.d("Keyboard", "Requesting keyboard");
                }, 150);
            }
        }
    }

    public void setCommentListener(Listener.CommentListener listener) {
        this.listener = listener;
    }

    public void setOnDialogDismissListener(Listener.OnDialogDismissListener listener) {
        this.dismissListener = listener;
    }
}
