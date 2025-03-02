package dam.pmdm.spyrothedragon.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import dam.pmdm.spyrothedragon.adapters.WorldsAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentWorldsBinding;
import dam.pmdm.spyrothedragon.databinding.GuideWorldsBinding;
import dam.pmdm.spyrothedragon.models.World;

public class WorldsFragment extends Fragment {

    private FragmentWorldsBinding binding;
    private RecyclerView recyclerView;
    private WorldsAdapter adapter;
    private List<World> worldsList;

    private FragmentWorldsBinding worldsBinding;
    private GuideWorldsBinding guideWorldsBinding;
    private MediaPlayer buttonSoundNext;
    private MediaPlayer buttonSoundBack;

    //SharedPreferences
    private GuidePreferences guidePreferences;
    private Boolean needToStartAnimation = true; //Variable booleana para comenzar o no la guía
    private RecyclerView.OnItemTouchListener itemTouchBlocker;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWorldsBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerViewWorlds;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        worldsList = new ArrayList<>();
        adapter = new WorldsAdapter(worldsList);
        recyclerView.setAdapter(adapter);


        loadWorlds();

        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Incluimos la guía.
        guideWorldsBinding = binding.includeWorldsLayout;
        //Audios
        buttonSoundNext = MediaPlayer.create(requireContext(), R.raw.button_sound);
        buttonSoundBack = MediaPlayer.create(requireContext(), R.raw.spyro_back);


        //SharedPreferences
        guidePreferences = new GuidePreferences(requireContext());


        if (guidePreferences.isGuideCompleted()) {
            // Si la guía está completa, activamos los controles
            enableRecyclerView();
            ((MainActivity) requireActivity()).restartBottomNavigation();
            ((MainActivity) requireActivity()).bloquearMenuActionBar(false);
            // guidePreferences.resetGuidePreferences();
        } else if (!guidePreferences.isGuideAbandoned2()) {
          
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

        //Evento botón Siguiente worldsNextButton
        guideWorldsBinding.worldsNextButton.setOnClickListener(v -> {
            //Reproducimos el Audio
            buttonSoundNext.start();
            //Ocultamos la guia
            guideWorldsBinding.guideWorldsLayout.setVisibility(View.GONE);
            //Navegamos al siguiente fragmento
            NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
            navController.navigate(R.id.action_fragmentWorlds_to_fragmentCollectibles);
        });

        //Evento botón Atrás worldBackButton
        guideWorldsBinding.worldsBackButton.setOnClickListener(v -> {
            //Reproducimos el Audio
            buttonSoundBack.start();
            //marcamos para que salte la animacion

            guidePreferences.setGuideAbandoned2();
            //Navegamos al fragmento anterior
            NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
            navController.popBackStack();

        });
        //guideWorldsBinding.exit_guide.setOnClickListener(this::onExitGuide);//Al pulsar saltamos la guía28/02 madrugada
        //Evento del botón saltar guía
        guideWorldsBinding.exitGuide.setOnClickListener(v -> {
            //Marcamos la guía como incompletada asi cuando reiniciemos la app,
            //nos permitira iniciar de nuevo la guía.
            marcarAbandonado();

            //Ocultamos la guia
            guideWorldsBinding.guideWorldsLayout.setVisibility(View.GONE);

            //Habilitamos el recyclerview y el about
            enableRecyclerView();
            //Habilitamos el about
            ((MainActivity) requireActivity()).bloquearMenuActionBar(false);
            //Habilitamos el bottomNavigation
            ((MainActivity) requireActivity()).restartBottomNavigation();
            //No vamos necesitar que se active la animación al cambiar de fragment
            needToStartAnimation = false;
            Toast.makeText(requireContext(), "Has Abandonado la Guia!!!.", Toast.LENGTH_SHORT).show();
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
        guideWorldsBinding = null;
    }

    private void loadWorlds() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.worlds);

            // Crear un parser XML
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            World currentWorld = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if ("world".equals(tagName)) {
                            currentWorld = new World();
                        } else if (currentWorld != null) {
                            if ("name".equals(tagName)) {
                                currentWorld.setName(parser.nextText());
                            } else if ("description".equals(tagName)) {
                                currentWorld.setDescription(parser.nextText());
                            } else if ("image".equals(tagName)) {
                                currentWorld.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();

                        if ("world".equals(tagName) && currentWorld != null) {
                            worldsList.add(currentWorld);
                        }
                        break;
                }

                eventType = parser.next();
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//fin LoadWorlds()

    private void animation() {
        // if (needToStartAnimation) {

        //Deshabilitar la recyclerView y bottomNavigation y about
        disableRecyclerView();
        ((MainActivity) requireActivity()).blokedBottomNavigation();
        ((MainActivity) requireActivity()).bloquearMenuActionBar(true);
        //Hacemos visible la guia
        guideWorldsBinding.guideWorldsLayout.setVisibility(View.VISIBLE);
        //Iniciamos la animación del bottomNavigation
        ((MainActivity) requireActivity()).drawCircle(R.id.nav_worlds, R.drawable.circle_bottom_navigation);
        //Inhabilitamos los botones
        guideWorldsBinding.worldsBackButton.setEnabled(false);
        guideWorldsBinding.worldsNextButton.setEnabled(false);

        //Le vamos a dar al bocadillo animación

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(
                guideWorldsBinding.worldsMessage, "alpha", 0f, 1f);

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
                guideWorldsBinding.worldsBackButton.setEnabled(true);
                guideWorldsBinding.worldsNextButton.setEnabled(true);


                // }
            }
        });


    }//fin metodo animation

    // Metodo para deshabilitar la recyclerview
    private void disableRecyclerView() {

        //Deshabilita el menu about

        //Deshabilitamos el recyclerview y el scroll
        binding.recyclerViewWorlds.setClickable(false);
        binding.recyclerViewWorlds.setFocusable(false);
        binding.recyclerViewWorlds.setEnabled(false);
        // Bloquear scroll completamente
        binding.recyclerViewWorlds.setLayoutManager(new LinearLayoutManager(requireContext()) {
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
        binding.recyclerViewWorlds.addOnItemTouchListener(itemTouchBlocker);
        // ((MainActivity) requireActivity()).blokedBottomNavigation();
    }

    //Metodo para Habilitar la recycledView
    private void enableRecyclerView() {
        //Habilitamos el recyclerview y el scroll
        binding.recyclerViewWorlds.setClickable(true);
        binding.recyclerViewWorlds.setFocusable(true);
        binding.recyclerViewWorlds.setEnabled(true);
        // Desbloquear scroll completamente
        binding.recyclerViewWorlds.setLayoutManager(new LinearLayoutManager(requireContext()) {
            @Override
            public boolean canScrollVertically() {
                return true; // Desbloquea el scroll vertical
            }

        });

        //Desbloquear la interacciòn con los items
        binding.recyclerViewWorlds.removeOnItemTouchListener(itemTouchBlocker);
        //  ((MainActivity) requireActivity()).restartBottomNavigation();
    }



}
