package com.example.madguardians.ui.course;

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
import androidx.lifecycle.LifecycleOwner;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.bumptech.glide.Glide;
import com.example.madguardians.database.CloudinaryUploadWorker;
import com.example.madguardians.utilities.AdapterMedia;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MediasHandler {

//    public interface MediaHandleCallback {
//        void onMediaSelected(String filePath, String fileType);
//    }
    private final Context context;
    private final ActivityResultLauncher<Intent> activityResultLauncher;
//    private final MediaHandleCallback callback;
    private WorkRequest uploadWorkRequest;
    private AdapterMedia mediaAdapter;
    private List<Uri> selectedMedias;
    private String mediaType;

    public MediasHandler(@NonNull Context context, @NonNull ActivityResultLauncher<Intent> activityResultLauncher, String mediaType, @Nullable RecyclerView RVMedias) {
        this.context = context;
        this.activityResultLauncher = activityResultLauncher;
        this.mediaType = mediaType;
//        this.callback = callback;

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
                Log.d("TAG", "handleResult: here1");
                String[] projection = {MediaStore.Images.Media.DATA, OpenableColumns.SIZE};
                try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);) {
                    Log.d("TAG", "handleResult: here2");
                    if (cursor != null && cursor.moveToFirst()){
                        Log.d("TAG", "handleResult: here3");
                        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                        Log.d("TAG", "size: " + sizeIndex);
                        if (sizeIndex != -1){
                            Log.d("TAG", "handleResult: here4");
                            size = cursor.getLong(sizeIndex);
                            if (size < 10485760) {
                                Log.d("TAG", "handleResult: here5");
//                                String filePath = getPathFromUri(uri);
                                selectedMedias.add(uri);
                                Log.d("pick images / pdfs", "Selected Images / Pdfs: " + selectedMedias.toString());
                                mediaAdapter.notifyDataSetChanged();
//                                callback.onMediaSelected(filePath, "image");
                            } else
                                Toast.makeText(context, "The file is too large (Maximum 10MB), please try another file", Toast.LENGTH_LONG).show();
                        } else
                            Log.d("TAG", "handleResult: here6");
                    } else
                        Log.d("TAG", "handleResult: here7");
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
//            } else if (mediaType.equalsIgnoreCase("pdf")) {
//                try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
//                    if (cursor != null && cursor.moveToFirst()){
//                        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
//                        if (sizeIndex != -1){
//                            size = cursor.getLong(sizeIndex);
//                            if (size < 10485760) {
//                                Log.d("TAG", "handleResult: pdfUri: " + uri);
//                                byte[] filePathBytes = getBytesFromUri(uri);
////                                String filePath = saveBytesToCacheFile(filePathBytes);
//                                Log.d("TAG", "handleResult: path: " + filePath);
////                                callback.onMediaSelected(filePath, "pdf");
//                            } else
//                                Toast.makeText(context, "The file is too large (Maximum 10MB), please try another file", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
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
    public void uploadImageInBackground(Uri uri, String database, @Nullable ImageView imageView) {
        String filePath = getPathFromUri(uri);
        Toast.makeText(context, "Uploading image", Toast.LENGTH_LONG).show();
        Data data = new Data.Builder()
                .putString("filePath", filePath)
                .putString("fileType", "image")
                .build();

        uploadWorkRequest = new OneTimeWorkRequest.Builder(CloudinaryUploadWorker.class)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context).enqueue(uploadWorkRequest);

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(uploadWorkRequest.getId())
                .observe((LifecycleOwner) context, workInfo -> {
                    if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                        // Retrieve the uploaded UR
                        String imageUrl = workInfo.getOutputData().getString("uploadedUrl");

                        Log.d("TAG", "uploadImageInBackground: Succeeded " + imageUrl);
                        if (imageUrl != null) {
                            Toast.makeText(context, "Video Uploaded Successfully", Toast.LENGTH_SHORT).show();

                            // Other logic (eg. save in the database
                            handleUploadedURL(imageUrl, database);

                            if (imageView != null) {
                                displayImage(context, imageUrl, imageView);
                            }
                        } else {
                            Log.d("TAG", "uploadImageInBackground: null url");
                        }
                    } else if (workInfo != null && workInfo.getState() == WorkInfo.State.FAILED) {
                        Toast.makeText(context, "Video Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void uploadVideoInBackground(Uri uri, String database, @Nullable ExoPlayer player) {
        String filePath = getPathFromUri(uri);
        Toast.makeText(context, "Uploading video", Toast.LENGTH_LONG).show();
        Data data = new Data.Builder()
                .putString("filePath", filePath)
                .putString("fileType", "video")
                .build();

        Log.d("here", "uploadVideoInBackground fileType: " + data.getString("fileType"));
        uploadWorkRequest = new OneTimeWorkRequest.Builder(CloudinaryUploadWorker.class)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context).enqueue(uploadWorkRequest);

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(uploadWorkRequest.getId())
                .observe((LifecycleOwner) context, workInfo -> {
                    if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                        // Retrieve the uploaded UR
                        String videoUrl = workInfo.getOutputData().getString("uploadedUrl");

                        Log.d("TAG", "uploadVideoInBackground: Succeeded " + videoUrl);
                        if (videoUrl != null) {
                            Toast.makeText(context, "Video Uploaded Successfully", Toast.LENGTH_SHORT).show();

                            // Other logic (eg. save in the database
                            handleUploadedURL(videoUrl, database);

                            if (player != null) {
                                Log.d("Cloudinary URL", videoUrl);
                                playVideo(videoUrl, player);
                            }
                        } else {
                            Log.d("TAG", "uploadImageInBackground: null url");
                        }
                    } else if (workInfo != null && workInfo.getState() == WorkInfo.State.FAILED) {
                        Toast.makeText(context, "Video Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void uploadPdfInBackground(Uri uri, String database, @Nullable WebView webView) {
        byte[] filePathBytes = getBytesFromUri(uri);
        String filePath = saveBytesToCacheFile(filePathBytes);
        Toast.makeText(context, "Uploading pdf", Toast.LENGTH_LONG).show();
        Data data = new Data.Builder()
                .putString("filePath", filePath)
                .putString("fileType", "pdf")
                .build();

        uploadWorkRequest = new OneTimeWorkRequest.Builder(CloudinaryUploadWorker.class)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context).enqueue(uploadWorkRequest);

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(uploadWorkRequest.getId())
                .observe((LifecycleOwner) context, workInfo -> {
                    if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                        // Retrieve the uploaded UR
                        String pdfUrl = workInfo.getOutputData().getString("uploadedUrl");

                        Log.d("TAG", "uploadImageInBackground: Succeeded " + pdfUrl);
                        if (pdfUrl != null) {
                            Toast.makeText(context, "Video Uploaded Successfully", Toast.LENGTH_SHORT).show();

                            // Other logic (eg. save in the database
                            handleUploadedURL(pdfUrl, database);
                            deleteCacheFile("/data/user/0/com.example.testroom/cache/temp.pdf");
                            if (webView != null){
                                displayPDF(pdfUrl, webView);
                                Log.d("TAG", "uploadPdfInBackground: display pdf");
                            }

                        } else {
                            Log.d("TAG", "uploadImageInBackground: null url");
                        }
                    } else if (workInfo != null && workInfo.getState() == WorkInfo.State.FAILED) {
                        Toast.makeText(context, "Video Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void handleUploadedURL(String imageUrl, String database) {
        Log.d("TAG", "handleUploadedURL: Wow still can work!");
    }

    // Display Image
    public static void displayImage(Context context, String imageUrl, ImageView imageView) {
        Log.d("Cloudinary URL", imageUrl);
        Glide.with(context).load(imageUrl).into(imageView);
    }

    // Display PDF
    public static void displayPDF(String pdfUrl, WebView webView) {
        Log.d("Cloudinary URL", pdfUrl);
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdfUrl);
    }

    public static void playVideo(String videoUrl, ExoPlayer player) {
//        mediaItem = MediaItem.fromUri(Uri.parse(toHTTPS("http://res.cloudinary.com/dmgpozfee/video/upload/v1731385187/cgtajcgfoloqc4calfov.mp4")));
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        player.setMediaItem(mediaItem);

        player.prepare();
        player.setVolume(1.0f);
        player.play();
    }

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

