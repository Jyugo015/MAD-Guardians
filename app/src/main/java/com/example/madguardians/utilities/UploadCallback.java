package com.example.madguardians.utilities;

public interface UploadCallback<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}
