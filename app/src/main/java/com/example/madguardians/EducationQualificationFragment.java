package com.example.madguardians;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.madguardians.database.CloudinaryUploadWorker;
import com.example.madguardians.database.Domain;
import com.example.madguardians.utilities.MediaHandler;
import com.example.madguardians.utilities.MediasHandler;
import com.example.madguardians.utilities.UploadCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EducationQualificationFragment extends Fragment implements MediaHandler.MediaHandleCallback {
    private Button uploadButton,selectDomainButton, saveChangesButton;
    private WebView webView;
    private MediaHandler pdfHandler;
    private static final int PICK_PDF_REQUEST = 1;
    private Uri pdfUri;
    private List<String> selectedDomainIds = new ArrayList<>();
    private FirebaseFirestore firestore;
    SharedPreferences sharedPreferences;
    String userId;
    private static final String TAG = "EducationQualificationFragment";

    public EducationQualificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_education_qualification, container, false);

        uploadButton = view.findViewById(R.id.upload);
        selectDomainButton = view.findViewById(R.id.selectDomainButton);
        saveChangesButton = view.findViewById(R.id.save_change);
        webView = view.findViewById(R.id.displayPdf);
        firestore = FirebaseFirestore.getInstance();  // Initialize Firestore instance
        // Get SharedPreferences
        sharedPreferences = getContext().getSharedPreferences("user_preferences", MODE_PRIVATE);
        //Get userid
        userId = sharedPreferences.getString("user_id", null);

        ActivityResultLauncher<Intent> pdfPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        pdfUri = result.getData().getData();
                        pdfHandler.handleResult(pdfUri, "pdf");
                    }
                }
        );
        pdfHandler = new MediaHandler(getContext(), pdfPickerLauncher, this);
        updateSelectDomainButtonText();
        // Trigger pdf selection
        uploadButton.setOnClickListener(v -> pdfHandler.selectPDF());
        // Handle the select domain button click
        selectDomainButton.setOnClickListener(v -> showApproveDialog());
        saveChangesButton.setOnClickListener(v-> {uploadPdfAndSaveData();});

        return view;
    }

    /////////////////////////////////////////////////xy//////////////////////////////////////////////
    @Override
    public void onMediaSelected(String filePath, String fileType) {
        // Handle the file path returned by the MediaHandler
        Toast.makeText(getContext(), "Selected " + fileType + " path: " + filePath, Toast.LENGTH_LONG).show();
        if (fileType.equalsIgnoreCase("pdf")) {
            Log.d(TAG, "onMediaSelected: PDF is uploaded");
            uploadButton.setText("Uploaded");
        }
    }
    /////////////////////////////////////////////////xy//////////////////////////////////////////////

    // Call this method when the user selects domains and clicks Confirm in the dialog
    public void onDomainSelectionConfirmed(List<String> selectedDomainIds) {
        if (selectedDomainIds == null || selectedDomainIds.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one domain.", Toast.LENGTH_SHORT).show();
            return;
        }
        this.selectedDomainIds = selectedDomainIds;  // Update the selected domain IDs
        if (pdfUri == null) {
            Toast.makeText(getContext(), "Please upload a PDF first.", Toast.LENGTH_SHORT).show();
        }
    }
    /////////////////////////////////////////////////xy//////////////////////////////////////////////
    private void uploadPdfAndSaveData() {
        // Upload the PDF and get its URL
        if (pdfUri == null) {
            Toast.makeText(getContext(), "Please upload a PDF first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDomainIds == null || selectedDomainIds.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one domain.", Toast.LENGTH_SHORT).show();
            return;
        }
        pdfHandler.uploadPdfInBackground(pdfUri, webView, new UploadCallback<String>() {
            @Override
            public void onSuccess(String pdfUrl) {
                saveDataToFirestore(pdfUrl);  // Save data
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to upload PDF.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /////////////////////////////////////////////////xy//////////////////////////////////////////////
    private void saveDataToFirestore(String mediaId) {
        String staffId = null; // Set to null by default
        String verifiedStatus = null;

        // Fetch the current highest verEducatorId
        firestore.collection("verEducator")
                .orderBy("verEducatorId", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String newVerEducatorId = generateNewId(queryDocumentSnapshots);
                    Log.d(TAG, "Saving data with domainIds: " + selectedDomainIds);

                    // Prepare the data to save
                    Map<String, Object> data = new HashMap<>();
                    data.put("userId", userId);
                    data.put("mediaId", mediaId);
                    data.put("domainId", selectedDomainIds);  // Store only domainIds
                    data.put("staffId", staffId);
                    data.put("timestamp", FieldValue.serverTimestamp());
                    data.put("verifiedStatus", verifiedStatus);
                    data.put("verEducatorId", newVerEducatorId); // Add the new verEducatorId

                    // Save the data to Firestore
                    firestore.collection("verEducator").document(newVerEducatorId)
                            .set(data)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Data saved successfully.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to save data.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch current verEducatorId.", Toast.LENGTH_SHORT).show();
                });
    }

    private String generateNewId(QuerySnapshot queryDocumentSnapshots) {
        String newVerEducatorId = "VE00001"; // Default ID if no records exist
        if (!queryDocumentSnapshots.isEmpty()) {
            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
            String lastId = document.getString("verEducatorId");
            if (lastId != null && lastId.startsWith("VE")) {
                // Increment the numeric part of the last ID
                int currentId = Integer.parseInt(lastId.substring(2));
                newVerEducatorId = String.format("VE%05d", currentId + 1);
            }
        }
        return newVerEducatorId;
    }
    public void showApproveDialog() {
        ApproveEducatorDialogFragment dialog = ApproveEducatorDialogFragment.newInstance(selectedDomainIds);
        dialog.setOnApproveListener(selectedDomains -> {
            // selectedDomains is a List<Domain> passed from the dialog
            selectedDomainIds = selectedDomains;
            updateSelectDomainButtonText();
        });
        dialog.show(getParentFragmentManager(), "ApproveEducatorDialogFragment");
    }

    private void updateSelectDomainButtonText() {
        if (selectedDomainIds.isEmpty()) {
            selectDomainButton.setText("Select Domains");
        } else {
            selectDomainButton.setText("Selected (" + selectedDomainIds.size() + ")");
        }
    }
}