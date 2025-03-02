package dam.pmdm.spyrothedragon.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import dam.pmdm.spyrothedragon.databinding.GuideBinding;
import dam.pmdm.spyrothedragon.databinding.GuideCharactersBinding;
import dam.pmdm.spyrothedragon.models.Character;
import dam.pmdm.spyrothedragon.adapters.CharactersAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentCharactersBinding;


public class CharactersFragment extends Fragment {

    private FragmentCharactersBinding binding;

    private RecyclerView recyclerView;
    private CharactersAdapter adapter;
    private List<Character> charactersList;

    private GuideCharactersBinding guideCharactersBinding;
    private GuideBinding guideBinding;
    private Boolean needToStartGuide = true; //Variable booleana para comenzar o no la guía
    private boolean needToStartAnimation = true;

    private MediaPlayer soundNextButton;
    private MediaPlayer dragonFireSound;
    private FrameLayout animationContainer;
    private MediaPlayer themeSound;

    //SharedPreferences
    private GuidePreferences guidePreferences;
    private RecyclerView.OnItemTouchListener itemTouchBlocker;
    private SoundPool soundPool;
    private int buttonSoundInt;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCharactersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Inicializamos el RecyclerView y el adaptador
        recyclerView = binding.recyclerViewCharacters;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        charactersList = new ArrayList<>();


        // Pasamos la referencia correcta del Fragment a través de la interfaz
        adapter = new CharactersAdapter(charactersList, this::startFireAnimation);
        recyclerView.setAdapter(adapter);

        loadCharacters();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //SharedPreferences
        guidePreferences = new GuidePreferences(requireContext());

        // Inicializar contenedor de animación para spyro
        animationContainer = binding.animationContainer;
        //Incluimos la guia characters
        guideCharactersBinding = binding.includeGuiaCharacters;//26/02 mañabna
        //Ocultamos guide_characters
        guideCharactersBinding.guideCharactersLayout.setVisibility(View.GONE);
        //Aqui incluimos la guia inicial
        guideBinding = binding.includeLayout;//26/02 tarde

        //Hacemos visible la guia inicial guide
        // guideBinding.guideLayout.setVisibility(View.VISIBLE);

        //Ocultamos guide
        guideBinding.guideLayout.setVisibility(View.GONE);

        soundNextButton = MediaPlayer.create(requireContext(), R.raw.button_sound);
        dragonFireSound = MediaPlayer.create(requireContext(), R.raw.dragon_fire);

        // Inicializar SoundPool
        soundPool = new SoundPool.Builder()
                .setMaxStreams(5) // Máximo de sonidos simultáneos
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build())
                .build();

        // Cargar los audios
        buttonSoundInt = soundPool.load(requireContext(), R.raw.button_sound, 1);

        if (!guidePreferences.isGuideDesactive()) { // Si la guía NO está desactivada

            if (!guidePreferences.isGuideCompleted() && !guidePreferences.isGuideAbandoned0()) {

                inicializeGuide();
                if (guidePreferences.isGuideAbandoned2()) {
                    animation();
                }
            } else if (!guidePreferences.isGuideCompleted() && !guidePreferences.isGuideAbandoned1()) {

                animation();
            } else {

                showDialogAdvertencia();
            }
        } else {
            //Descomentar para poder reiniciar la guia, ya que al estar desactivada
            //no se volverá a mostrar.
            //Aceptar reiniciar la guia y volver a comentar y ejecutar de nuevo
            //para que no salga el AlertDialog
           // showConfirmationDialog();
        }


        //BOTONES DE LA GUIA guide_characters
        //Evento al pulsar el botón comenzar de guide_characters
        guideCharactersBinding.charactersNextButton.setOnClickListener(v -> {
            //Ocultamos guide_characters
            guideCharactersBinding.guideCharactersLayout.setVisibility(View.GONE);

            soundPool.play(buttonSoundInt, 1, 1, 0, 0, 1);
            //Navegamos hacia el siguiente fragmento
            NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
            navController.navigate(R.id.action_fragmentCharacteres_to_fragmentWorlds);
        });


        //Evento del botón saltar guía
        guideCharactersBinding.exitGuide.setOnClickListener(v -> {
            //Marcamos la guía como incompletada asi cuando reiniciemos la app,
            //nos permitira iniciar de nuevo la guía.
            marcarAbandonado();

            //Ocultamos la guia
            guideCharactersBinding.guideCharactersLayout.setVisibility(View.GONE);
            //Habilitamos el recyclerview y el about
            enableRecyclerView();
            //Habilitamos el about
            ((MainActivity) requireActivity()).bloquearMenuActionBar(false);
            //Habilitamos el bottomNavigation
            ((MainActivity) requireActivity()).restartBottomNavigation();
            //No vamos necesitar que se active la animación al cambiar de fragment
            needToStartAnimation = false;

            Toast.makeText(requireContext(), "Has abandonado la Guía!!!.", Toast.LENGTH_SHORT).show();
            showDialogAdvertencia();
        });


    }

    // Metodo para deshabilitar la recyclerview
    private void disableRecyclerView() {

        //Deshabilitamos el recyclerview y el scroll
        binding.recyclerViewCharacters.setClickable(false);
        binding.recyclerViewCharacters.setFocusable(false);
        binding.recyclerViewCharacters.setEnabled(false);
        // Bloquear scroll completamente
        binding.recyclerViewCharacters.setLayoutManager(new LinearLayoutManager(requireContext()) {
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
        binding.recyclerViewCharacters.addOnItemTouchListener(itemTouchBlocker);

    }

    //Metodo para Habilitar la recycledView
    private void enableRecyclerView() {
        //Habilitamos el recyclerview y el scroll
        binding.recyclerViewCharacters.setClickable(true);
        binding.recyclerViewCharacters.setFocusable(true);
        binding.recyclerViewCharacters.setEnabled(true);
        // Desbloquear scroll completamente
        binding.recyclerViewCharacters.setLayoutManager(new LinearLayoutManager(requireContext()) {
            @Override
            public boolean canScrollVertically() {
                return true; // Desbloquea el scroll vertical
            }

            @Override
            public boolean canScrollHorizontally() {
                return true; // Desbloquea el scroll horizontal (si lo hay)
            }
        });


        //Desbloquear la interacciòn con los items
        binding.recyclerViewCharacters.removeOnItemTouchListener(itemTouchBlocker);

    }

    private void resetGuide() {
        guidePreferences = new GuidePreferences(requireContext());
        guidePreferences.resetGuide();
        Toast.makeText(requireContext(), "Guía Reiniciada", Toast.LENGTH_SHORT).show();
        //navController.navigate(R.id.navigation_characters);
        needToStartGuide = true;
        inicializeGuide();

    }

    private void showConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Reiniciar Guía");
        builder.setMessage("¿Deseas reiniciar la Guía Interactiva?");

        // Botón ACEPTAR
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            //
            guidePreferences.resetGuidePreferences();
            inicializeGuide();
        });

        // Botón CANCELAR
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            //guidePreferences.setGuideDesactive();
            enableRecyclerView();
            //Desbloquear bottomNavigation
            ((MainActivity) requireActivity()).restartBottomNavigation();
            Toast.makeText(requireContext(), "Para volver a iniciar la guía, deberás reiniciar la app.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        // Mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogAdvertencia() {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("NO HAS COMPLETADO LA GUIA");
        builder.setMessage("Tienes que completar la Guia, para no volver a ver este mensaje");

        // Botón ACEPTAR
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            //Reseteamos sharedpreferences e iniciamos  la guia
            guidePreferences.resetGuidePreferences();
            inicializeGuide();
        });

        // Botón CANCELAR
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            //guidePreferences.setGuideDesactive();
            enableRecyclerView();
            //Desbloquear bottomNavigation
            ((MainActivity) requireActivity()).restartBottomNavigation();
            Toast.makeText(requireContext(), "Recuerda que tienes que completar la Guía!!!.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        // Mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
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

    // Metodo que inicializa la guía.
    private void inicializeGuide() {

        if (needToStartGuide) {

            resetAbandoned();
            //Deshabilita el bottomNavigationView
            ((MainActivity) requireActivity()).blokedBottomNavigation();
            //Deshabilitamos el menu about
            ((MainActivity) requireActivity()).bloquearMenuActionBar(true);

            // binding.navView.getMenu().setGroupEnabled(0, false);
            //Hacemos la guía visible
            guideBinding.guideLayout.setVisibility(View.VISIBLE);

            //Instanciamos los objetos MediaPlayer
            // soundButton = MediaPlayer.create(this, R.raw.button_sound);
            themeSound = MediaPlayer.create(requireContext(), R.raw.spyro_theme);
            //Reproducimos audio
            themeSound.start();

            //Evento para el boton Comenzar de guide
            guideBinding.buttonStart.setOnClickListener(v -> {
                needToStartGuide = false;
                //Deshabilitamos la recyclerView
                disableRecyclerView();
                //Paramos el audio
                themeSound.stop();
                themeSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release(); // Libera los recursos cuando termine la reproducción
                        themeSound = null;
                    }
                });

                //Reproducimos el audio del botón
                //soundButton.start();
                soundPool.play(buttonSoundInt, 1, 1, 0, 0, 1);

                //Ocultamos la guia guide
                guideBinding.guideLayout.setVisibility(View.GONE);


                //Deshabilita el menu about
                ((MainActivity) requireActivity()).bloquearMenuActionBar(true);

                //Iniciamos la animacion
                animation();

            });// fin listener buttonStart

            //Evento al pulsar el botón saltar guia de guide
            guideBinding.hopGuide.setOnClickListener(v -> {
                needToStartAnimation = false;
                needToStartGuide = false;
                //Ocultamos la guía.
                guideBinding.guideLayout.setVisibility(View.GONE);
                //Marcamos la guía como incompletada asi cuando reiniciemos la app,
                //nos permitira iniciar de nuevo la guía.

                marcarAbandonado();
                Toast.makeText(requireContext(), "Has Abandonado la Guia!!!!.", Toast.LENGTH_SHORT).show();
                //Habilitar el bottomNavigation
                ((MainActivity) requireActivity()).restartBottomNavigation();

                //Habilitar el menu about
                // bloquearMenuActionBar(false);
                ((MainActivity) requireActivity()).bloquearMenuActionBar(false);
                showConfirmationDialog();
            });


        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (soundPool != null) {
            soundPool.release();  // Libera la memoria ocupada por SoundPool
            soundPool = null;
        }
        binding = null;
        guideBinding = null;
    }

    private void loadCharacters() {
        try {
            // Cargamos el archivo XML desde res/xml (NOTA: ahora se usa R.xml.characters)
            InputStream inputStream = getResources().openRawResource(R.raw.characters);

            // Crear un parser XML
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            Character currentCharacter = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if ("character".equals(tagName)) {
                            currentCharacter = new Character();
                        } else if (currentCharacter != null) {
                            if ("name".equals(tagName)) {
                                currentCharacter.setName(parser.nextText());
                            } else if ("description".equals(tagName)) {
                                currentCharacter.setDescription(parser.nextText());
                            } else if ("image".equals(tagName)) {
                                currentCharacter.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();

                        if ("character".equals(tagName) && currentCharacter != null) {
                            charactersList.add(currentCharacter);
                        }
                        break;
                }

                eventType = parser.next();
            }

            adapter.notifyDataSetChanged(); // Notificamos al adaptador que los datos han cambiado
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animation() {//26/02 mañana
        if (needToStartAnimation) {
            resetAbandoned();
            Log.d("FRAGMENT CHARACTERS", "dentro animation GUIA ABANDONADA 2" + guidePreferences.isGuideAbandoned2());
            //Deshabilitar la recyclerView y bottomNavigation
            disableRecyclerView();
            ((MainActivity) requireActivity()).blokedBottomNavigation();
            guideCharactersBinding.guideCharactersLayout.setVisibility(View.VISIBLE);// ya esta visible
            //Se inicia la animación del bottomNavigation
            ((MainActivity) requireActivity()).drawCircle(R.id.nav_characters, R.drawable.circle_bottom_navigation);

            //Le vamos a dar al bocadillo animación
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(
                    guideCharactersBinding.characterMessage, "alpha", 0f, 1f);

            fadeIn.setRepeatCount(3);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(fadeIn);
            animatorSet.setDuration(1000);
            animatorSet.start();
            //guideCharactersBinding.charactersNextButton.setVisibility(View.VISIBLE);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // if (needToStartGuide) {
                    super.onAnimationEnd(animation);
                    // guideBinding.guideLayout.setVisibility(View.GONE);
                    guideCharactersBinding.charactersNextButton.setVisibility(View.VISIBLE);
                    enableRecyclerView();


                }
            });


        }

    }//fin metodo animation


    private void startFireAnimation(View dragonView) {
        dragonFireSound.start();
        // Crear la vista de fuego
        FireView fireView = new FireView(getContext());
        fireView.setLayoutParams(new FrameLayout.LayoutParams(200, 200)); // Tamaño ajustable

        // Posicionar el fuego en la boca del dragón
        int[] location = new int[2];
        dragonView.getLocationOnScreen(location);
        fireView.setX(location[0] + dragonView.getWidth() / 2 - 100); // Ajustar posición
        fireView.setY(location[1] + dragonView.getHeight() / 2 - 350); // Ajustar posición

        animationContainer.addView(fireView);

        // Eliminar el fuego después de 2 segundos
        new Handler().postDelayed(() -> animationContainer.removeView(fireView), 3000);

    }


}