package com.example.madguardians.ui.course;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import com.example.madguardians.R;
import com.example.madguardians.firebase.MediaFB;
import com.example.madguardians.utilities.MediaHandler;
import com.example.madguardians.utilities.UploadCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PdfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PdfFragment extends Fragment {

    private MediaFB pdf;
    private View view;
    public PdfFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PdfFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PdfFragment newInstance(String param1, String param2) {
        PdfFragment fragment = new PdfFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            MediaFB.getMedia(getArguments().getString("mediaId"), new UploadCallback<MediaFB>() {
                @Override
                public void onSuccess(MediaFB result) {
                    pdf = result;
                    WebView webView = view.findViewById(R.id.webView);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setSupportZoom(true);
                    webView.setWebViewClient(new WebViewClient());
                    MediaHandler.displayPDF(pdf.getUrl(), webView);
                }
                @Override
                public void onFailure(Exception e) {
                    Log.e("TAG", "onFailure: ", e);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pdf, container, false);
        return view;
    }
}