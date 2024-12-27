package com.example.madguardians.ui.course;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.madguardians.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadDomainsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadDomainsFragment extends Fragment {

    private String userId;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_domains, container, false);
        GridLayout GLFolders = view.findViewById(R.id.GLFolders);
        for (Domain domain : Domain.getDomains()) {
            View folderView = inflater.inflate(R.layout.segment_folder, container, false);
            TextView TVDomain = folderView.findViewById(R.id.TVDomain);
            TVDomain.setText(domain.getDomainName());
            GLFolders.addView(folderView);
            folderView.setOnClickListener(v-> {
                Bundle bundle = new Bundle();
                bundle.putString("domainId", domain.getDomainId());
                Navigation.findNavController(view).navigate(R.id.nav_domain, bundle);
            });
        }
        return view;
    }
}