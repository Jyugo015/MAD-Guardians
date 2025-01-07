package com.example.madguardians;

import android.Manifest;
import static android.content.Context.MODE_PRIVATE;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Domain;
import com.example.madguardians.database.DomainDao;
import com.example.madguardians.database.Executor;
import com.example.madguardians.database.FirestoreManager;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;
import com.example.madguardians.database.VerEducator;
import com.example.madguardians.database.VerEducatorDao;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

public class EditProfileFragment extends Fragment {
    // UI Elements
    private static int IMAGE_REQ =1;
    private EditText ETname, mail, ETnumber;
    private TextView email, phone_number;
    private ImageView profileImageView;
    private AppDatabase db;
    private UserDao userDao;
    private VerEducatorDao verEducatorDao;
    private DomainDao domainDao;
    private LinearLayout verifiedContainer;
    private AppDatabase appDatabase;
    SharedPreferences sharedPreferences;
    String userId;
    // Query the user from the database in a background thread
    User user;
    // Constant for image picker request
    public static final int PICK_IMAGE_REQUEST = 1;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set the title dynamically in the Activity
        if (getActivity() != null) {
            getActivity().setTitle("Edit Profile");
        }
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize UI elements
        ETname = rootView.findViewById(R.id.ETname);
//        mail = rootView.findViewById(R.id.mail);
        ETnumber = rootView.findViewById(R.id.ETnumber);
//        email = rootView.findViewById(R.id.email);
        phone_number = rootView.findViewById(R.id.phone_number);
        profileImageView = rootView.findViewById(R.id.profile);
        verifiedContainer = rootView.findViewById(R.id.verifiedContainer);
//        appDatabase = AppDatabase.getDatabase(getContext());

        sharedPreferences = getContext().getSharedPreferences("user_preferences", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);

        // Initialize database and DAO
//        db = AppDatabase.getDatabase(getContext());
//        userDao = db.userDao();
//        verEducatorDao = db.verEducatorDao();
//        domainDao = db.domainDao();
//
//        Executor.executeTask(()-> {user = userDao.getById(userId);});
        // Set click listener for profile image
        profileImageView.setOnClickListener(v -> openImageChooser());

        loadUserData(userId);
        loadVerifiedStatus(userId);

        // Set click listener for save button
        TextView saveButton = rootView.findViewById(R.id.save_profile);
        saveButton.setOnClickListener(v -> saveProfile());

        // Check and request permissions for Android 14+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
            }
        }
        return rootView;
    }

    // This method will open the image chooser
    private void openImageChooser() {
        // Create a new Intent to pick an image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // Use startActivityForResult to handle the result
        startActivityForResult(intent, IMAGE_REQ);
    }
    // ActivityResultContract to handle the image picking result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQ && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadImageToCloudinary(imageUri);
        }
    }

    // Upload to Cloudinary
    private void uploadImageToCloudinary(Uri imageUri) {
        String filePath = getPathFromUri(imageUri);
        new Thread(() -> {
            try {
                // Cloudinary setting
                Map<String, String> cloudinaryConfig = new HashMap<>();
                cloudinaryConfig.put("cloud_name", getString(R.string.cloud_name));
                cloudinaryConfig.put("api_key", getString(R.string.api_key));
                cloudinaryConfig.put("api_secret", getString(R.string.api_secret));

                Cloudinary cloudinary = new Cloudinary(cloudinaryConfig);

                // upload to Cloudinary
                Map response = cloudinary.uploader().upload(filePath, ObjectUtils.emptyMap());
                String imageUrl = (String) response.get("secure_url");

                // Update Firestore user data
                updateProfilePicInFirestore(imageUrl);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Update Firestore user data
    private void updateProfilePicInFirestore(String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user")
                .document(userId)
                .update("profilePic", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                        Glide.with(EditProfileFragment.this)
                                .load(imageUrl)
                                .circleCrop()
                                .into(profileImageView);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update profile picture in Firestore", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    // get path from uri
    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }
    // Load user data from Firestore
    private void loadUserData(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            String name = document.getString("name");
//                            String emailText = document.getString("email");
                            String phoneNo = document.getString("phoneNo");
                            String profilePicUrl = document.getString("profilePic");

                            // Update UI with user data
                            if (name != null) ETname.setText(name);
//                            if (emailText != null) mail.setText(emailText);
                            if (phoneNo != null) ETnumber.setText(phoneNo);

                            if (profilePicUrl == null || profilePicUrl.equals("url link of default profile pic")) {
                                profileImageView.setImageResource(R.drawable.ic_profile);
                            } else {
                                Glide.with(EditProfileFragment.this)
                                        .load(profilePicUrl)
                                        .circleCrop()
                                        .into(profileImageView);
                            }
                        } else {
                            Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveProfile() {
        String name = ETname.getText().toString();
//        String emailText = mail.getText().toString();
        String phoneNumber = ETnumber.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user")
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!document.getId().equals(userId)) {
                                Toast.makeText(getContext(), "Name is already taken by another user", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

//                    db.collection("user")
//                            .whereEqualTo("email", emailText)
//                            .get()
//                            .addOnCompleteListener(emailTask -> {
//                                if (emailTask.isSuccessful() && !emailTask.getResult().isEmpty()) {
//                                    for (QueryDocumentSnapshot document : emailTask.getResult()) {
//                                        if (!document.getId().equals(userId)) {
//                                            Toast.makeText(getContext(), "Email is already registered by another user", Toast.LENGTH_SHORT).show();
//                                            return;
//                                        }
//                                    }
//                                }

                                Map<String, Object> updates = new HashMap<>();
                                updates.put("name", name);
//                                updates.put("email", emailText);
                                updates.put("phoneNo", phoneNumber);

                                db.collection("user")
                                        .document(userId)
                                        .update(updates)
                                        .addOnSuccessListener(aVoid ->
                                                Toast.makeText(getContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show()
                                        )
                                        .addOnFailureListener(e ->
                                                Toast.makeText(getContext(), "Failed to save profile", Toast.LENGTH_SHORT).show()
                                        );
                            });
//                });
    }

    private void loadVerifiedStatus(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("verEducator")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Set<String> uniqueDomainIds = new HashSet<>();
//                        List<String> domainIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String verifiedStatus = document.getString("verifiedStatus");
                            if ("approved".equalsIgnoreCase(verifiedStatus)) {
                                List<String> documentDomainIds = (List<String>) document.get("domainId");
                                if (documentDomainIds != null) {
                                    uniqueDomainIds.addAll(documentDomainIds);
                                }
                            }
                        }
                        List<String> domainIds = new ArrayList<>(uniqueDomainIds);

                        // Update UI with verified domains
                        loadDomainNames(domainIds);
                    } else {
                        Toast.makeText(getContext(), "Failed to load verified educators", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadDomainNames(List<String> domainIds) {
        // If no domainID，show nothing
        if (domainIds.isEmpty()) {
            updateVerifiedContainer(new ArrayList<>());
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> domainNames = new ArrayList<>();


        // Chenk domainName in domain table
        db.collection("domain")
                .whereIn("domainId", domainIds) // check all domainId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String domainName = document.getString("domainName");
                            if (domainName != null) {
                                domainNames.add(domainName);
                            }
                        }
                        //Update UI
                        updateVerifiedContainer(domainNames);
                    } else {
                        Toast.makeText(getContext(), "Failed to load domain names", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateVerifiedContainer(List<String> domainNames) {
        getActivity().runOnUiThread(() -> {
            verifiedContainer.removeAllViews();

            if (domainNames.isEmpty()) {
                TextView noRecordTextView = new TextView(getContext());
                noRecordTextView.setText("No Verification Records");
                noRecordTextView.setTextSize(16);
                noRecordTextView.setPadding(10, 10, 10, 10);
                verifiedContainer.addView(noRecordTextView);
            } else {
                for (String domainName : domainNames) {
                    TextView verifiedTextView = new TextView(getContext());
                    verifiedTextView.setText(domainName);
                    verifiedTextView.setTextSize(16);
                    verifiedTextView.setPadding(10, 10, 10, 10);
                    verifiedTextView.setTextColor(getResources().getColor(android.R.color.black));
                    verifiedContainer.addView(verifiedTextView);
                }
            }
        });
    }


}