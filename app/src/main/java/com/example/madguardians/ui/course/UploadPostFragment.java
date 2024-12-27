package com.example.madguardians.ui.course;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madguardians.R;
import com.example.madguardians.utilities.PostViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadPostFragment extends Fragment{

    private int level;
    private MediasHandler imagesHandler;
    private MediasHandler videosHandler;
    private MediasHandler pdfsHandler;
    private EditText ETTitle, ETDescription;
    private PostViewModel postViewModel;
    private View view;

    public UploadPostFragment() {
        // Required empty public constructor
    }

    public static UploadPostFragment newInstance(String param1, String param2) {
        UploadPostFragment fragment = new UploadPostFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            level = getArguments().getInt("level");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_upload_post, container, false);
        ETTitle = view.findViewById(R.id.ETTitle);
        ETDescription = view.findViewById(R.id.ETDescription);
        TextView TVPost = view.findViewById(R.id.TVPost);
        RecyclerView RVImages = view.findViewById(R.id.RVImages);
        RecyclerView RVVideos = view.findViewById(R.id.RVVideos);
        RecyclerView RVPDFs = view.findViewById(R.id.RVPDFs);
        Button btnImages = view.findViewById(R.id.BTNImages);
        Button btnVideos = view.findViewById(R.id.BTNVideos);
        Button btnPDFs = view.findViewById(R.id.BTNPDFs);
        Button btnQuiz = view.findViewById(R.id.BTNQuizs);
        Button btnConfirm = view.findViewById(R.id.BTNConfirm);
        Button btnCancel = view.findViewById(R.id.BTNCancel);

        TVPost.setText("Upload Post " + level);

        imagesHandler = new MediasHandler(getContext(), pickMultipleImages, "image", RVImages);
        videosHandler = new MediasHandler(getContext(), pickMultipleVideos, "video", RVVideos);
        pdfsHandler = new MediasHandler(getContext(), pickMultiplePdfs, "pdf", RVPDFs);

        restoreData();

        btnImages.setOnClickListener(v -> {
            imagesHandler.selectImages();
        });
        btnVideos.setOnClickListener(v -> {
            videosHandler.selectVideos();
        });
        btnPDFs.setOnClickListener(v -> {
            pdfsHandler.selectPDFs();
        });
        btnQuiz.setOnClickListener(v-> {
            Toast.makeText(getContext(), "Connecting to quiz", Toast.LENGTH_SHORT).show();
        });
        btnConfirm.setOnClickListener(v -> {
            if (ETTitle.getText().toString().isEmpty() || ETDescription.getText().toString().isEmpty()) {
                Toast.makeText(getContext(),"Please complete title / description", Toast.LENGTH_SHORT).show();
            } else
                confirmSelection();
        });
        btnCancel.setOnClickListener(v -> {
            postViewModel.clear();
            resetUI();
            Navigation.findNavController(view).navigate(R.id.nav_upload_course);
        });

        return view;
    }

    private void resetUI() {
        ETTitle.setText("");
        ETDescription.setText("");
        imagesHandler.clearSelectedMedias();
        videosHandler.clearSelectedMedias();
        pdfsHandler.clearSelectedMedias();
    }

    private final ActivityResultLauncher<Intent> pickMultipleImages =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result != null && result.getData() != null) {
                    ClipData clipData = result.getData().getClipData();
                    if (clipData != null) {
                        // Handle multiple file selection
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri fileUri = clipData.getItemAt(i).getUri();
                            imagesHandler.handleResult(fileUri);
                        }
                    } else {
                        // Handle single file selection
                        Uri fileUri = result.getData().getData();
                        imagesHandler.handleResult(fileUri);
                    }
                }

            });

    private final ActivityResultLauncher<Intent> pickMultipleVideos =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result != null && result.getData() != null) {
                    ClipData clipData = result.getData().getClipData();
                    if (clipData != null) {
                        // Handle multiple file selection
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri fileUri = clipData.getItemAt(i).getUri();
                            videosHandler.handleResult(fileUri);
                        }
                    } else {
                        // Handle single file selection
                        Uri fileUri = result.getData().getData();
                        videosHandler.handleResult(fileUri);
                    }
                }
            });
    private final ActivityResultLauncher<Intent> pickMultiplePdfs =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result != null && result.getData() != null) {
                    ClipData clipData = result.getData().getClipData();
                    if (clipData != null) {
                        // Handle multiple file selection
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri fileUri = clipData.getItemAt(i).getUri();
                            pdfsHandler.handleResult(fileUri);
                        }
                    } else {
                        // Handle single file selection
                        Uri fileUri = result.getData().getData();
                        pdfsHandler.handleResult(fileUri);
                    }
                }
            });

    @Override
    public void onStop() {
        super.onStop();
        confirmSelection();
    }

    private void confirmSelection() {
        postViewModel.getImagesUri().clear();
        postViewModel.getVideosUri().clear();
        postViewModel.getPdfsUri().clear();

        postViewModel.getImagesUri().addAll(imagesHandler.getSelectedMedias());
        postViewModel.getVideosUri().addAll(videosHandler.getSelectedMedias());
        postViewModel.getPdfsUri().addAll(pdfsHandler.getSelectedMedias());
        postViewModel.setTitle(ETTitle.getText().toString());
        postViewModel.setDescription(ETDescription.getText().toString());
        postViewModel.setLevel(level);
        Navigation.findNavController(view).navigate(R.id.nav_upload_course);
    }

    private void restoreData() {
        postViewModel = PostViewModel.getViewModel(level);
        if (postViewModel != null) {
            // Restore the data from the ViewModel
            if (!postViewModel.getImagesUri().isEmpty()) {
                imagesHandler.loadSavedMedia(postViewModel.getImagesUri());
            }
            if (!postViewModel.getVideosUri().isEmpty()) {
                videosHandler.loadSavedMedia(postViewModel.getVideosUri());
            }
            if (!postViewModel.getPdfsUri().isEmpty()) {
                pdfsHandler.loadSavedMedia(postViewModel.getPdfsUri());
            }
            ETTitle.setText(postViewModel.getTitle());
            ETDescription.setText(postViewModel.getDescription());
        } else {
            postViewModel = new PostViewModel(level);
        }
    }
}