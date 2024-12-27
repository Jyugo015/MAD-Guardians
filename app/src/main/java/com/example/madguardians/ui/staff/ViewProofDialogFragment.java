package com.example.madguardians.ui.staff;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;

import java.util.Arrays;
import java.util.List;

public class ViewProofDialogFragment extends DialogFragment {

    public static ViewProofDialogFragment newInstance() {
        return new ViewProofDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the custom dialog layout
        View view = inflater.inflate(R.layout.staff_pop_up_view_proof_hzw, container, false);

        // Close button
        ImageView closeButton = view.findViewById(R.id.ic_close);
        closeButton.setOnClickListener(v -> dismiss());

        // Title
        TextView title = view.findViewById(R.id.TVViewProofTitle);
        title.setText("View proof");

        // Description
        TextView description = view.findViewById(R.id.TVViewProofDescr);
        description.setText("View the document user uploaded");

        // RecyclerView for documents
        RecyclerView recyclerView = view.findViewById(R.id.document_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Sample document data
        List<String> documents = Arrays.asList("Document 1", "Document 2", "Document 3");
        DocumentAdapter adapter = new DocumentAdapter(documents);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private static class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {
        private final List<String> documents;

        DocumentAdapter(List<String> documents) {
            this.documents = documents;
        }

        @NonNull
        @Override
        public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hzw_item_document, parent, false);
            return new DocumentViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
            holder.documentName.setText(documents.get(position));
        }

        @Override
        public int getItemCount() {
            return documents.size();
        }

        static class DocumentViewHolder extends RecyclerView.ViewHolder {
            TextView documentName;

            DocumentViewHolder(View itemView) {
                super(itemView);
                documentName = itemView.findViewById(R.id.document_name);
            }
        }
    }
}
