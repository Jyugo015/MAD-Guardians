package com.example.madguardians.ui.course;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.work.Configuration;

import com.example.madguardians.R;
import com.example.madguardians.firebase.CourseFB;
import com.example.madguardians.utilities.MediaHandler;
import com.example.madguardians.utilities.MediasHandler;
import com.example.madguardians.utilities.UploadCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadCourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadCourseFragment extends Fragment implements MediaHandler.MediaHandleCallback {

    private String domainId;
    private String folderId;
    private String userId;
    private ImageView IVCoverImage;
    private MediaHandler coverImageHandler;
    private Button btnChooseImage, btnUploadPost, btnLevel2, btnLevel3, btnConfirm, btnCancel;
    private EditText ETTitle, ETDescription;
    private MediasHandler imagesHandler;
    private MediasHandler videosHandler;
    private MediasHandler pdfsHandler;
    private CourseViewModel courseViewModel;
    private static final String TAG = "UploadCourseFragment";

    public UploadCourseFragment() {
        // Required empty public constructor
    }

    public static UploadCourseFragment newInstance(String param1, String param2) {
        UploadCourseFragment fragment = new UploadCourseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            domainId = getArguments().getString("domainId");
            folderId = getArguments().getString("folderId");
            Log.d(TAG, "onCreate: domainId " + domainId);
            Log.d(TAG, "onCreate: folderId " + folderId);
        }
//        WorkManager.initialize(getContext(), getWorkManagerConfiguration());
        userId = "U0001";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_course, container, false);
        IVCoverImage = view.findViewById(R.id.IVCoverImage);
        btnChooseImage = view.findViewById(R.id.BTNChooseImage);
        btnConfirm = view.findViewById(R.id.BTNConfirm);
        btnCancel = view.findViewById(R.id.BTNCancel);
        btnUploadPost = view.findViewById(R.id.BTNUploadPost);
//        btnLevel2 = view.findViewById(R.id.BTNLevel2);
//        btnLevel3 = view.findViewById(R.id.BTNLevel3);
        ETTitle = view.findViewById(R.id.ETTitle);
        ETDescription = view.findViewById(R.id.ETDescription);
        // Initialize MediaHandler with the callback
        coverImageHandler = new MediaHandler(getContext(), imagePickerLauncher, this);
        imagesHandler = new MediasHandler(getContext(), imagePickerLauncher, "image",null);
        videosHandler = new MediasHandler(getContext(), imagePickerLauncher, "video",null);
        pdfsHandler = new MediasHandler(getContext(), imagePickerLauncher, "pdf",null);
        btnChooseImage.setOnClickListener(v -> {
            coverImageHandler.selectImage();
        });
        restoreData();
        Log.d(TAG, "uploadCourse: domainId " + domainId);
        Log.d(TAG, "uploadCourse: folderId " + folderId);

        btnUploadPost.setOnClickListener(v -> {
            saveCourse();
            Bundle bundle = new Bundle();
            bundle.putInt("level", 1);
            bundle.putString("folderId", folderId);
            Navigation.findNavController(view).navigate(R.id.nav_upload_post, bundle);
        });
//        btnLevel2.setOnClickListener(v -> {
//            Bundle bundle = new Bundle();
//            bundle.putInt("level", 2);
//            Navigation.findNavController(view).navigate(R.id.nav_upload_post, bundle);
//        });
//        btnLevel3.setOnClickListener(v -> {
//            Bundle bundle = new Bundle();
//            bundle.putInt("level", 3);
//            Navigation.findNavController(view).navigate(R.id.nav_upload_post, bundle);
//        });
        btnConfirm.setOnClickListener(v -> {
            if (! validateCourse()) {
                Toast.makeText(getContext(),"Please complete title / description / cover image", Toast.LENGTH_SHORT).show();
            } else {
                confirmSelection();
                Toast.makeText(getContext(), "Uploading", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(getView()).navigate(R.id.nav_home);
            }
        });
        btnCancel.setOnClickListener(v -> {
            clearHistory();
            Navigation.findNavController(view).navigate(R.id.nav_home);
        });
        return view;
    }

    private void saveCourse() {
        Log.d(TAG, "saveCourse: ");
        Log.d(TAG, "saveCourse: domainId " + domainId);
        Log.d(TAG, "saveCourse: folderId " + folderId);
        courseViewModel.setTitle(ETTitle.getText().toString());
        courseViewModel.setDescription(ETDescription.getText().toString());
        courseViewModel.setDomainId(domainId);
        courseViewModel.setFolderId(folderId);
    }

    private boolean validateCourse() {
        if (ETTitle.getText().toString().isEmpty() || ETDescription.getText().toString().isEmpty() || courseViewModel.getCoverImageUri() == null) return false;
        ArrayList<PostViewModel> posts = PostViewModel.selectedMedias;
        if (posts.isEmpty()) {
            Toast.makeText(getContext(), "Please upload at least one post", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean isPost1Set = false;
        boolean isPost2Set = false;
        boolean isPost3Set = false;
        // check if all post have title and descriptions
        for (PostViewModel p:posts) {
            if (p.getTitle().isEmpty() || p.getDescription().isEmpty()) {
                Toast.makeText(getContext(), "Please complete title / description for post " + p.getLevel(), Toast.LENGTH_SHORT).show();
                return false;
            }
            if (p.getImagesUri().isEmpty() && p.getVideosUri().isEmpty() && p.getPdfsUri().isEmpty()) {
                Toast.makeText(getContext(), "Please upload at least one media for post " + p.getLevel(), Toast.LENGTH_SHORT).show();
                return false;
            }
            if (p.getLevel() == 1) isPost1Set = true;
            else if (p.getLevel() == 2) isPost2Set = true;
            else if (p.getLevel() == 3) isPost3Set = true;
        }

        if (!isPost1Set || (!isPost2Set && isPost3Set)) {
            Toast.makeText(getContext(), "Please upload according to level", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void restoreData() {
        courseViewModel = CourseViewModel.getCourse();
        ETTitle.setText(courseViewModel.getTitle());
        ETDescription.setText(courseViewModel.getDescription());
        domainId = courseViewModel.getDomainId() == null ? domainId : courseViewModel.getDomainId();
        folderId = courseViewModel.getFolderId() == null ? folderId : courseViewModel.getFolderId();
        Log.d(TAG, "restoreData: title " + courseViewModel.getTitle());
        Log.d(TAG, "restoreData: description " + courseViewModel.getDescription());
        Log.d(TAG, "restoreData: domainId " + courseViewModel.getDomainId());
        Log.d(TAG, "restoreData: folderId " + folderId);
        if (courseViewModel.getCoverImageUri() != null) {
            IVCoverImage.setImageURI(courseViewModel.getCoverImageUri());
        };
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result != null && result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri coverImageUri = result.getData().getData();
                    courseViewModel.setCoverImageUri(coverImageUri);
                    IVCoverImage.setImageURI(coverImageUri);
                }
            }
    );

    private void confirmSelection() {
        // upload cover image
        if (courseViewModel.getCoverImageUri() != null){;
            coverImageHandler.uploadImageInBackground(courseViewModel.getCoverImageUri(), new UploadCallback<String>(){
                @Override
                public void onSuccess(String coverImageUrl) {
                    Log.d(TAG, "coverImageUrl: " + coverImageUrl);
                    uploadCourse(coverImageUrl);
                }
                @Override
                public void onFailure(Exception e) {}
            });
        }
    }

    private void uploadCourse(String coverImageUrl) {
        ArrayList<PostViewModel> posts = PostViewModel.selectedMedias;
        String postId1 = null;
        String postId2 = null;
        String postId3 = null;
        for (PostViewModel post : posts) {
            Log.d(TAG, "uploadCourse: post.getLevel() " + post.getLevel());
            if (post.getLevel() == 1) postId1 = post.getPostId();
            else if (post.getLevel() == 2) postId2 = post.getPostId();
            else if (post.getLevel() == 3) postId3 = post.getPostId();
        }

        Log.d(TAG, "uploadCourse: domainId " + domainId);
        Log.d(TAG, "uploadCourse: folderId " + folderId);
        Log.d(TAG, "uploadCourse: postId1 " + postId1);
        Log.d(TAG, "uploadCourse: postId2 " + postId2);
        Log.d(TAG, "uploadCourse: postId3 " + postId3);
        HashMap<String, Object> courseHashMap = CourseFB.createCourseData(courseViewModel.getTitle(), userId, courseViewModel.getDescription(), coverImageUrl, postId1, postId2, postId3, domainId, folderId, generateDate());
        Log.d(TAG, "uploadPostIfAvailable: uploading course");
        CourseFB.insertCourse(courseHashMap, new UploadCallback<String>(){
            @Override
            public void onSuccess(String courseId) {
                Log.d(TAG, "uploadPostIfAvailable: uploaded courses successfully");
                sendForVerification(courseId);
                clearHistory();
            }
            @Override
            public void onFailure(Exception e) {

            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        saveCourse();
    }

    private static String generateDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(new Date());
        Log.d(TAG, "generateDate: " + date);
        return date;
    }

    private void clearHistory() {
        PostViewModel.clearAll();
        CourseViewModel.clear(courseViewModel);
        IVCoverImage.setImageURI(null);
    }
    @Override
    public void onMediaSelected(String filePath, String fileType) {

    }

    //////////////////////////////////////////////////////////////////
    // zw
    private void sendForVerification(String courseId) {
    }
}