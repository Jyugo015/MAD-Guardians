package com.example.madguardians.ui.consult;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConsultViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ConsultViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is consult fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}