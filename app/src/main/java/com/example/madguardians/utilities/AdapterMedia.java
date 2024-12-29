package com.example.madguardians.utilities;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madguardians.R;

import java.util.List;

public class AdapterMedia extends RecyclerView.Adapter<AdapterMedia.ViewHolder> {

    private List<Uri> medias;
    private String mediaType;
    private OnRemoveClickListener removeClickListener;

    public interface OnRemoveClickListener {
        void onRemove(Uri uri);
    }

    public AdapterMedia(List<Uri> medias, String mediaType, OnRemoveClickListener removeClickListener) {
        this.medias = medias;
        this.mediaType = mediaType;
        this.removeClickListener = removeClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.segment_item_media, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri mediaUri = medias.get(position);
        if (mediaType.equalsIgnoreCase("image") || mediaType.equalsIgnoreCase("video")){
            Glide.with(holder.itemView.getContext()).load(mediaUri).into(holder.imageView);
            Log.d("TAG", "onBindViewHolder: image");
        } else if (mediaType.equalsIgnoreCase("pdf")) {
            holder.imageView.setImageResource(R.drawable.ic_pdf);
            Log.d("TAG", "onBindViewHolder: pdf");
        }
        holder.removeButton.setOnClickListener(v -> {
            if (removeClickListener != null) removeClickListener.onRemove(mediaUri);
        });
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, removeButton;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image_preview);
            removeButton = view.findViewById(R.id.remove_button);
        }
    }
}
