package com.example.madguardians.ui.consult.adapter_lo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.ui.consult.model_lo.ChatroomModel;
import com.example.madguardians.ui.consult.model_lo.CounselorModel;
import com.example.madguardians.ui.consult.model_lo.UserModel;
import com.example.madguardians.ui.consult.utils_lo.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    private final NavController navController;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, NavController navController) {
        super(options);
        this.navController = navController;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds()).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();


                if (document.exists()) {

                    if (document.contains("email")) {  // Field check for UserModel
                        UserModel otherUserModel = document.toObject(UserModel.class);
                        if (otherUserModel != null) {
                            setUpChat(holder, model, otherUserModel);
                        }
                    } else if (document.contains("experience")) {  // Field check for CounselorModel
                        CounselorModel otherUserModel = document.toObject(CounselorModel.class);
                        if (otherUserModel != null) {
                            setUpChat(holder, model, otherUserModel);
                        }
                    }
                } else {
                    Log.e("RecyclerAdapter", "Document does not exist");
                }
            } else {
                Log.e("RecyclerAdapter", "Failed to fetch user data");
            }
        });
    }

    private void setUpChat(ChatroomModelViewHolder holder, ChatroomModel model, Object otherUserModel) {
        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

        if (otherUserModel instanceof UserModel) {
            UserModel userModel = (UserModel) otherUserModel;
            holder.usernameText.setText(userModel.getName());
        } else if (otherUserModel instanceof CounselorModel) {
            CounselorModel counselorModel = (CounselorModel) otherUserModel;
            holder.usernameText.setText(counselorModel.getName());
        }

        holder.lastMessageText.setText(lastMessageSentByMe ? "You: " + model.getLastMessage() : model.getLastMessage());
        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

        Log.e("username","found: "+holder.usernameText.getText());

        holder.itemView.setOnClickListener(v -> {
            if (navController != null) {
                Bundle bundle = new Bundle();
                bundle.putString("chatroomId", model.getChatroomId());
                bundle.putString("otherUserId", otherUserModel instanceof UserModel ?
                        ((UserModel) otherUserModel).getUserId() :
                        ((CounselorModel) otherUserModel).getUserId());
                bundle.putString("otherUserName", otherUserModel instanceof UserModel ?
                        ((UserModel) otherUserModel).getName() :
                        ((CounselorModel) otherUserModel).getName());

                navController.navigate(R.id.action_chatHistoryFragment_to_chatFragment, bundle);
            }
        });
    }


    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    static class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, lastMessageText, lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
