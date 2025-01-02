package com.example.madguardians.utilities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkRequest;

import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.madguardians.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediasHandler {

//    public interface MediaHandleCallback {
//        void onMediaSelected(String filePath, String fileType);
//    }
    private final Context context;
    private final ActivityResultLauncher<Intent> activityResultLauncher;
//    private final MediaHandleCallback callback;
    private AdapterMedia mediaAdapter;
    private List<Uri> selectedMedias;
    private List<String> mediaURLs = new ArrayList<>();
//    private List<String> VideoURLs = new ArrayList<>();
//    private List<String> PdfURLs = new ArrayList<>();
    private String mediaType;
    private Cloudinary cloudinary;
    private static final String TAG = "MediasHandler";

    public MediasHandler(@NonNull Context context, @NonNull ActivityResultLauncher<Intent> activityResultLauncher, String mediaType, @Nullable RecyclerView RVMedias) {
        this.context = context;
        this.activityResultLauncher = activityResultLauncher;
        this.mediaType = mediaType;
//        this.callback = callback;

        Map<String, String> cloudinaryConfig = new HashMap<>();
        cloudinaryConfig.put("cloud_name", context.getString(R.string.cloud_name));
        cloudinaryConfig.put("api_key", context.getString(R.string.api_key));
        cloudinaryConfig.put("api_secret", context.getString(R.string.api_secret));
        cloudinary = new Cloudinary(cloudinaryConfig);

        if (RVMedias != null) {
            selectedMedias = new ArrayList<>();
            mediaAdapter = new AdapterMedia(selectedMedias, mediaType, this::removeMedia);
            RVMedias.setLayoutManager(new LinearLayoutManager(context));
            RVMedias.setAdapter(mediaAdapter);
        }
    }

    // Select Image
    public void selectImages(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activityResultLauncher.launch(intent);
    }

    public void selectVideos(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activityResultLauncher.launch(intent);
    }

    public void selectPDFs() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activityResultLauncher.launch(intent);
    }

    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }

    public void handleResult(Uri uri) {
        if (uri != null) {
            long size = 0;
            if (mediaType.equalsIgnoreCase("image") || mediaType.equalsIgnoreCase("pdf")) {
                Log.d(TAG, "handleResult: here1");
                String[] projection = {MediaStore.Images.Media.DATA, OpenableColumns.SIZE};
                try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);) {
                    Log.d(TAG, "handleResult: here2");
                    if (cursor != null && cursor.moveToFirst()){
                        Log.d(TAG, "handleResult: here3");
                        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                        Log.d(TAG, "size: " + sizeIndex);
                        if (sizeIndex != -1){
                            Log.d(TAG, "handleResult: here4");
                            size = cursor.getLong(sizeIndex);
                            if (size < 10485760) {
                                Log.d(TAG, "handleResult: here5");
//                                String filePath = getPathFromUri(uri);
                                selectedMedias.add(uri);
                                Log.d("pick images / pdfs", "Selected Images / Pdfs: " + selectedMedias.toString());
                                mediaAdapter.notifyDataSetChanged();
//                                callback.onMediaSelected(filePath, "image");
                            } else
                                Toast.makeText(context, "The file is too large (Maximum 10MB), please try another file", Toast.LENGTH_LONG).show();
                        } else
                            Log.d(TAG, "handleResult: here6");
                    } else
                        Log.d(TAG, "handleResult: here7");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (mediaType.equalsIgnoreCase("video")) {
                try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()){
                        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                        if (sizeIndex != -1){
                            size = cursor.getLong(sizeIndex);
                            if (size < 104857600) {
                                selectedMedias.add(uri);
                                Log.d("pick videos", "Selected Videos: " + selectedMedias.toString());
                                mediaAdapter.notifyDataSetChanged();
//                                String filePath = getPathFromUri(uri);
//                                callback.onMediaSelected(filePath, "video");
                            } else
                                Toast.makeText(context, "The file is too large (Maximum 100MB), please try another file", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d("Media Handler", "The filepath is null");

        }
    }

    private byte[] getBytesFromUri(Uri pdfUri) {
        ContentResolver contentResolver = context.getContentResolver();
        try (InputStream inputStream = contentResolver.openInputStream(pdfUri);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (inputStream != null) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
            }
            return outputStream.toByteArray();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadMediasInBackground(Queue<Uri> uris, UploadCallback<List<String>> callback) {
        if (uris.isEmpty()) {
            Log.d(TAG, "uploadMediasInBackground: done");
            callback.onSuccess(mediaURLs);
            mediaURLs.clear();
            return;
        }
        Log.d(TAG, "uploadMediasInBackground: here1");
        Uri uri = uris.poll();
        Log.d(TAG, "uploadMediasInBackground: here2");
//        String filePath = (mediaType.equals(FirebaseController.IMAGE) || mediaType.equals(FirebaseController.VIDEO)) ? getPathFromUri(uri) : getBytesFromUri(uri);
//        Log.d(TAG, "uploadMediasInBackground: filePath " + filePath);
        Toast.makeText(context, "Uploading " + mediaType, Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            try {
                Map response = null;
                // Perform upload to Cloudinary
                if (mediaType.equalsIgnoreCase("image") ) {
                    Log.d(TAG, "uploadMediasInBackground: isImage");
                    String filePath = getPathFromUri(uri);
                    response = cloudinary.uploader().upload(filePath, ObjectUtils.emptyMap());
                } else if (mediaType.equalsIgnoreCase("video")) {
                    Log.d(TAG, "uploadMediasInBackground: isVideo");
//                    String filePath = getPathFromUri(uri);
                    byte[] filePath = getBytesFromUri(uri);
                    response = cloudinary.uploader().upload(filePath, ObjectUtils.asMap("resource_type", "video"));
                } else if (mediaType.equalsIgnoreCase("pdf")) {
                    Log.d(TAG, "uploadMediasInBackground: isPDF");
                    byte[] filePath = getBytesFromUri(uri);
//                    File file = new File(filePath);
//                    byte[] bytes = Files.readAllBytes(file.toPath());
                    response = cloudinary.uploader().upload(filePath, ObjectUtils.asMap("resource_type", "raw"));
                }

                if (response != null){
                    String mediaUrl = (String) response.get("secure_url");
                    // Save the url for later need
                    ((Activity) context).runOnUiThread(() -> {
                        Log.d("Cloudinary URL", mediaUrl);
                        mediaURLs.add(mediaUrl);
                        uploadMediasInBackground(uris, callback);
                    });
                } else {
                    Log.d(TAG, "uploadMediasInBackground: response is null");
                }

            } catch (IOException e) {
                callback.onFailure(e);
            }
        }).start();

    }

    private String saveBytesToCacheFile(byte[] filePathBytes) {
        try {
            File file = new File(context.getCacheDir(), "temp.pdf");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(filePathBytes);
            }
            return file.getAbsolutePath(); // Return the file path
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void deleteCacheFile(String s) {
        File tempFile = new File(s);
        if (tempFile.delete()) {
            Log.d("Worker", "Temporary file deleted");
        } else {
            Log.e("Worker", "Failed to delete temporary file");
        }
    }

//    private void handleUploadedURL(String imageUrl, String database) {
//        Log.d(TAG, "handleUploadedURL: Wow still can work!");
//    }

    // Display Image
//    public static void displayImage(Context context, String imageUrl, ImageView imageView) {
//        Log.d("Cloudinary URL", imageUrl);
//        Glide.with(context).load(imageUrl).into(imageView);
//    }
//
//    // Display PDF
//    public static void displayPDF(String pdfUrl, WebView webView) {
//        Log.d("Cloudinary URL", pdfUrl);
//        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdfUrl);
//    }
//
//    public static void playVideo(String videoUrl, ExoPlayer player) {
////        mediaItem = MediaItem.fromUri(Uri.parse(toHTTPS("http://res.cloudinary.com/dmgpozfee/video/upload/v1731385187/cgtajcgfoloqc4calfov.mp4")));
//        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
//        player.setMediaItem(mediaItem);
//
//        player.prepare();
//        player.setVolume(1.0f);
//        player.play();
//    }

    private void removeMedia(Uri uri) {
            selectedMedias.remove(uri);
            mediaAdapter.notifyDataSetChanged();
    }

    public List<Uri> getSelectedMedias() {
        return selectedMedias;
    }

    public void clearSelectedMedias() {
        selectedMedias.clear();
        mediaAdapter.notifyDataSetChanged();
    }

    public void loadSavedMedia(List<Uri> savedMedias) {
        selectedMedias.clear();
        selectedMedias.addAll(savedMedias);
        mediaAdapter.notifyDataSetChanged();
    }
}

