package com.example.madguardians;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.database.Domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApproveEducatorDialogFragment extends DialogFragment {

    private OnConfirmListener listener;

    public static ApproveEducatorDialogFragment newInstance(List<String> selectedDomainIds) {
        ApproveEducatorDialogFragment fragment = new ApproveEducatorDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("selectedDomainIds", new ArrayList<>(selectedDomainIds)); // 传递已选中的 domainId 列表
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnApproveListener(OnConfirmListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the custom dialog layout
        View view = inflater.inflate(R.layout.fragment_approve_educator_dialog, container, false);

        List<String> selectedDomainIds = getArguments() != null ?
                getArguments().getStringArrayList("selectedDomainIds") : new ArrayList<>();


        // Close button
        ImageView closeButton = view.findViewById(R.id.ic_close);
        closeButton.setOnClickListener(v -> dismiss());

        // Title
        TextView title = view.findViewById(R.id.TVApproveTitle);
        title.setText("Approve");

        // Description
        TextView description = view.findViewById(R.id.TVApproveDescr);
        description.setText("The user is pro in the field of ");

        // RecyclerView for documents
        RecyclerView recyclerView = view.findViewById(R.id.document_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Sample document data
        List<Domain> domains = Arrays.asList(
                new Domain("D00001", "Language"),
                new Domain("D00002", "Computer Science"),
                new Domain("D00003", "Physics"),
                new Domain("D00004", "Chemistry"),
                new Domain("D00005", "Biology"),
                new Domain("D00006", "Mathematics"),
                new Domain("D00007", "Music")
        );
        ApproveAdapter adapter = new ApproveAdapter(domains, selectedDomainIds);;
        recyclerView.setAdapter(adapter);

        // Confirm button
        view.findViewById(R.id.button_confirm).setOnClickListener(v -> {
            List<String> selectedDomainIdsToSave = adapter.getSelectedDomainIds();
            if (listener != null) {
                listener.onConfirmed(selectedDomainIdsToSave);  // Pass the domainIds to the listener
            }
            dismiss();  // Dismiss the dialog
        });

        return view;
    }

    private static class ApproveAdapter extends RecyclerView.Adapter<ApproveAdapter.ViewHolder> {

        private final List<Domain> domains;
        private final List<Boolean> selected; // Track which checkboxes are selected

        public ApproveAdapter(List<Domain> domains, List<String> initiallySelectedDomainIds) {
            this.domains = domains;
            this.selected = new ArrayList<>();
            for (Domain domain : domains) {
                selected.add(initiallySelectedDomainIds.contains(domain.getDomainId())); // Initialize all as unselected
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hzw_item_field_checkbox, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Domain domain = domains.get(position);
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setText(domain.getDomainName());
            holder.checkBox.setChecked(selected.get(position));

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                selected.set(position, isChecked);
            });
        }

        @Override
        public int getItemCount() {
            return domains.size();
        }

        public List<String> getSelectedDomainIds() {
            List<String> selectedDomainIds = new ArrayList<>();
            for (int i = 0; i < domains.size(); i++) {
                if (selected.get(i)) {  // Check if it's selected
                    selectedDomainIds.add(domains.get(i).getDomainId());
                }
            }
            return selectedDomainIds;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox checkBox;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                checkBox = itemView.findViewById(R.id.checkbox_field);
            }
        }
    }

    public interface OnConfirmListener {
        void onConfirmed(List<String> selectedDomainIds);  // Change to List<String>
    }
}