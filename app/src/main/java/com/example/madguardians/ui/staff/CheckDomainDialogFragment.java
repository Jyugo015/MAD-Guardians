package com.example.madguardians.ui.staff;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.comment.adapter.Listener;
import com.example.madguardians.database.Domain;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CheckDomainDialogFragment extends DialogFragment {
    private FirebaseFirestore firestore;
    private DomainAdapter adapter;
    private String verEducatorId;
    Listener.OnDomainClickListener listener;

    public static CheckDomainDialogFragment newInstance(String verEducatorId) {
        CheckDomainDialogFragment fragment = new CheckDomainDialogFragment();
        Bundle args = new Bundle();
        args.putString("verEducatorId", verEducatorId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_educator_dialog, container, false);

        firestore = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            verEducatorId = getArguments().getString("verEducatorId");
        }

        TextView title = view.findViewById(R.id.TVApproveTitle);
        title.setText("Domains");

        RecyclerView recyclerView = view.findViewById(R.id.document_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Adapter to display the list of domains
        adapter = new DomainAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        loadDomains();

        return view;
    }
public void setDomainListener(Listener.OnDomainClickListener listener){
        this.listener=listener;
}
//    private void loadDomains() {
//        if (verEducatorId != null) {
//            firestore.collection("verEducator")
//                    .document(verEducatorId)
//                    .collection("domain")
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            List<Domain> domains = new ArrayList<>();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                String domainId = document.getString("domainId");
//                                String domainName = document.getString("domainName");
//                                if (domainId != null && domainName != null) {
//                                    domains.add(new Domain(domainId, domainName));
//                                }
//                            }
//                            adapter.updateDomains(domains);
//                        }
//                    });
//        }
//    }
private void loadDomains() {
    if (verEducatorId != null) {
        firestore.collection("verEducator")
                .document(verEducatorId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Retrieve the domainId array from the document
                        List<String> domainIds = (List<String>) task.getResult().get("domainId");

                        if (domainIds != null && !domainIds.isEmpty()) {
                            List<Domain> domains = new ArrayList<>();
                            for (String domainId : domainIds) {
                                // Fetch additional information for each domain from the "domain" collection
                                firestore.collection("domain")
                                        .document(domainId)
                                        .get()
                                        .addOnCompleteListener(domainTask -> {
                                            if (domainTask.isSuccessful() && domainTask.getResult() != null) {
                                                String domainName = domainTask.getResult().getString("domainName");
                                                if (domainId != null && domainName != null) {
                                                    domains.add(new Domain(domainId, domainName));
                                                }

                                                // Update adapter after processing all domains
                                                if (domains.size() == domainIds.size()) {
                                                    adapter.updateDomains(domains);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}


    public static class DomainAdapter extends RecyclerView.Adapter<DomainAdapter.ViewHolder> {

        private final List<Domain> domains;

        public DomainAdapter(List<Domain> domains) {
            this.domains = domains;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hzw_item_document, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Domain domain = domains.get(position);
            holder.textView.setText(domain.getDomainName()); // Display domain name
        }

        @Override
        public int getItemCount() {
            return domains.size();
        }

        public void updateDomains(List<Domain> newDomains) {
            domains.clear();
            domains.addAll(newDomains);
            notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.document_name);
            }
        }
    }
}

//package com.example.madguardians.ui.staff;
//
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.DialogFragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.madguardians.R;
//import com.example.madguardians.database.Domain;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class CheckDomainDialogFragment extends DialogFragment {
//
//    private OnConfirmListener listener;
//
//    public static CheckDomainDialogFragment newInstance(List<String> selectedDomainIds) {
//        CheckDomainDialogFragment fragment = new CheckDomainDialogFragment();
//        Bundle args = new Bundle();
//        args.putStringArrayList("selectedDomainIds", new ArrayList<>(selectedDomainIds)); // 传递已选中的 domainId 列表
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    public void setOnApproveListener(OnConfirmListener listener) {
//        this.listener = listener;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        // Inflate the custom dialog layout
//        View view = inflater.inflate(R.layout.fragment_approve_educator_dialog, container, false);
//
//        List<String> selectedDomainIds = getArguments() != null ?
//                getArguments().getStringArrayList("selectedDomainIds") : new ArrayList<>();
//
//
//        // Close button
//        ImageView closeButton = view.findViewById(R.id.ic_close);
//        closeButton.setOnClickListener(v -> dismiss());
//
//        // Title
//        TextView title = view.findViewById(R.id.TVApproveTitle);
//        title.setText("Approve");
//
//        // Description
//        TextView description = view.findViewById(R.id.TVApproveDescr);
//        description.setText("The user is pro in the field of ");
//
//        // RecyclerView for documents
//        RecyclerView recyclerView = view.findViewById(R.id.document_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//        // Sample document data
//        List<Domain> domains = Arrays.asList(
//                new Domain("D00001", "Language"),
//                new Domain("D00002", "Computer Science"),
//                new Domain("D00003", "Physics"),
//                new Domain("D00004", "Chemistry"),
//                new Domain("D00005", "Biology"),
//                new Domain("D00006", "Mathematics"),
//                new Domain("D00007", "Music")
//        );
//        ApproveAdapter adapter = new ApproveAdapter(domains, selectedDomainIds);;
//        recyclerView.setAdapter(adapter);
//
//        // Confirm button
//        view.findViewById(R.id.button_confirm).setOnClickListener(v -> {
//            List<String> selectedDomainIdsToSave = adapter.getSelectedDomainIds();
//            if (listener != null) {
//                listener.onConfirmed(selectedDomainIdsToSave);  // Pass the domainIds to the listener
//            }
//            dismiss();  // Dismiss the dialog
//        });
//
//        return view;
//    }
//
//    private static class ApproveAdapter extends RecyclerView.Adapter<ApproveAdapter.ViewHolder> {
//
//        private final List<Domain> domains;
//        private final List<Boolean> selected; // Track which checkboxes are selected
//
//        public ApproveAdapter(List<Domain> domains, List<String> initiallySelectedDomainIds) {
//            this.domains = domains;
//            this.selected = new ArrayList<>();
//            for (Domain domain : domains) {
//                selected.add(initiallySelectedDomainIds.contains(domain.getDomainId())); // Initialize all as unselected
//            }
//        }
//
//        @NonNull
//        @Override
//        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.hzw_item_field_checkbox, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//            Domain domain = domains.get(position);
//            holder.checkBox.setOnCheckedChangeListener(null);
//            holder.checkBox.setText(domain.getDomainName());
//            holder.checkBox.setChecked(selected.get(position));
//
//            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                selected.set(position, isChecked);
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return domains.size();
//        }
//
//        public List<String> getSelectedDomainIds() {
//            List<String> selectedDomainIds = new ArrayList<>();
//            for (int i = 0; i < domains.size(); i++) {
//                if (selected.get(i)) {  // Check if it's selected
//                    selectedDomainIds.add(domains.get(i).getDomainId());
//                }
//            }
//            return selectedDomainIds;
//        }
//
//        public static class ViewHolder extends RecyclerView.ViewHolder {
//            CheckBox checkBox;
//
//            public ViewHolder(@NonNull View itemView) {
//                super(itemView);
//                checkBox = itemView.findViewById(R.id.checkbox_field);
//            }
//        }
//    }
//
//    public interface OnConfirmListener {
//        void onConfirmed(List<String> selectedDomainIds);  // Change to List<String>
//    }
//}
