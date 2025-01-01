package com.example.madguardians.ui.course;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.madguardians.R;
import com.example.madguardians.firebase.MediaFB;
import com.example.madguardians.utilities.MediaHandler;
import com.example.madguardians.utilities.UploadCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {

    private MediaFB image;
    private View view;
    public ImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageFragment newInstance(String param1, String param2) {
        ImageFragment fragment = new ImageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            MediaFB.getMedia(getArguments().getString("mediaId"), new UploadCallback<MediaFB>() {
                @Override
                public void onSuccess(MediaFB result) {
                    image = result;
                    ImageView imageView = view.findViewById(R.id.imageView);
                    MediaHandler.displayImage(getContext(), image.getUrl(), imageView);
                }
                @Override
                public void onFailure(Exception e) {
                    Log.e("TAG", "onFailure: ", e);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_image, container, false);
        return view;
    }
}