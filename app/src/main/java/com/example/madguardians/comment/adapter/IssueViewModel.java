package com.example.madguardians.comment.adapter;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.madguardians.database.Comments;
import com.example.madguardians.database.Issue;

import java.util.List;

public class IssueViewModel extends ViewModel {
    FirestoreComment firestoreManager = new FirestoreComment();
    LiveData<List<Issue>> issueLiveData;
    //            new MutableLiveData<>();
    public LiveData<List<Issue>> getIssue() {
        issueLiveData = firestoreManager.getIssue();
        return issueLiveData;
    }
}
