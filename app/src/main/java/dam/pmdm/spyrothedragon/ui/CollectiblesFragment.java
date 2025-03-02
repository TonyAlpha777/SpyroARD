package dam.pmdm.spyrothedragon.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dam.pmdm.spyrothedragon.GuidePreferences;
import dam.pmdm.spyrothedragon.MainActivity;
import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.adapters.CollectiblesAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentCollectiblesBinding;
import dam.pmdm.spyrothedragon.databinding.GuideAboutBinding;
import dam.pmdm.spyrothedragon.databinding.GuideCollectiblesBinding;
import dam.pmdm.spyrothedragon.databinding.GuideResumeBinding;
import dam.pmdm.spyrothedragon.models.Collectible;

public class CollectiblesFragment extends Fragment {

    private FragmentCollectiblesBinding binding;
    private RecyclerView recyclerView;
    private CollectiblesAdapter adapter;
    private List<Collectible> collectiblesList;

    private GuideCollectiblesBinding guideCollectiblesBinding;
    private GuideResumeBinding guideResumeBinding;
    private GuideAboutBinding guideAboutBinding;
    private MediaPlayer buttonSoundNext;
    private MediaPlayer buttonSoundBack;


    //SharedPreferences
    private GuidePreferences guidePreferences;
    private Boolean needToStartAnimation = true; //Variable booleana para comenzar o no la guía
    //Para bloquear la interacciones con la pantalla
    private RecyclerView.OnItemTouchListener itemTouchBlocker;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCollectiblesBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerViewCollectibles;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        collectiblesList = new ArrayList<>();
        adapter = new CollectiblesAdapter(collectiblesList, () -> {
            VideoDialogFragment videoDialog = new VideoDialogFragment();
            videoDialog.show(getChildFragmentManager(), "VideoDialog");
        });
        recyclerView.setAdapter(adapter);

        loadCollectibles();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        //Incluimos la guía al fragment
        guideCollectiblesBinding = binding.includeCollectiblesLayout;
        guideResumeBinding = binding.includeGuiaResume;
        guideAboutBinding = binding.includeGuiaAbout;
        //Audios
        buttonSoundNext = MediaPlayer.create(requireContext(), R.raw.button_sound);
        buttonSoundBack = MediaPlayer.create(requireContext(), R.raw.spyro_back);
        //guideCollectiblesBinding.guideCollectiblesLayout.setVisibility(View.VISIBLE);


        //SharedPreferences
        guidePreferences = new GuidePreferences(requireContext());

        if (guidePreferences.isGuideCompleted()) {
            // Si la guía está completa, activamos los controles
            enableRecyclerView();
            ((MainActivity) requireActivity()).restartBottomNavigation();
            ((MainActivity) requireActivity()).bloquearMenuActionBar(false);

        } else if (!guidePreferences.isGuideAbandoned3()) {

            // Si la guía no está completa y tampoco ha sido abandonada, reproducimos la animación
            if (needToStartAnimation) {
                animation();

            }

        } else {
            // Si la guía no está completa pero ha sido abandonada, solo activamos los controles
            enableRecyclerView();
            ((MainActivity) requireActivity()).restartBottomNavigation();
            ((MainActivity) requireActivity()).bloquearMenuActionBar(false);
        }


        //Al pulsar el botón siguiente de guide_collectibles
        guideCollectiblesBinding.collectiblesNextButton.setOnClickListener(v -> {
            //Reproduce sonido al pulsar el botón
            buttonSoundNext.start();
            //Ocultamos guide_collectibles
            guideCollectiblesBinding.guideCollectiblesLayout.setVisibility(View.GONE);
            //Hacemos visible guide_about
            guideAboutBinding.guideAboutLayout.setVisibility(View.VISIBLE);
            //Iniciamos la animación para guide_about
            showAnimationOnOverflow();


        });
        //Al pulsar el botón siguiente de guide_about
        guideAboutBinding.aboutNextButton.setOnClickListener(v -> {
            //Reproduce sonido al pulsar el botón
            buttonSoundNext.start();
            //Ocultamos guide_about
            guideAboutBinding.guideAboutLayout.setVisibility(View.GONE);
            //Hacemos visible guide_resume
            guideResumeBinding.guideResumeLayout.setVisibility(View.VISIBLE);


        });


        //Al pulsar el botón siguiente de guide_resume
        guideResumeBinding.resumeFinishButton.setOnClickListener(v -> {
            //Reproduce sonido al pulsar el botón
            buttonSoundNext.start();

            //Marcamos la guía como completada
            guidePreferences.setGuideCompleted();

            ((MainActivity) requireActivity()).bloquearMenuActionBar(false);
            Toast.makeText(requireContext(), "Guia Finalizada", Toast.LENGTH_SHORT).show();
            showConfirmationDesactiveGuide();

        });


        //Evento del backButton
        guideCollectiblesBinding.collectiblesBackButton.setOnClickListener(v -> {
            //Reproduce sonido al pulsar el botón
            buttonSoundBack.start();
            //Navegamos al fragment anterior
            NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
            navController.popBackStack();

        });

        // guideCollectiblesBinding.exit_guide.setOnClickListener(this::onExitGuide);//Al pulsar saltamos la guía28/02/ madrugada
        //Evento del botón saltar guía
        guideCollectiblesBinding.exitGuide.setOnClickListener(v -> {
            //Marcamos la guía como incompletada asi cuando reiniciemos la app,
            //nos permitira iniciar de nuevo la guía.

            marcarAbandonado();
            //Ocultamos la guia
            guideCollectiblesBinding.guideCollectiblesLayout.setVisibility(View.GONE);
            //Habilitamos el recyclerview y el about
            enableRecyclerView();
            //Habilitamos el about
            ((MainActivity) requireActivity()).bloquearMenuActionBar(false);
            //Habilitamos el bottomNavigation
            ((MainActivity) requireActivity()).restartBottomNavigation();
            //No vamos necesitar que se active la animación al cambiar de fragment
            needToStartAnimation = false;

            Toast.makeText(requireContext(), "Has Abandonado la Guia!!!", Toast.LENGTH_SHORT).show();

        });


    }

    // AlertDialog, donde podemos resetar la guia o continuar con la app
    private void showConfirmationDesactiveGuide() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("DesactivarGuía");
        builder.setMessage("¿Deseas desactivar la guía? No se volverá a mostrar");

        // Botón ACEPTAR
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            guidePreferences.setGuideDesactive();
            ((MainActivity) requireActivity()).restartBottomNavigation();

            // Usamos un pequeño retraso para asegurarnos de que los valores se guardan antes de navegar
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
                navController.navigate(R.id.action_fragmentCollectibles_to_fragmentCharacters);
            }, 100); // 100ms para dar tiempo a SharedPreferences a guardar

        });

        // Botón CANCELAR
        builder.setNegativeButton("Cancelar", (dialog, which) -> {

            NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
            navController.navigate(R.id.action_fragmentCollectibles_to_fragmentCharacters);

            dialog.dismiss();
            Toast.makeText(requireContext(), "Puedes volver a ver la Guia.", Toast.LENGTH_SHORT).show();

        });

        // Mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showAnimationOnOverflow() {


        guideAboutBinding.aboutNextButton.setEnabled(false);
        // Coloca la animación (por ejemplo, un círculo) en esa posición
        ImageView rowAbout = guideAboutBinding.about;

        // Animación (por ejemplo, una escala)
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rowAbout, "scaleX", 1f, 1.5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rowAbout, "scaleY", 1f, 1.5f, 1f);
        scaleX.setDuration(500);
        scaleY.setDuration(500);
        scaleX.setRepeatCount(3);
        scaleY.setRepeatCount(3);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // if (needToStartGuide) {
                super.onAnimationEnd(animation);

                //Al finalizar muestra el alertDialog
                ((MainActivity) requireActivity()).showInfoDialog();
                guideAboutBinding.aboutNextButton.setEnabled(true);


            }


        });


    }

    //Metodo para resetear es el estado de SharedPrefences
    private void resetAbandoned() {
        guidePreferences.setResetGuideAbandoned0();
        guidePreferences.setResetGuideAbandoned1();
        guidePreferences.setResetGuideAbandoned2();
        guidePreferences.setResetGuideAbandoned3();
    }

    //Metodo que establece el estado de SharedPreferences
    private void marcarAbandonado() {
        guidePreferences.setGuideAbandoned0();
        guidePreferences.setGuideAbandoned1();
        guidePreferences.setGuideAbandoned2();
        guidePreferences.setGuideAbandoned3();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        guideCollectiblesBinding = null;
    }

    private void loadCollectibles() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.collectibles);

            // Crear un parser XML
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            Collectible currentCollectible = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if ("collectible".equals(tagName)) {
                            currentCollectible = new Collectible();
                        } else if (currentCollectible != null) {
                            if ("name".equals(tagName)) {
                                currentCollectible.setName(parser.nextText());
                            } else if ("description".equals(tagName)) {
                                currentCollectible.setDescription(parser.nextText());
                            } else if ("image".equals(tagName)) {
                                currentCollectible.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();

                        if ("collectible".equals(tagName) && currentCollectible != null) {
                            collectiblesList.add(currentCollectible);
                        }
                        break;
                }

                eventType = parser.next();
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//fin loadCollectibles()

    private void animation() {
        if (needToStartAnimation) {
            disableRecyclerView();
            ((MainActivity) requireActivity()).blokedBottomNavigation();
            ((MainActivity) requireActivity()).bloquearMenuActionBar(true);
            guideCollectiblesBinding.guideCollectiblesLayout.setVisibility(View.VISIBLE);
            //Iniciamos la animación del bottomNavigation
            ((MainActivity) requireActivity()).drawCircle(R.id.nav_collectibles, R.drawable.circle_bottom_navigation);
            //Inhabilitamos los botones
            guideCollectiblesBinding.collectiblesNextButton.setEnabled(false);
            guideCollectiblesBinding.collectiblesBackButton.setEnabled(false);

            //Le vamos a dar al bocadillo animación

            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(
                    guideCollectiblesBinding.collectiblesMessage, "alpha", 0f, 1f);

            fadeIn.setRepeatCount(3);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(fadeIn);
            animatorSet.setDuration(1000);
            animatorSet.start();


            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // if (needToStartGuide) {
                    super.onAnimationEnd(animation);

                    //Habilitamos los botones al terminar la animación
                    guideCollectiblesBinding.collectiblesNextButton.setEnabled(true);
                    guideCollectiblesBinding.collectiblesBackButton.setEnabled(true);


                }
            });


        }//fin if
    }//fin método animation

    // Metodo para deshabilitar la recyclerview
    private void disableRecyclerView() {

        //Deshabilita el menu about

        //Deshabilitamos el recyclerview y el scroll
        binding.recyclerViewCollectibles.setClickable(false);
        binding.recyclerViewCollectibles.setFocusable(false);
        binding.recyclerViewCollectibles.setEnabled(false);
        // Bloquear scroll completamente
        binding.recyclerViewCollectibles.setLayoutManager(new LinearLayoutManager(requireContext()) {
            @Override
            public boolean canScrollVertically() {
                return false; // Bloquea el scroll vertical
            }

            @Override
            public boolean canScrollHorizontally() {
                return false; // Bloquea el scroll horizontal (si lo hay)
            }
        });

        itemTouchBlocker = new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return true; // Bloquea eventos táctiles
            }
        };
        binding.recyclerViewCollectibles.addOnItemTouchListener(itemTouchBlocker);
        // ((MainActivity) requireActivity()).blokedBottomNavigation();
    }

    //Metodo para Habilitar la recycledView
    private void enableRecyclerView() {
        //Habilitamos el recyclerview y el scroll
        binding.recyclerViewCollectibles.setClickable(true);
        binding.recyclerViewCollectibles.setFocusable(true);
        binding.recyclerViewCollectibles.setEnabled(true);
        // Desbloquear scroll completamente
        binding.recyclerViewCollectibles.setLayoutManager(new LinearLayoutManager(requireContext()) {
            @Override
            public boolean canScrollVertically() {
                return true; // Bloquea el scroll vertical
            }

        });

        //Desbloquear la interacciòn con los items
        binding.recyclerViewCollectibles.removeOnItemTouchListener(itemTouchBlocker);
        ((MainActivity) requireActivity()).restartBottomNavigation();
    }


}
