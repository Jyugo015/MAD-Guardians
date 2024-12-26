package com.example.madguardians.ui.staff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;
import com.example.madguardians.database.VerEducator;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewEducatorAdapter extends RecyclerView.Adapter<RecycleViewEducatorAdapter.PostViewHolder> {
    private List<VerEducator> educatorList;
    private Context context;
    private OnHandleEducatorActionListener onHandleEducatorActionListener;
    // Constructor
    public RecycleViewEducatorAdapter(List<VerEducator> verEducatorList, Context context, OnHandleEducatorActionListener onHandleEducatorActionListener) {
        this.educatorList = verEducatorList!=null?verEducatorList:new ArrayList<>();
        this.context = context;
        this.onHandleEducatorActionListener = onHandleEducatorActionListener;
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

        UserDao userDao = AppDatabase.getDatabase(context).userDao();
        User applier = userDao.getById(verEducator.getUserId());

        holder.tvEducatorName.setText(applier.getName());
        //add time
        holder.tvDate.setText("Date");
        holder.tvStatus.setText(verEducator.getVerifiedStatus());
        holder.tvEducatorName.setOnClickListener(v -> {
            if (onHandleEducatorActionListener != null) {
                onHandleEducatorActionListener.onEducatorNameClicked(verEducator, position);
            }
        });
        holder.tvViewProof.setOnClickListener(v -> {
            if (onHandleEducatorActionListener != null) {
                onHandleEducatorActionListener.onViewProofClicked(verEducator, position);
            }
        });
        // Handle buttons based on post status
        if ("pending".equals(verEducator.getVerifiedStatus())) {
            if (holder.btnReject != null) {
                holder.btnReject.setOnClickListener(v -> {
                    if (onHandleEducatorActionListener != null) {
                        onHandleEducatorActionListener.onRejectClicked(verEducator, position);
                        verEducator.setVerifiedStatus("rejected"); // Update status
                        notifyItemChanged(position); // Refresh item
                    }
                });
            }

            if (holder.btnApprove != null) {
                holder.btnApprove.setOnClickListener(v -> {
                    if (onHandleEducatorActionListener != null) {
                        onHandleEducatorActionListener.onApprovedClicked(verEducator, position);
                        verEducator.setVerifiedStatus("approved"); // Update status
                        notifyItemChanged(position); // Refresh item
                    }
                });
            }
        } else {
            if (holder.btnDelete != null) {
                holder.btnDelete.setOnClickListener(v -> {
                    if (onHandleEducatorActionListener != null) {
                        onHandleEducatorActionListener.onDeleteClicked(verEducator, position);
                    }
                });
            }
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
        TextView tvEducatorName, tvDate, tvStatus, tvViewProof;
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
        }
    }

    public interface OnHandleEducatorActionListener {
        void onRejectClicked(VerEducator educator, int position);
        void onApprovedClicked(VerEducator educator, int position);
        void onDeleteClicked(VerEducator educator, int position);
        void onEducatorNameClicked(VerEducator educator, int position);
        void onViewProofClicked(VerEducator educator, int position);
    }
}
