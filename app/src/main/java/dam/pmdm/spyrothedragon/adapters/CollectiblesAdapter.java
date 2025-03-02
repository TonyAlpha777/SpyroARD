package dam.pmdm.spyrothedragon.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.databinding.FragmentCollectiblesBinding;
import dam.pmdm.spyrothedragon.models.Collectible;
import dam.pmdm.spyrothedragon.ui.CollectiblesFragment;

public class CollectiblesAdapter extends RecyclerView.Adapter<CollectiblesAdapter.CollectiblesViewHolder> {

    private List<Collectible> list;


    private static OnVideoTriggerListener listener;

    public interface OnVideoTriggerListener {
        void onTriggerVideo();
    }
    // Constructor del adaptador
    public CollectiblesAdapter(List<Collectible> collectibleList, OnVideoTriggerListener listener) {
        this.list = collectibleList;
        this.listener = listener;
    }


    @Override
    public CollectiblesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CollectiblesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CollectiblesViewHolder holder, int position) {
        Collectible collectible = list.get(position);
        holder.nameTextView.setText(collectible.getName());

        // Cargar la imagen (simulado con un recurso drawable)
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(collectible.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CollectiblesViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;
        private int clickCount = 0;
        private int id;


        public CollectiblesViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(v -> {
                id = getAdapterPosition();
                clickCount++;
                if (clickCount == 4 && id == 1) {

                    //ejecutar acción
                    listener.onTriggerVideo();
                    clickCount = 0;//resetar el contador

                }

            });
        }



    /*    public void bind(int position) {
            // Usamos getAdapterPosition() para obtener la posición correcta
            itemView.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition(); // Obtener la posición actual
                if (adapterPosition != RecyclerView.NO_POSITION) { // Asegurarse de que la posición es válida
                    if (adapterPosition == 1) { // Solo el ítem 1 activa el video
                        clickCount++;

                        // Muestra un Toast con el número de clics
                        Toast.makeText(itemView.getContext(), "Clics: " + clickCount, Toast.LENGTH_SHORT).show();

                        if (clickCount == 4) {
                            clickCount = 0; // Resetear el contador
                            listener.onTriggerVideo(); // Notificar al Fragment que se debe reproducir el video
                        }
                    }
                }
            });
        }*/
    }
}
/*   public void videoStart(Context context, int position) {
            Toast.makeText(context, "Ítem " + position + " id= " + id + " presionado 4" + " veces!", Toast.LENGTH_SHORT).show();
            VideoView videoView = itemView.findViewById(R.id.videoView);
            // Obtener el path del video (de la carpeta "raw" o almacenamiento externo)
            String videoPath = "android.resource://" + context.getPackageName() + "/" + R.raw.agua;
            Uri videoUri = Uri.parse(videoPath);

            // Configurar el VideoView
            videoView.setVideoURI(videoUri);
            videoView.setMediaController(new MediaController(context)); // Controles opcionales
            videoView.requestFocus();
            videoView.start(); // Reproducir automáticamente
// Detectar cuando el video termine y cambiar de fragmento
            videoView.setOnCompletionListener(mp -> {
                // Reemplaza con tu método para cambiar de fragmento
            ;
                NavController navController = Navigation.findNavController(itemView);
                navController.navigate(R.id.navigation_collectibles);
            });*/
