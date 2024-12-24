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

public abstract class BaseTab2Fragment<T> extends Fragment {
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.staff_fragment_tab2_hzw, container, false);

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView_handlePost_tab2);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(getAdapter(getData()));

        return view;
    }

    // Abstract methods for subclasses to implement
    protected abstract List<T> getData();

    protected abstract RecyclerView.Adapter<?> getAdapter(List<T> data);
}