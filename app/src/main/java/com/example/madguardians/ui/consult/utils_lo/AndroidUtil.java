package com.example.madguardians.ui.consult.utils_lo;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.example.madguardians.ui.consult.model_lo.UserModel;

public class AndroidUtil {

    // Show a toast message
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // Pass UserModel as arguments to a Fragment
    public static void passUserModelAsBundle(Bundle bundle, UserModel model) {
        bundle.putString("username", model.getName());
        bundle.putString("phone", model.getPhone());
        bundle.putString("userId", model.getUserId());
    }

    // Retrieve UserModel from Fragment arguments
    public static UserModel getUserModelFromBundle(Bundle bundle) {
        if (bundle == null) return null;

        UserModel userModel = new UserModel();
        userModel.setName(bundle.getString("username"));
        userModel.setPhone(bundle.getString("phone"));
        userModel.setUserId(bundle.getString("userId"));
        return userModel;
    }
}
