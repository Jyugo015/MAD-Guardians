package com.example.madguardians.ui.course;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.madguardians.R;
import com.example.madguardians.firebase.DomainFB;
import com.example.madguardians.firebase.FolderFB;
import com.example.madguardians.utilities.UploadCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadDomainsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadDomainsFragment extends Fragment {

    private View view;
    private LayoutInflater inflater;
    private ViewGroup container;
    private String userId;
    private ArrayList<FolderFB> folderVerified = new ArrayList<>();
    public UploadDomainsFragment() {
        // Required empty public constructor
    }

    public static UploadDomainsFragment newInstance(String param1, String param2) {
        UploadDomainsFragment fragment = new UploadDomainsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        //////////////////////////////////////////////////////////////////////////////////
        userId = getUserId();
        FolderFB.getFolders(userId, new UploadCallback<List<FolderFB>>() {
            @Override
            public void onSuccess(List<FolderFB> result) {
                folderVerified.clear();
                folderVerified.addAll(result);
                setView();
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("TAG", "onFailure: ", e);
            }
        });
    }

    private String getUserId() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_id", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        this.container = container;
        view = inflater.inflate(R.layout.fragment_upload_domains, container, false);
        setView();
        return view;
    }

    private void setView() {
        if (!folderVerified.isEmpty()) {
            for (FolderFB folder : folderVerified) {
                String domainId = folder.getDomainId();
                View folderView = inflater.inflate(R.layout.segment_folder, container, false);
                GridLayout GLFolders = view.findViewById(R.id.GLFolders);
                TextView TVDomain = folderView.findViewById(R.id.TVDomain);
                DomainFB.getDomain(domainId, new UploadCallback<DomainFB>() {
                    @Override
                    public void onSuccess(DomainFB domain) {TVDomain.setText(domain.getDomainName());}
                    @Override
                    public void onFailure(Exception e) {Log.e("TAG", "onFailure: ", e);}
                });

                GLFolders.addView(folderView);
                folderView.setOnClickListener(v-> {
                    Bundle bundle = new Bundle();
                    bundle.putString("domainId", domainId);
                    bundle.putString("folderId", folder.getFolderId());
                    Navigation.findNavController(view).navigate(R.id.nav_domain, bundle);
                });
            }
        }
    }

}