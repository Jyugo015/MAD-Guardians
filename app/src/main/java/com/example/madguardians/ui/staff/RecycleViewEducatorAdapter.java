package com.example.madguardians.ui.staff;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madguardians.ApproveEducatorDialogFragment;
import com.example.madguardians.R;
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Media;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;
import com.example.madguardians.ui.staff.VerEducator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecycleViewEducatorAdapter extends RecyclerView.Adapter<RecycleViewEducatorAdapter.PostViewHolder> {
    private List<VerEducator> educatorList;
    private Context context;
    private OnHandleEducatorActionListener onHandleEducatorActionListener;
    private final FirebaseFirestore firestore;
    private final CollectionReference userRef;
    private List<String> selectedDomainIds = new ArrayList<>();
    // Constructor
    public RecycleViewEducatorAdapter(List<VerEducator> verEducatorList, Context context, OnHandleEducatorActionListener onHandleEducatorActionListener) {
        this.educatorList = verEducatorList!=null?verEducatorList:new ArrayList<>();
        this.context = context;
        this.onHandleEducatorActionListener = onHandleEducatorActionListener;

        this.firestore = FirebaseFirestore.getInstance();
        this.userRef = firestore.collection("user");
    }
    public void updateData(List<VerEducator> newData) {
        this.educatorList = newData; // Assuming postList is the list in your adapter
        notifyDataSetChanged(); // Notify the adapter about data changes
    }

    @Override
    public int getItemViewType(int position) {
        VerEducator verEducator = educatorList.get(position);

        if ("pending".equals(verEducator.getVerifiedStatus())) {
            return R.layout.staff_one_line_handle_educator_pending_hzw;
        } else {
            return R.layout.staff_one_line_handle_educator_completed_hzw;
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        VerEducator verEducator = educatorList.get(position);

        // Default placeholder values
        holder.ivPost.setImageResource(R.drawable.hzw_ic_profile);
        holder.tvEducatorName.setText("Loading...");
        holder.tvStatus.setText(verEducator.getVerifiedStatus() != null ? verEducator.getVerifiedStatus() : "Unknown");

        // Check if timestamp is not null and format it
        if (verEducator.getTimestamp() != null) {
            // Convert Timestamp to Date and format as String
            Date date = verEducator.getTimestamp().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(date);
            holder.tvDate.setText(formattedDate);
        } else {
            holder.tvDate.setText("No Date");
        }

        // Fetch user details
        userRef.document(verEducator.getUserId()).get()
                .addOnSuccessListener(userSnapshot -> {
                    if (userSnapshot.exists()) {
                        User user = userSnapshot.toObject(User.class);
                        if (user != null) {
                            holder.tvEducatorName.setText(user.getName() != null ? user.getName() : "Unknown author");
                            if (user.getProfilePic() != null) {
                                Glide.with(context).load(user.getProfilePic()).into(holder.ivPost);
                            } else{
                                holder.ivPost.setImageResource(R.drawable.hzw_ic_profile);
                            }
                        }
                    }
                });
        // Handle actions based on verifiedStatus
        String verifiedStatus = verEducator.getVerifiedStatus();
        if ("pending".equals(verifiedStatus)) {

            holder.btnReject.setOnClickListener(v -> {
                if (onHandleEducatorActionListener != null) {
                    onHandleEducatorActionListener.onRejectClicked(verEducator, position);
                    firestore.collection("verPost")
                            .document(verEducator.getVerEducatorId())
                            .update("verifiedStatus", "rejected");
                    notifyItemChanged(position);
                }
            });

            holder.btnApprove.setOnClickListener(v -> {
                if (onHandleEducatorActionListener != null) {
                    onHandleEducatorActionListener.onApprovedClicked(verEducator, position);
                    firestore.collection("verPost")
                            .document(verEducator.getVerEducatorId())
                            .update("verifiedStatus", "approved");

                    notifyItemChanged(position);
                }
            });
            holder.tVDomain.setOnClickListener(v -> {
                if (onHandleEducatorActionListener != null) {
                    onHandleEducatorActionListener.onDomainClicked(verEducator, position);
                }

                CheckDomainDialogFragment dialog = CheckDomainDialogFragment.newInstance(verEducator.getVerEducatorId());

//                if (context instanceof AppCompatActivity) {
                    Log.w("tvdomain","got run");
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    dialog.show(fragmentManager, "CheckDomainDialogFragment");
//                } else {
                    Log.e("DialogError", "Context is not an instance of AppCompatActivity");
//                }
            });

        } else {

            holder.btnDelete.setOnClickListener(v -> {
                if (onHandleEducatorActionListener != null) {
                    System.out.println("Delete button clicked for position: ");
                    onHandleEducatorActionListener.onDeleteClicked(verEducator, position);
                }
            });
        }
        holder.tvViewProof.setOnClickListener(v->{
            if (onHandleEducatorActionListener != null) {
                onHandleEducatorActionListener.onViewProofClicked(verEducator, position);
                String message = "View proof clicked.";
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                System.out.println(message);
            }
        });
        holder.tvEducatorName.setOnClickListener(v->{
            if(onHandleEducatorActionListener!=null){
                onHandleEducatorActionListener.onEducatorNameClicked(verEducator,position);
                String message = "Educator name clicked.";
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                System.out.println(message);
            }
        });
        holder.tvViewProof.setOnClickListener(v -> navigateToFragment(v, "pdf", verEducator.getMediaId()));
//        holder.tVDomain.setOnClickListener();
    }
//    public void showApproveDialog(v->showApproveDialog()) {
//        ApproveEducatorDialogFragment dialog = ApproveEducatorDialogFragment.newInstance(selectedDomainIds);
//        dialog.setOnApproveListener(selectedDomains -> {
//            // selectedDomains is a List<Domain> passed from the dialog
//            selectedDomainIds = selectedDomains;
//            updateSelectDomainButtonText();
//        });
//        dialog.show(getParentFragmentManager(), "ApproveEducatorDialogFragment");
//    }
//    private void updateSelectDomainButtonText() {
//        if (selectedDomainIds.isEmpty()) {
//            selectDomainButton.setText("Select Domains");
//        } else {
//            selectDomainButton.setText("Selected (" + selectedDomainIds.size() + ")");
//        }
//    }
    private void updateSelectDomainButtonText() {

    }
    private void navigateToFragment(View view, String typeMedia, String mediaId) {
        Bundle bundle = new Bundle();
        bundle.putString("mediaId", mediaId);
        System.out.println("test1:"+mediaId);
        if ("pdf".equalsIgnoreCase(typeMedia)) {
            Navigation.findNavController(view).navigate(R.id.nav_pdf, bundle);
            System.out.println("test2");
        }
    }
    @Override
    public int getItemCount() {
        return educatorList!=null?educatorList.size():null;
    }
    public void updateList(List<VerEducator> newEducatorList) {
        educatorList = newEducatorList != null ? newEducatorList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPost;
        TextView tvEducatorName, tvDate, tvStatus, tvViewProof, tVDomain;
        Button btnReject, btnApprove, btnDelete;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPost = itemView.findViewById(R.id.IVPost);
            tvEducatorName = itemView.findViewById(R.id.TVEducatorName);
            tvDate = itemView.findViewById(R.id.TVRequestEDate);
            tvStatus = itemView.findViewById(R.id.TVStatus);
            btnReject = itemView.findViewById(R.id.BTNReject);
            btnApprove = itemView.findViewById(R.id.BTNApprove);
            btnDelete = itemView.findViewById(R.id.BTNDelete);
            tvViewProof = itemView.findViewById(R.id.TVViewProof);
            tVDomain = itemView.findViewById(R.id.TVDomain);
        }
    }

    public interface OnHandleEducatorActionListener {
        void onRejectClicked(VerEducator educator, int position);
        void onApprovedClicked(VerEducator educator, int position);
        void onDeleteClicked(VerEducator educator, int position);
        void onEducatorNameClicked(VerEducator educator, int position);
        void onViewProofClicked(VerEducator educator, int position);
        void onDomainClicked(VerEducator educator, int position);
    }
}
