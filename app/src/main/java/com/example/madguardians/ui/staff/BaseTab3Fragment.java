package com.example.madguardians.ui.staff;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;

import java.util.List;

public abstract class BaseTab3Fragment<T> extends Fragment {
    protected RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.staff_fragment_tab3_hzw, container, false);

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView_handlePost_tab3);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

//        fetchData(); // Fetch data when the view is created
        return view;
    }

    // Abstract method to fetch data
    protected abstract void fetchData();

    // Abstract method to set the adapter
    protected abstract void updateRecyclerViewAdapter(List<T> data);
}