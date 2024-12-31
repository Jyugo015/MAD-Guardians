package com.example.madguardians.ui.course;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.madguardians.R;
import com.example.madguardians.firebase.Media;
import com.example.madguardians.utilities.MediaHandler;
import com.example.madguardians.utilities.UploadCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {

    private Media video;
    private View view;
    private ExoPlayer player;
    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Media.getMedia(getArguments().getString("mediaId"), new UploadCallback<Media>() {
                @Override
                public void onSuccess(Media result) {
                    video = result;
                    PlayerView playerView = view.findViewById(R.id.playerView);
                    player = new ExoPlayer.Builder(getContext()).build();
                    playerView.setPlayer(player);
                    MediaHandler.playVideo(video.getUrl(), player);
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
        view = inflater.inflate(R.layout.fragment_video, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (player != null)
            player.play();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}