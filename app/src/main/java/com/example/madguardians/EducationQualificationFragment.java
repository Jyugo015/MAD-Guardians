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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EducationQualificationFragment extends Fragment implements MediaHandler.MediaHandleCallback {
    private Button uploadButton,selectDomainButton;
    private WebView webView;
    private MediaHandler pdfHandler;
    private static final int PICK_PDF_REQUEST = 1;
    private Uri pdfUri;
    private String pdfUrl;  // To store the PDF URL after upload
    private List<String> selectedDomainIds = new ArrayList<>();
    private FirebaseFirestore firestore;
    SharedPreferences sharedPreferences;
    String userId;

    public EducationQualificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_education_qualification, container, false);

        uploadButton = view.findViewById(R.id.upload);
        selectDomainButton = view.findViewById(R.id.selectDomainButton);
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

        // Trigger pdf selection
        uploadButton.setOnClickListener(v -> pdfHandler.selectPDF());
        // Handle the select domain button click
        selectDomainButton.setOnClickListener(v -> showApproveDialog());

        return view;
    }

    /////////////////////////////////////////////////xy//////////////////////////////////////////////
    @Override
    public void onMediaSelected(String filePath, String fileType) {
        // Handle the file path returned by the MediaHandler
        Toast.makeText(getContext(), "Selected " + fileType + " path: " + filePath, Toast.LENGTH_LONG).show();
        if (fileType.equalsIgnoreCase("pdf")) {
            // Upload the PDF and get its URL
            pdfHandler.uploadPdfInBackground(filePath, "YOUR_DATABASE", webView);
        }
    }
    /////////////////////////////////////////////////xy//////////////////////////////////////////////

    // Call this method when the user selects domains and clicks Confirm in the dialog
    public void onDomainSelectionConfirmed(List<String> selectedDomainIds) {
        this.selectedDomainIds = selectedDomainIds;  // Update the selected domain IDs
        if (pdfUri != null) {
            uploadPdfAndSaveData();  // Proceed with saving data if PDF is uploaded
        } else {
            Toast.makeText(getContext(), "Please upload a PDF first.", Toast.LENGTH_SHORT).show();
        }
    }
    /////////////////////////////////////////////////xy//////////////////////////////////////////////
    private void uploadPdfAndSaveData() {
       saveDataToFirestore();
    }
    /////////////////////////////////////////////////xy//////////////////////////////////////////////
    private void saveDataToFirestore() {
        String staffId = null; // Set to null by default

        // Prepare the data to save
        // Prepare the data to save
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("pdfUrl", pdfUrl);
        data.put("domainId", selectedDomainIds);  // Store only domainIds
        data.put("staffId", staffId);
        data.put("timestamp", FieldValue.serverTimestamp());

        // Save the data to Firestore
        firestore.collection("verEducator").add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Data saved successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save data.", Toast.LENGTH_SHORT).show();
                });
    }

    public void showApproveDialog() {
        ApproveEducatorDialogFragment dialog = ApproveEducatorDialogFragment.newInstance();
        dialog.setOnApproveListener(selectedDomains -> {
            // selectedDomains is a List<Domain> passed from the dialog
            onDomainSelectionConfirmed(selectedDomains);
        });
        dialog.show(getParentFragmentManager(), "ApproveEducatorDialogFragment");
    }
}