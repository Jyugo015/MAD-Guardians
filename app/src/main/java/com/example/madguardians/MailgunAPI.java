package com.example.madguardians;
import android.util.Base64;
import android.util.Log;

import okhttp3.*;
import java.io.IOException;

public class MailgunAPI {
    private static final String MAILGUN_API_KEY = "df3e666eba307de89e4b5119e70cf92a-2e68d0fb-aa0f7f91";
    private static final String DOMAIN = "sandboxa198bd00a99741f88780505175b7b2a4.mailgun.org";  // e.g., "sandbox123.mailgun.org"
    private static final String FROM_EMAIL = "madguardian04@gmail.com"; // Sender's email address

    public void sendOTPEmail(String recipientEmail, String otp) {
        OkHttpClient client = new OkHttpClient();

        // Prepare the request data (email content)
        RequestBody requestBody = new FormBody.Builder()
                .add("from", FROM_EMAIL)
                .add("to", recipientEmail)
                .add("subject", "Your OTP Code")
                .add("text", "Your OTP code is: " + otp)
                .build();

        // Create the request
        Request request = new Request.Builder()
                .url("https://api.mailgun.net/v3/" + DOMAIN + "/messages")
                .addHeader("Authorization", "Basic " + getBasicAuth())
                .post(requestBody)
                .build();

        // Make the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // OTP email sent successfully
                    Log.d("Mailgun", "OTP email sent successfully to: " + recipientEmail);
                } else {
                    // Error occurred
                    Log.e("Mailgun", "Error sending OTP email: " + response.body().string());
                }
            }
        });
    }

    // Helper method to get Basic Auth header value
    private String getBasicAuth() {
        String credentials = "api:" + MAILGUN_API_KEY;
        return Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }
}
