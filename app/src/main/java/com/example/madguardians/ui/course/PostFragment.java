package com.example.madguardians.ui.course;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madguardians.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {

    private Post post;
    private static int NUMBER_OF_SEGMENT;
    private int lastViewId;
    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            post = Post.getPost(getArguments().getString("postId"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        TextView TVTitle = view.findViewById(R.id.TVTitle);
        TextView TVDescription = view.findViewById(R.id.TVDescription);
        if (post != null) {
            TVTitle.setText(post.getTitle());
            TVDescription.setText(post.getDescription());
        } else {
            Log.d("Post fragment", "onCreateView: Post is null");
        }
        NUMBER_OF_SEGMENT = 0;
        lastViewId = 0;

        //find if this post is viewed
        // if yes, change the status as "Completed"
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ConstraintLayout CLLevel = view.findViewById(R.id.CLLevel);

        if (post != null) {
            androidx.constraintlayout.widget.ConstraintLayout CLContainer = view.findViewById(R.id.CLContainer);
            ArrayList<Media> imgMedias = new ArrayList<>();
            ArrayList<Media> vidMedias = new ArrayList<>();
            ArrayList<Media> pdfMedias = new ArrayList<>();
            if (post.getImageSetId() != null) {
                imgMedias = Media.getMedias(post.getImageSetId());
            }
            if (post.getFileSetId() != null) {
                pdfMedias = Media.getMedias(post.getFileSetId());
            }
            if (post.getVideoSetId() != null) {
                vidMedias = Media.getMedias(post.getVideoSetId());
            }
            for (Media imgMedia : imgMedias) {
                displayMediaSegment(CLContainer, imgMedia, "image");
            }for (Media vidMedia : vidMedias) {
                displayMediaSegment(CLContainer, vidMedia, "video");
            }for (Media pdfMedia : pdfMedias) {
                displayMediaSegment(CLContainer, pdfMedia, "pdf");
            }
        }
    }

    private void displayMediaSegment(ConstraintLayout clContainer, Media imgMedia, String typeMedia) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View mediaSegment = layoutInflater.inflate(R.layout.segment_media_option, clContainer, false);
        mediaSegment.setId(View.generateViewId());
        ImageView IVImage = mediaSegment.findViewById(R.id.IVImage);
        TextView TVDesc = mediaSegment.findViewById(R.id.TVDesc);
        Button BTNStart = mediaSegment.findViewById(R.id.BTNStart);
        Button BTNReport = mediaSegment.findViewById(R.id.BTNReport);
        Button BTNComment = mediaSegment.findViewById(R.id.BTNComment);


        // set text in the button
        if (isRead(imgMedia))
            BTNStart.setText("Completed");
        else
            BTNStart.setText("Start!");

        if (typeMedia.equalsIgnoreCase("image")){
            IVImage.setImageResource(R.drawable.ic_image);
            TVDesc.setText(R.string.image_desc);
        }
        else if (typeMedia.equalsIgnoreCase("video")){
            IVImage.setImageResource(R.drawable.ic_video);
            TVDesc.setText(R.string.video_desc);
        }
        else if (typeMedia.equalsIgnoreCase("pdf")){
            IVImage.setImageResource(R.drawable.ic_pdf);
            TVDesc.setText(R.string.pdf_desc);
        }

        BTNStart.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("mediaId", imgMedia.getMediaId());
            setIsRead(imgMedia.getMediaId());

            if (typeMedia.equalsIgnoreCase("image"))
                Navigation.findNavController(v).navigate(R.id.nav_img, bundle);
            else if (typeMedia.equalsIgnoreCase("video"))
                Navigation.findNavController(v).navigate(R.id.nav_vid, bundle);
            else if (typeMedia.equalsIgnoreCase("pdf")) {
                Navigation.findNavController(v).navigate(R.id.nav_pdf, bundle);
            }
        });

        clContainer.addView(mediaSegment);

        // Set constrain set for proper alignment
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(clContainer);

        if (NUMBER_OF_SEGMENT == 0) {
            // If it is the first element, attach it to the parent layout
            constraintSet.connect(mediaSegment.getId(), ConstraintSet.TOP, clContainer.getId(), ConstraintSet.TOP, 16);
        } else {
            // otherwise attach to previous one
            constraintSet.connect(mediaSegment.getId(), ConstraintSet.TOP, lastViewId, ConstraintSet.BOTTOM,16);
        }

        constraintSet.applyTo(clContainer);
        lastViewId = mediaSegment.getId();
        NUMBER_OF_SEGMENT++;

        ////////////////////////////////////////////////////////////////////////////////
        BTNReport.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Connecting to report", Toast.LENGTH_SHORT).show();
        });
        BTNComment.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Connecting to commment", Toast.LENGTH_SHORT).show();
        });
    }

    ////////////////////////////////////////////////////////////////////////////////
    private boolean isRead(Media pdfMedia) {
        return true;
    }

    private void setIsRead(String mediaId) {
    }
}