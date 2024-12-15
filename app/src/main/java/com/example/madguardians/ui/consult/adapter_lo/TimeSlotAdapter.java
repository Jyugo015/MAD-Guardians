package com.example.madguardians.ui.consult.adapter_lo;

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
        holder.bookButton.setSelected(buttonStates.get(position));


        holder.bookButton.setOnClickListener(v -> {
            boolean isSelected = !buttonStates.get(position);
            buttonStates.set(position, isSelected);
            holder.bookButton.setSelected(isSelected);
            listener.onTimeSlotSelected(timeSlot, isSelected);
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
