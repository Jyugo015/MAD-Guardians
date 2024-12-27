package com.example.madguardians.ui.consult.utils_lo;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRouter;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {

    public static String currentUserId(Context context) {

        // Get SharedPreferences instance using the passed context
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);

        // Retrieve the user_id from SharedPreferences
        String userId = sharedPreferences.getString("user_id", null); // null is the default value if the key doesn't exist

        // Return the userId (or null if not found)
        return userId;
    }




    public static void isCounselor(SimpleCallback callback, Context context) {
        String currentUserId = currentUserId(context);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("counselors")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // The document exists, user is a counselor
                        callback.onResult(true);
                    } else {
                        // The document doesn't exist
                        callback.onResult(false);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors during the Firestore operation
                    callback.onError(e);
                });
    }

    public interface SimpleCallback {
        void onResult(boolean isCounselor); // To handle the boolean result
        void onError(Exception e);         // To handle errors
    }



    public interface CheckCounselorCallback {
        void onResult(boolean isCounselor);
        void onError(Exception e);
    }



    public static DocumentReference getUser(String userId) {
        Log.e("UserID","Current UserID is : "+ userId);
        return FirebaseFirestore.getInstance().collection("user").document(userId);
    }



    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static String getChatroomId(String userId1, String userId2) {
        return (userId1.hashCode() < userId2.hashCode()) ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }


    public static Task<DocumentSnapshot> getOtherUserFromChatroom(List<String> userIds, Context context) {

        String currentUserId = FirebaseUtil.currentUserId(context);
        String otherUserId = userIds.get(0).equals(currentUserId) ? userIds.get(1) : userIds.get(0);


        DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("user").document(otherUserId);
        DocumentReference counselorDocRef = FirebaseFirestore.getInstance().collection("counselors").document(otherUserId);


        return userDocRef.get().continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                return Tasks.forResult(task.getResult());
            } else {
                return counselorDocRef.get();
            }
        });
    }


    public static Task<String> getUserNameById(String userId) {
        DocumentReference userDoc = getUser(userId);
        return userDoc.get().continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                return task.getResult().getString("name");
            }else{
                Log.e("Username","The username is not found using userID");
            }
            return null;
        });
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }
}
