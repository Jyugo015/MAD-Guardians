package com.example.madguardians.ui.consult;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.notification.NotificationUtils;
import com.example.madguardians.ui.consult.adapter_lo.ChatRecyclerAdapter;
import com.example.madguardians.ui.consult.model_lo.ChatMessageModel;
import com.example.madguardians.ui.consult.model_lo.ChatroomModel;
import com.example.madguardians.ui.consult.model_lo.UserModel;
import com.example.madguardians.ui.consult.utils_lo.AndroidUtil;
import com.example.madguardians.ui.consult.utils_lo.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatFragment extends Fragment {

    private String counselorName ;
    private String counselorID;
    private String chatroomId;
    private String otherUserId;
    private String anotherUserName;
    private ChatroomModel chatroomModel;
    private ChatRecyclerAdapter adapter;

    private EditText messageInput;
    private ImageButton sendMessageBtn;
    private TextView otherUsername;
    private RecyclerView recyclerView;
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            counselorName = bundle.getString("counselorName");
            counselorID = bundle.getString("counselorID");

            chatroomId = bundle.getString("chatroomId");
            otherUserId = bundle.getString("otherUserId");
            anotherUserName = bundle.getString("otherUserName");

            Log.d("ChatFragment", "Received counselor"+ counselorName);
        }
        if(counselorID != null){
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(getContext()),counselorID);}

        messageInput = view.findViewById(R.id.chat_message_input);
        sendMessageBtn = view.findViewById(R.id.message_send_btn);
        otherUsername = view.findViewById(R.id.other_username);
        recyclerView = view.findViewById(R.id.chat_recycler_view);
        imageView = view.findViewById(R.id.profile_pic_image_view);


        if(counselorID != null){
            otherUsername.setText(counselorName);}
        else {
            otherUsername.setText(anotherUserName);
        }

        sendMessageBtn.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessageToUser(message);
            }
        });

        getOrCreateChatroomModel();
        setupChatRecyclerView();

        return view;
    }

    private void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class)
                .build();

        adapter = new ChatRecyclerAdapter(options, requireContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        adapter.startListening();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    private void sendMessageToUser(String message) {
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId(getContext()));
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
        NotificationUtils notificationUtils = new NotificationUtils();



        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(getContext()), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                messageInput.setText("");
                if(counselorID != null){
                    notificationUtils.createTestNotification(counselorID, message);}
                else {
                    notificationUtils.createTestNotification(otherUserId,message);
                }

            }
        });
    }

    private void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    chatroomModel = task.getResult().toObject(ChatroomModel.class);
                    if (chatroomModel == null) {
                        chatroomModel = new ChatroomModel(
                                chatroomId,
                                Arrays.asList(FirebaseUtil.currentUserId(getContext()),counselorID),
                                Timestamp.now(),
                                ""
                        );
                        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                    }
                }else{
                    Log.e("Chatroom","chatroom not found");
                }
            }
        });
    }
}
