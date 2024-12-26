package com.example.madguardians.ui.consult.adapter_lo;

import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.ui.consult.model_lo.TimeSlotModel;
import com.example.madguardians.ui.consult.model_lo.UserModel;
import com.example.madguardians.ui.consult.utils_lo.FirebaseUtil;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {


    UserModel userModel;


    private  List<TimeSlotModel> timeSlots;
    private  List<Boolean> buttonStates;
    private  OnTimeSlotSelectedListener listener;

    public interface OnTimeSlotSelectedListener {
        void onTimeSlotSelected(TimeSlotModel timeSlot, boolean isSelected);
    }
    public TimeSlotAdapter(List<TimeSlotModel> timeSlots, OnTimeSlotSelectedListener listener) {
        this.timeSlots = timeSlots;
        this.listener = listener;
        this.buttonStates = new ArrayList<>(Collections.nCopies(timeSlots.size(), false));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimeSlotModel timeSlot = timeSlots.get(position);
        holder.bookButton.setText(timeSlot.getTime());

        boolean isSelected = buttonStates.get(position);
        int color = isSelected
                ? ContextCompat.getColor(holder.itemView.getContext(), R.color.colorAccent)
                : ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary);

        // Apply color
        holder.bookButton.setBackground(new ColorDrawable(color));

        // Add logs to confirm
        Log.d("ButtonColor", "Applying color: " + color + " to position: " + position);

        // Set click listener
        holder.bookButton.setOnClickListener(v -> {
            boolean newState = !buttonStates.get(position);
            buttonStates.set(position, newState);

            if (newState) {
                listener.onTimeSlotSelected(timeSlot, true);
            } else {
                listener.onTimeSlotSelected(timeSlot, false);
            }

            notifyItemChanged(position); // Notify the change for that item
        });
    }





    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        Button bookButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookButton = itemView.findViewById(R.id.timeSlotButton);
        }
    }
}
