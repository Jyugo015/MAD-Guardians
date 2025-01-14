package com.example.madguardians.ui.course;

import static android.content.Context.MODE_PRIVATE;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.madguardians.R;
import com.example.madguardians.comment.adapter.Listener;
import com.example.madguardians.database.Comments;
import com.example.madguardians.firebase.MediaFB;
import com.example.madguardians.firebase.PostFB;
import com.example.madguardians.utilities.AdapterCourse;
import com.example.madguardians.utilities.FirebaseController;
import com.example.madguardians.utilities.MediaHandler;
import com.example.madguardians.utilities.UploadCallback;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment implements Listener.onHelpdeskListener{

    private PostFB post;
    private View view;
    private static int NUMBER_OF_SEGMENT;
    private int lastViewId;
    FirebaseFirestore firestore;
    SharedPreferences sharedPreferences;
    String userId;
    String staffId;

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getContext().getSharedPreferences("user_preferences", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);
        //staff -zw
        sharedPreferences = getContext().getSharedPreferences("staff_preferences", MODE_PRIVATE);
        staffId = sharedPreferences.getString("staff_id", null);
        //////
        firestore = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            PostFB.getPost(getArguments().getString("postId"), new UploadCallback<PostFB>(){
                @Override
                public void onSuccess(PostFB result) {
                    post = result;
                    setView();
                }
                @Override
                public void onFailure(Exception e) {
                    Log.e("TAG", "onFailure: ", e);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post, container, false);
        setView();
        NUMBER_OF_SEGMENT = 0;
        lastViewId = 0;
        return view;
    }

    private void setView() {
        TextView TVTitle = view.findViewById(R.id.TVTitle);
        TextView TVDescription = view.findViewById(R.id.TVDescription);
        Button BTNComment = view.findViewById(R.id.BTNComment);
        Button BTNReport = view.findViewById(R.id.BTNReport);
        if (post != null) {
            TVTitle.setText(post.getTitle());
            TVDescription.setText(post.getDescription());
            ConstraintLayout CLContainer = view.findViewById(R.id.CLContainer);
            ArrayList<MediaFB> imgMedias = new ArrayList<>();
            ArrayList<MediaFB> vidMedias = new ArrayList<>();
            ArrayList<MediaFB> pdfMedias = new ArrayList<>();
            if (post.getImageSetId() != null) {
                MediaFB.getMedias(post.getImageSetId(), new UploadCallback<List<MediaFB>>() {
                    @Override
                    public void onSuccess(List<MediaFB> result) {
                        imgMedias.clear();
                        imgMedias.addAll(result);
                        for (MediaFB imgMedia : imgMedias) {
                            displayMediaSegment(CLContainer, imgMedia, FirebaseController.IMAGE);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("TAG", "onFailureImage: ", e);
                    }
                });
            }
            if (post.getFileSetId() != null) {
                MediaFB.getMedias(post.getFileSetId(), new UploadCallback<List<MediaFB>>() {
                    @Override
                    public void onSuccess(List<MediaFB> result) {
                        pdfMedias.clear();
                        pdfMedias.addAll(result);
                        for (MediaFB pdfMedia : pdfMedias) {
                            displayMediaSegment(CLContainer, pdfMedia, FirebaseController.PDF);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("TAG", "onFailurePDF: ", e);
                    }
                });
            }
            if (post.getVideoSetId() != null) {
                MediaFB.getMedias(post.getVideoSetId(), new UploadCallback<List<MediaFB>>() {
                    @Override
                    public void onSuccess(List<MediaFB> result) {
                        vidMedias.clear();
                        vidMedias.addAll(result);
                        for (MediaFB vidMedia : vidMedias) {
                            displayMediaSegment(CLContainer, vidMedia, FirebaseController.VIDEO);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("TAG", "onFailureVideo: ", e);
                    }
                });
            }
        } else {
            Log.d("Post fragment", "onCreateView: Post is null");
        }

        BTNComment.setOnClickListener(v -> {
            connectToComment();
        });
        BTNReport.setOnClickListener(v -> {
//            reportPost(post.getPostId());
            onReport(post);
        });
    }

    private void displayMediaSegment(ConstraintLayout clContainer, MediaFB media, String typeMedia) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View mediaSegment = layoutInflater.inflate(R.layout.segment_media_option, clContainer, false);
        mediaSegment.setId(View.generateViewId());
        ImageView IVImage = mediaSegment.findViewById(R.id.IVImage);
        TextView TVDesc = mediaSegment.findViewById(R.id.TVDesc);
        Button BTNStart = mediaSegment.findViewById(R.id.BTNStart);
        Button BTNReport = mediaSegment.findViewById(R.id.BTNReport);

        // set text in the button
        isRead(media, isRead -> {
            if (isRead) {
                BTNStart.setText("Completed");
            } else {
                BTNStart.setText("Start!");
            }
        });

        if (typeMedia.equalsIgnoreCase("image")){
            IVImage.setImageResource(R.drawable.ic_image);
            TVDesc.setText(R.string.image_desc);
        }
        else if (typeMedia.equalsIgnoreCase("video")){
            IVImage.setImageResource(R.drawable.ic_video);
            TVDesc.setText(R.string.video_desc);
        }
        else if (typeMedia.equalsIgnoreCase("pdf")){
            IVImage.setImageResource(R.drawable.ic_pdf);
            TVDesc.setText(R.string.pdf_desc);
        }

        BTNStart.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("mediaId", media.getMediaId());
            handleReadStatus(media);

            if (typeMedia.equalsIgnoreCase("image"))
                Navigation.findNavController(v).navigate(R.id.nav_img, bundle);
            else if (typeMedia.equalsIgnoreCase("video"))
                Navigation.findNavController(v).navigate(R.id.nav_vid, bundle);
            else if (typeMedia.equalsIgnoreCase("pdf")) {
                Navigation.findNavController(v).navigate(R.id.nav_pdf, bundle);
            }
        });

        clContainer.addView(mediaSegment);

        // Set constrain set for proper alignment
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(clContainer);

        if (NUMBER_OF_SEGMENT == 0) {
            // If it is the first element, attach it to the parent layout
            constraintSet.connect(mediaSegment.getId(), ConstraintSet.TOP, clContainer.getId(), ConstraintSet.TOP, 16);
        } else {
            // otherwise attach to previous one
            constraintSet.connect(mediaSegment.getId(), ConstraintSet.TOP, lastViewId, ConstraintSet.BOTTOM,16);
        }

        constraintSet.applyTo(clContainer);
        lastViewId = mediaSegment.getId();
        NUMBER_OF_SEGMENT++;

        ////////////////////////////////////////////////////////////////////////////////
        BTNReport.setOnClickListener(v -> {
//            reportMedia(media.getMediaId());
            onReport(post);
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // zw

    public void onReport(PostFB postFB) {
        //report post or media(pdf/video/image) if there is any issue
        ReportDialogFragment reportDialogFragment=ReportDialogFragment.newReport(postFB);
        reportDialogFragment.show(getParentFragmentManager(), "ReportDialogFragment");
        reportDialogFragment.setHelpedeskListener(PostFragment.this);
    }

    private void reportPost(String postId) {
        // Call generateNextHelpdeskId to fetch the next available ID
        generateNextHelpdeskId("helpdesk", new OnIdGeneratedListener() {
            @Override
            public void onIdGenerated(String helpdeskId) {
                if (helpdeskId != null) {
                    // Once the ID is generated, proceed with creating the helpdesk data
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                    // Create a HashMap to store data
                    Map<String, Object> helpdeskData = new HashMap<>();
                    helpdeskData.put("commentId", null); // Left as null
                    helpdeskData.put("helpdeskId", helpdeskId);
                    helpdeskData.put("helpdeskStatus", "pending");
                    helpdeskData.put("issueId", null); // what should put?
                    helpdeskData.put("reason", null); // Left as null
                    helpdeskData.put("reportedItemId", postId);
                    helpdeskData.put("staffId", null); // Left as null
                    helpdeskData.put("timestamp", new Timestamp(new Date())); // Current timestamp
                    helpdeskData.put("userId", userId); // Left as null

                    // Add the data to Firestore
                    firestore.collection("helpdesk")
                            .document(helpdeskId)
                            .set(helpdeskData)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("reportPost", "Post reported successfully!");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("reportPost", "Error reporting post", e);
                            });
                } else {
                    Log.e("reportPost", "Failed to generate helpdesk ID");
                }
            }
        });
    }

    private void reportMedia(String mediaId) {
        // Call generateNextHelpdeskId to fetch the next available ID
        generateNextHelpdeskId("helpdesk", new OnIdGeneratedListener() {
            @Override
            public void onIdGenerated(String helpdeskId) {
                if (helpdeskId != null) {
                    // Once the ID is generated, proceed with creating the helpdesk data
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                    // Create a HashMap to store data
                    Map<String, Object> helpdeskData = new HashMap<>();
                    helpdeskData.put("commentId", null); // Left as null
                    helpdeskData.put("helpdeskId", helpdeskId);
                    helpdeskData.put("helpdeskStatus", "pending");
                    helpdeskData.put("issueId", null); // Left as null
                    helpdeskData.put("reason", null); // Left as null
                    helpdeskData.put("reportedItemId", mediaId);
                    helpdeskData.put("staffId", null); // Left as null
                    helpdeskData.put("timestamp", new Timestamp(new Date())); // Current timestamp
                    helpdeskData.put("userId", null); // Left as null

                    // Add the data to Firestore
                    firestore.collection("helpdesk")
                            .document(helpdeskId)
                            .set(helpdeskData)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("reportMedia", "Media reported successfully!");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("reportMedia", "Error reporting media", e);
                            });
                } else {
                    Log.e("reportMedia", "Failed to generate helpdesk ID");
                }
            }
        });
    }

    public void generateNextHelpdeskId(String tableName, OnIdGeneratedListener callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(tableName)
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String newHelpdeskId = "H00000"; // Default ID if no documents exist
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String lastDocumentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        // Extract numeric part and increment
                        int numericPart = Integer.parseInt(lastDocumentId.substring(1));
                        numericPart++;
                        newHelpdeskId = String.format("H%05d", numericPart); // Format with leading zeros
                    }
                    callback.onIdGenerated(newHelpdeskId);
                })
                .addOnFailureListener(e -> {
                    Log.e("generateNextHelpdeskId", "Error fetching last document ID", e);
                    callback.onIdGenerated(null); // Notify failure
                });
    }

    @Override
    public void helpdeskAdded() {
        Toast.makeText(getContext(), "Post/Media Reported", Toast.LENGTH_SHORT).show();
    }

    // Callback Interface
    public interface OnIdGeneratedListener {
        void onIdGenerated(String newId);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // yewoon
    private void getCourseIdByPostId(String postId, FirestoreCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // check post1 == postId
        firestore.collection("course")
                .whereEqualTo("post1", postId)
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task1.getResult()) {
                            String courseId = document.getString("courseId");
                            callback.onSuccess(courseId);
                            return;
                        }
                    }

                    // check post2 == postId
                    firestore.collection("course")
                            .whereEqualTo("post2", postId)
                            .get()
                            .addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful() && !task2.getResult().isEmpty()) {
                                    for (QueryDocumentSnapshot document : task2.getResult()) {
                                        String courseId = document.getString("courseId");
                                        callback.onSuccess(courseId);
                                        return;
                                    }
                                }

                                // check post3 == postId
                                firestore.collection("course")
                                        .whereEqualTo("post3", postId)
                                        .get()
                                        .addOnCompleteListener(task3 -> {
                                            if (task3.isSuccessful() && !task3.getResult().isEmpty()) {
                                                for (QueryDocumentSnapshot document : task3.getResult()) {
                                                    String courseId = document.getString("courseId");
                                                    callback.onSuccess(courseId);
                                                    return;
                                                }
                                            }

                                            // if not result
                                            Log.e("Firestore", "Course not found for postId: " + postId);
                                            callback.onFailure(new Exception("Course not found"));
                                        });
                            });
                });
    }

    public interface FirestoreCallback {
        void onSuccess(String courseId);
        void onFailure(Exception e);
    }

    private void checkAndUpdateReadStatus(MediaFB pdfMedia, FirestoreCallback callback) {
        String postId = post.getPostId(); // Get PostId

        getCourseIdByPostId(postId, new FirestoreCallback() {
            @Override
            public void onSuccess(String courseId) {
                // check userHistory
                firestore.collection("userHistory")
                        .whereEqualTo("userId", userId)
                        .whereEqualTo("courseId", courseId)
                        .whereEqualTo("mediaId", pdfMedia.getMediaId())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    // Have record，update time
                                    for (DocumentSnapshot document : task.getResult()) {
                                        firestore.collection("userHistory")
                                                .document(document.getId())
                                                .update("timestamp", System.currentTimeMillis())
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("Firestore", "Timestamp updated for mediaId: " + pdfMedia.getMediaId());
                                                    callback.onSuccess("updated"); // Update
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("Firestore", "Error updating timestamp: ", e);
                                                    callback.onFailure(e);
                                                });
                                    }
                                } else {
                                    // no record，create new record
                                    Map<String, Object> history = new HashMap<>();
                                    history.put("userId", userId);
                                    history.put("courseId", courseId);
                                    history.put("mediaId", pdfMedia.getMediaId());
                                    history.put("timestamp", System.currentTimeMillis());

                                    firestore.collection("userHistory")
                                            .add(history)
                                            .addOnSuccessListener(documentReference -> {
                                                Log.d("Firestore", "New record created for mediaId: " + pdfMedia.getMediaId());
                                                callback.onSuccess("created"); // New Record created
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("Firestore", "Error creating record: ", e);
                                                callback.onFailure(e);
                                            });
                                }
                            } else {
                                Log.e("Firestore", "Error fetching user history: ", task.getException());
                                callback.onFailure(task.getException());
                            }
                        });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "Error fetching courseId for checkAndUpdateReadStatus", e);
                callback.onFailure(e);
            }
        });
    }
    private void isRead(MediaFB pdfMedia, ReadStatusCallback callback) {
        String postId = post.getPostId(); // Get PostId

        getCourseIdByPostId(postId, new FirestoreCallback() {
            @Override
            public void onSuccess(String courseId) {
                firestore.collection("userHistory")
                        .whereEqualTo("userId", userId)
                        .whereEqualTo("courseId", courseId)
                        .whereEqualTo("mediaId", pdfMedia.getMediaId())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                // have record, isRead
                                callback.onResult(true);
                            } else {
                                // No record, unRead
                                callback.onResult(false);
                            }
                        });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "Error fetching courseId for isRead check", e);
                callback.onResult(false); // Fail to search userHistory
            }
        });
    }
    public interface ReadStatusCallback {
        void onResult(boolean isRead);
    }

    private void handleReadStatus(MediaFB pdfMedia) {
        checkAndUpdateReadStatus(pdfMedia, new FirestoreCallback() {
            @Override
            public void onSuccess(String result) {
                if ("updated".equals(result)) {
                    Log.d("ReadStatus", "Media record updated.");
                } else if ("created".equals(result)) {
                    Log.d("ReadStatus", "New media record created.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ReadStatus", "Error handling read status: ", e);
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // jiaqi
    private void connectToComment() {
        //add staff view - zw
        if(staffId!=null){
            System.out.println("userId:"+userId);
            System.out.println("staffId:"+staffId);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.NavHostsFragmentStaff);
            Bundle bundle = new Bundle();
            bundle.putSerializable("post", post);
            navController.navigate(R.id.nav_user_comment, bundle);
        }else{
            NavController navController = Navigation.findNavController(requireActivity(), R.id.NavHostFragment);
            Bundle bundle = new Bundle();
            bundle.putSerializable("post", post);
            navController.navigate(R.id.nav_user_comment, bundle);
        }
    }

}