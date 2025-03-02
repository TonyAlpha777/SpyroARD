package dam.pmdm.spyrothedragon.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import dam.pmdm.spyrothedragon.R;

public class VideoDialogFragment extends DialogFragment {
    private VideoView videoView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        videoView = view.findViewById(R.id.videoView);
        // Configurar los controles del video
        MediaController mediaController = new MediaController(requireContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        // Cargar el video desde res/raw
        String videoPath = "android.resource://" + requireContext().getPackageName() + "/" + R.raw.spyrovideo;
        //En el caso de utilizar una url en internet:
        //Uri videoUri = Uri.parse("https://youtu.be/uVHGuhHApMw?si=8Ocf3776ffjIBkPZ");
        Uri videoUri = Uri.parse(videoPath);

        videoView.setVideoURI(videoUri);
        videoView.start();

        // Cerrar el dialog cuando termine el video
        videoView.setOnCompletionListener(mp -> dismiss());

        return view;
    }

}
