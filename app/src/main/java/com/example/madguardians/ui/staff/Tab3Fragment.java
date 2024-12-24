//package com.example.madguardians.ui.staff;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.example.madguardians.R;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class Tab3Fragment<T extends DisplayableItem> extends Fragment {
//    private RecyclerView recyclerView;
//    private RecyclerView.Adapter mAdapter;
//    private RecyclerView.LayoutManager layoutManager;
//
//    private List<T> itemList = new ArrayList<>();
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_tab3, container, false);
//
//        fillItemList();
//
//        recyclerView = view.findViewById(R.id.recyclerView_handlePost_tab3);
//        layoutManager = new LinearLayoutManager(requireContext());
//        recyclerView.setLayoutManager(layoutManager);
//
//        mAdapter = new RecycleViewAdapter<>(itemList, requireContext());
//        recyclerView.setAdapter(mAdapter);
//
//        return view;
//    }
//
//    private void fillItemList() {
//        itemList = new ArrayList<>(); // Initialize the list
//
//        // For demonstration, casting items dynamically
//        // Replace with appropriate item additions based on your use case
//        itemList.add((T) new Post("image1", "PostTitle1", "AuthorName1", "Date1", "Pending"));
//        itemList.add((T) new Post("image1", "PostTitle1", "AuthorName1", "Date1", "Pending"));
//        itemList.add((T) new Post("image1", "PostTitle1", "AuthorName1", "Date1", "Pending"));
//        itemList.add((T) new Post("image1", "PostTitle1", "AuthorName1", "Reason1", "Pending"));
//    }
//}