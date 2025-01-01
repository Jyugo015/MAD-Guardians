package com.example.madguardians;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
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
import com.example.madguardians.utilities.MediaHandler;

public class EducationQualificationFragment extends Fragment implements MediaHandler.MediaHandleCallback {
    private Button uploadButton;
    public WebView webView;
    private MediaHandler pdfHandler;
    private static final int PICK_PDF_REQUEST = 1;


    public EducationQualificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /// Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_education_qualification, container, false);

        uploadButton = view.findViewById(R.id.upload);
        webView = view.findViewById(R.id.displayPdf);
        ActivityResultLauncher<Intent> pdfPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri pdfUri = result.getData().getData();
                        pdfHandler.handleResult(pdfUri, "pdf");
                    }
                }
        );
        pdfHandler = new MediaHandler(getContext(), pdfPickerLauncher, this);
        // Trigger pdf selection
        uploadButton.setOnClickListener(v -> pdfHandler.selectPDF());



        // Set up the upload button to open the file picker
//        uploadButton.setOnClickListener(v -> openFilePicker());

        return view;
    }



    @Override
    public void onMediaSelected(String filePath, String fileType) {
        // Handle the file path returned by the MediaHandler
        Toast.makeText(getContext(), "Selected " + fileType + " path: " + filePath, Toast.LENGTH_LONG).show();
        if (fileType.equalsIgnoreCase("pdf")){
            pdfHandler.uploadPdfInBackground(filePath, "YOUR_DATABASE", webView);
        }
    }
//    @Override
//    public void onApprovedClicked(HandleEducator educator, int position) {
//        ApproveEducatorDialogFragment dialog = ApproveEducatorDialogFragment.newInstance();
//        dialog.show(getParentFragmentManager(), "ApproveEducatorDialogFragment");
//
////        Toast.makeText(requireContext(), "Approved: " + educator.getEducatorName(), Toast.LENGTH_SHORT).show();
//
//        dialog.setOnApproveListener(selectedFields -> {
//            // Handle the selected fields directly
//            Log.d("Selected Fields", "User selected: " + selectedFields);
//
//            // Example: Save data here or update UI
//            saveApprovedFields(educator, selectedFields);
//
//            Toast.makeText(requireContext(), "Approved: " + educator.getEducatorName(), Toast.LENGTH_SHORT).show();
//});
//
//}
}