package com.example.madguardians.comment.adapter;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.madguardians.database.Comments;

import java.util.List;

public class CommentViewModel extends ViewModel {
    String userId;
    FirestoreComment firestoreManager = new FirestoreComment();
    LiveData<List<Comments>> commentLiveData;
//            new MutableLiveData<>();
    public LiveData<List<Comments>> getAllComment() {
        commentLiveData = firestoreManager.getComment(userId, null);
        return commentLiveData;
    }

    public LiveData<List<Comments>> getUnreadReadComment(boolean isRead) {
        commentLiveData = firestoreManager.getComment(userId, isRead?"Read":"Unread");
        return commentLiveData;
    }

    public LiveData<List<Comments>> getRootComment(String postId){
        commentLiveData = firestoreManager.getRootComment(postId);
        return commentLiveData;
    }

    public LiveData<List<Comments>> getChildComment(String rootCommentId){
        commentLiveData = firestoreManager.getChildComment(rootCommentId);
        return commentLiveData;
    }

    public void setUser(String userId){
        this.userId = userId;
        Log.d("Firestore", userId);
    }
}
