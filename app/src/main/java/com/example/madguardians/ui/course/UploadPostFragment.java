package com.example.madguardians.ui.course;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.firebase.MediaFB;
import com.example.madguardians.firebase.PostFB;
import com.example.madguardians.utilities.FirebaseController;
import com.example.madguardians.utilities.MediasHandler;
import com.example.madguardians.utilities.UploadCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
    private static final String TAG = "UploadPostFragment";
    private String userId;
    private String domainId;
    private String folderId;
    private Queue<List<Uri>> uriQueue = new LinkedList<>();
    private Queue<String> mediaTypeQueue = new LinkedList<>();
    private Queue<UploadCallback> callbackQueue = new LinkedList<>();

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
            folderId = getArguments().getString("folderId");
        }
        userId = "U0001";

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
            if (isValidate())
                confirmSelection();
        });
        btnCancel.setOnClickListener(v -> {
            PostViewModel.selectedMedias.remove(postViewModel);
            resetUI();
            Navigation.findNavController(view).navigate(R.id.nav_upload_course);
        });

        return view;
    }

    private boolean isValidate() {
        if (ETTitle.getText().toString().isEmpty() || ETDescription.getText().toString().isEmpty()) {
            Toast.makeText(getContext(),"Please complete title / description", Toast.LENGTH_SHORT).show();
            return false;
        } else if (imagesHandler.getSelectedMedias().isEmpty() && videosHandler.getSelectedMedias().isEmpty() && pdfsHandler.getSelectedMedias().isEmpty()){
            Toast.makeText(getContext(),"Please upload at least one media", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
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
        Log.d(TAG, "confirmSelection: level " + level);

        //updatePost if want
        if (postViewModel.getPostId() != null) {
            updatePost();
        } else {
            //uploadPost
            Log.d(TAG, "confirmSelection: uploading media");
            uploadMedias(postViewModel.getImagesUri(), FirebaseController.IMAGE, new UploadCallback<String>() {
                @Override
                public void onSuccess(String mediaSetId) {
                    Log.d(TAG, "onSuccess upload images in post");
                    postViewModel.setImageUploaded(true);
                    postViewModel.setImageSetId(mediaSetId);
                    uploadPostIfAvailable();
                }
                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "onFailure upload images in post");
                    Log.e(TAG, "onFailure: ", e);
                }
            });
            uploadMedias(postViewModel.getVideosUri(), FirebaseController.VIDEO, new UploadCallback<String>() {
                @Override
                public void onSuccess(String mediaSetId) {
                    Log.d(TAG, "onSuccess upload video in post");
                    postViewModel.setVideosUploaded(true);
                    postViewModel.setVideoSetId(mediaSetId);
                    uploadPostIfAvailable();
                }
                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "onFailure upload video in post");
                    Log.e(TAG, "onFailure: ", e);
                }
            });
            uploadMedias(postViewModel.getPdfsUri(), FirebaseController.PDF, new UploadCallback<String>() {
                @Override
                public void onSuccess(String mediaSetId) {
                    Log.d(TAG, "onSuccess upload pdfs in post");
                    postViewModel.setPdfsUploaded(true);
                    postViewModel.setPdfSetId(mediaSetId);
                    uploadPostIfAvailable();
                }
                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "onFailure upload pdfs in post");
                    Log.e(TAG, "onFailure: ", e);
                }
            });
            Log.d("post confirmed", "post title: " + postViewModel.getTitle());
            Log.d("post confirmed", "post description: " + postViewModel.getDescription());
            Log.d("post confirmed", "post imagesUri: " + postViewModel.getImagesUri().toString());
            Log.d("post confirmed", "post videosUri: " + postViewModel.getVideosUri().toString());
            Log.d("post confirmed", "post pdfUris: " + postViewModel.getPdfsUri().toString());
        }

        if (level >= 1 && level <= 2) {
            int newLevel = level + 1;
            Log.d(TAG, "confirmSelection: going to level "+ newLevel);
            Bundle bundle = new Bundle();
            bundle.putInt("level", newLevel);
            bundle.putString("folderId", folderId);
            Navigation.findNavController(view).navigate(R.id.nav_upload_post, bundle);
        } else {
            Navigation.findNavController(view).navigate(R.id.nav_upload_course);
        }
    }

    private void updatePost() {
        HashMap<String, Object> hashMap = PostFB.createPostData(userId, postViewModel.getTitle(), postViewModel.getDescription(), postViewModel.getImageSetId(), postViewModel.getVideoSetId(), postViewModel.getPdfSetId(), postViewModel.getQuizSetId(), folderId, generateDate());
        PostFB.updatePost(hashMap);
    }

    private void uploadPostIfAvailable() {
        Log.d(TAG, "uploadPostIfAvailable: ");
        if (postViewModel.isImageUploaded() && postViewModel.isVideosUploaded() && postViewModel.isPdfsUploaded()) {
            Log.d(TAG, "uploadPostIfAvailable: All media uploaded");
            Queue<HashMap<String, Object>> queue = new LinkedList<>();
            HashMap<String, Object> hashMap = PostFB.createPostData(userId, postViewModel.getTitle(), postViewModel.getDescription(), postViewModel.getImageSetId(), postViewModel.getVideoSetId(), postViewModel.getPdfSetId(), postViewModel.getQuizSetId(), folderId, generateDate());
            queue.add(hashMap);

            Log.d(TAG, "uploadPostIfAvailable: uploading post");
            PostFB.uploadNextPost(queue, new UploadCallback<List<HashMap<String, Object>>>(){
                @Override
                public void onSuccess(List<HashMap<String, Object>> postHashMaps) {
                    Log.d(TAG, "uploadPostIfAvailable: successfully uploaded post " + level);
                    postViewModel.setPostId(postHashMaps.get(0).get("postId").toString());
                }
                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "onFailure: uploadPostIFAvaillable", e);
                }
            });
        }
    }

    private void uploadMedias(List<Uri> uris, String mediaType, UploadCallback<String> mediaSetIdCallback) {
        Log.d(TAG, "uploadMedias: queing");
        uriQueue.add(uris);
        mediaTypeQueue.add(mediaType);
        callbackQueue.add(mediaSetIdCallback);
        if (uriQueue.size() == 1) {
            uploadMedia();
        }
    }
    private void uploadMedia() {
        if (uriQueue.isEmpty()) {
            Log.d(TAG, "uploadMedia: done uploading");
//            uploadPostIfAvailable();
//            postViewModel.setImageUploaded(true);
//            postViewModel.setVideosUploaded(true);
//            postViewModel.setPdfsUploaded(true);
            return;
        }
        Log.d(TAG, "uploadMedia: here1");
        List<Uri> uris = uriQueue.peek();
        String mediaType = mediaTypeQueue.peek();
        UploadCallback mediaSetIdCallback = callbackQueue.peek();
        if (uris != null && !uris.isEmpty()) {
            Log.d(TAG, "uploadMedia: here2");
            Queue<Uri> queue = new LinkedList<>(uris);
            FirebaseController.generateDocumentId(FirebaseController.MEDIASET, new UploadCallback<String>() {
                @Override
                public void onSuccess(String mediaSetId) {
                    Log.d(TAG, "onSuccess: mediaType " + mediaType);
                    if (mediaType.equals(FirebaseController.IMAGE)) {
                        imagesHandler.uploadMediasInBackground(queue, new UploadCallback<List<String>>(){
                            @Override
                            public void onSuccess(List<String> URLs) {
                                for (String URL: URLs) {
                                    HashMap<String, Object> hashMap = MediaFB.createMediaData(FirebaseController.IMAGE, mediaSetId, URL);
                                    insertMedia(hashMap, mediaSetId, mediaSetIdCallback);
                                    uriQueue.poll();
                                    mediaTypeQueue.poll();
                                    callbackQueue.poll();
                                    uploadMedia();
                                };
                            }
                            @Override
                            public void onFailure(Exception e) {mediaSetIdCallback.onFailure(e);}
                        });
                    } else if (mediaType.equals(FirebaseController.VIDEO)){
                        videosHandler.uploadMediasInBackground(queue, new UploadCallback<List<String>>(){
                            @Override
                            public void onSuccess(List<String> URLs) {
                                for (String URL: URLs) {
                                    HashMap<String, Object> hashMap = MediaFB.createMediaData(FirebaseController.VIDEO, mediaSetId, URL);
                                    insertMedia(hashMap, mediaSetId, mediaSetIdCallback);
                                    uriQueue.poll();
                                    mediaTypeQueue.poll();
                                    callbackQueue.poll();
                                    uploadMedia();
                                };
                            }
                            @Override
                            public void onFailure(Exception e) {mediaSetIdCallback.onFailure(e);}
                        });
                    } else if (mediaType.equals(FirebaseController.PDF)){
                        Log.d(TAG, "onSuccess: uploading pdf");
                        pdfsHandler.uploadMediasInBackground(queue, new UploadCallback<List<String>>(){
                            @Override
                            public void onSuccess(List<String> URLs) {
                                for (String URL: URLs) {
                                    HashMap<String, Object> hashMap = MediaFB.createMediaData(FirebaseController.PDF, mediaSetId, URL);
                                    insertMedia(hashMap, mediaSetId, mediaSetIdCallback);
                                    uriQueue.poll();
                                    mediaTypeQueue.poll();
                                    callbackQueue.poll();
                                    uploadMedia();
                                };
                            }
                            @Override
                            public void onFailure(Exception e) {mediaSetIdCallback.onFailure(e);}
                        });
                    }
                }
                @Override
                public void onFailure(Exception e) {mediaSetIdCallback.onFailure(e);}
            });
        } else {
            Log.d(TAG, "uploadMedia: uris is empty");
            if (mediaType.equals(FirebaseController.IMAGE)) {
                postViewModel.setImageUploaded(true);
            }
            else if (mediaType.equals(FirebaseController.VIDEO)) {
                postViewModel.setVideosUploaded(true);
            }
            else if (mediaType.equals(FirebaseController.PDF)) {
                postViewModel.setPdfsUploaded(true);
            }
            uriQueue.poll();
            mediaTypeQueue.poll();
            callbackQueue.poll();
            mediaSetIdCallback.onSuccess(null);
            uploadMedia();
        }
    }

    private void insertMedia(HashMap<String, Object> hashMap, String mediaSetId, UploadCallback<String> callback) {
        MediaFB.insertMedia(hashMap, new UploadCallback<Boolean>(){
            @Override
            public void onSuccess(Boolean result) {
                Log.d(TAG, "onSuccess: upload into media");
                callback.onSuccess(mediaSetId);
            }
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "onFailure upload media in post");
                Log.e(TAG, "onFailure: ", e);
            }
        });
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

    private static String generateDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(new Date());
        Log.d(TAG, "generateDate: " + date);
        return date;
    }
}