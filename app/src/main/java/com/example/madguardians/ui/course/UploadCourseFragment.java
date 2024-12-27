package com.example.madguardians.ui.course;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.madguardians.R;
import com.example.madguardians.utilities.MediaHandler;
import com.example.madguardians.utilities.PostViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadCourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadCourseFragment extends Fragment implements MediaHandler.MediaHandleCallback {

    private String domainId;
    private ImageView IVCoverImage;
    private MediaHandler coverImageHandler;
    private Uri coverImageUri;
    private List<Uri> selectedImages = new ArrayList<>();
    private Button btnChooseImage,  btnLevel1, btnLevel2, btnLevel3, btnConfirm, btnCancel;
    private EditText ETTitle, ETDescription;
    private MediaHandler imageHandler;
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_course, container, false);
        IVCoverImage = view.findViewById(R.id.IVCoverImage);
        btnChooseImage = view.findViewById(R.id.BTNChooseImage);
        btnConfirm = view.findViewById(R.id.BTNConfirm);
        btnCancel = view.findViewById(R.id.BTNCancel);
        btnLevel1 = view.findViewById(R.id.BTNLevel1);
        btnLevel2 = view.findViewById(R.id.BTNLevel2);
        btnLevel3 = view.findViewById(R.id.BTNLevel3);
        ETTitle = view.findViewById(R.id.ETTitle);
        ETDescription = view.findViewById(R.id.ETDescription);
        // Initialize MediaHandler with the callback
        imageHandler = new MediaHandler(getContext(), imagePickerLauncher, this);
        btnChooseImage.setOnClickListener(v -> {
            imageHandler.selectImage();
        });

        btnLevel1.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("level", 1);
            Navigation.findNavController(view).navigate(R.id.nav_upload_post, bundle);
        });
        btnLevel2.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("level", 2);
            Navigation.findNavController(view).navigate(R.id.nav_upload_post, bundle);
        });
        btnLevel3.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("level", 3);
            Navigation.findNavController(view).navigate(R.id.nav_upload_post, bundle);
        });
        btnConfirm.setOnClickListener(v -> {
            if (ETTitle.getText().toString().isEmpty() || ETDescription.getText().toString().isEmpty()) {
                Toast.makeText(getContext(),"Please complete title / description", Toast.LENGTH_SHORT).show();
            } else
                confirmSelection();
        });
        btnCancel.setOnClickListener(v -> {
            clearHistory();
            Navigation.findNavController(view).navigate(R.id.nav_home);
        });
        return view;
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result != null && result.getResultCode() == RESULT_OK && result.getData() != null) {
                    coverImageUri = result.getData().getData();
                    IVCoverImage.setImageURI(coverImageUri);
                }
            }
    );

    private void confirmSelection() {
        ArrayList<PostViewModel> posts = PostViewModel.selectedMedias;
        if (posts.isEmpty()) {
            Toast.makeText(getContext(), "Please upload at least one post", Toast.LENGTH_SHORT).show();
            return;
        }
        // check if all post have title and descriptions
        for (PostViewModel p:posts) {
            if (p.getTitle().isEmpty() || p.getDescription().isEmpty()) {
                Toast.makeText(getContext(), "Please complete title / description for post " + p.getLevel(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (p.getImagesUri().isEmpty() && p.getVideosUri().isEmpty() && p.getPdfsUri().isEmpty()) {
                Toast.makeText(getContext(), "Please upload at least one media for post " + p.getLevel(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // uploading
        for (PostViewModel p:posts) {
            Log.d("post confirmed", "post title: " + p.getTitle());
            Log.d("post confirmed", "post description: " + p.getDescription());
            Log.d("post confirmed", "post imagesUri: " + p.getImagesUri().toString());
            Log.d("post confirmed", "post videosUri: " + p.getVideosUri().toString());
            Log.d("post confirmed", "post pdfUris: " + p.getPdfsUri().toString());
            // create a entity for database
            // verpost

        }
        clearHistory();
        Toast.makeText(getContext(), "Uploading", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(getView()).navigate(R.id.nav_home);
    }

    private void clearHistory() {
        PostViewModel.clearAll();
        EditText ETTitle = getView().findViewById(R.id.ETTitle);
        EditText ETDescription = getView().findViewById(R.id.ETDescription);
        ETTitle.setText("");
        ETDescription.setText("");
        selectedImages.clear();
        IVCoverImage.setImageURI(null);
    }
    @Override
    public void onMediaSelected(String filePath, String fileType) {

    }
}