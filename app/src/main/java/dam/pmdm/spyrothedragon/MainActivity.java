package dam.pmdm.spyrothedragon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;
import dam.pmdm.spyrothedragon.databinding.GuideCharactersBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    NavController navController;
    private GuideCharactersBinding guideCharactersBinding;
    //Sharedpreferences
    private GuidePreferences guidePreferences;
    private Boolean needToStartGuide = true; //Variable booleana para comenzar o no la guía

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

            setContentView(binding.getRoot());


        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navController = NavHostFragment.findNavController(navHostFragment);
            NavigationUI.setupWithNavController(binding.navView, navController);
            NavigationUI.setupActionBarWithNavController(this, navController);
        }

        binding.navView.setOnItemSelectedListener(this::selectedBottomMenu);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_characters ||
                    destination.getId() == R.id.navigation_worlds ||
                    destination.getId() == R.id.navigation_collectibles) {
                // Para las pantallas de los tabs, no queremos que aparezca la flecha de atrás
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                // Si se navega a una pantalla donde se desea mostrar la flecha de atrás, habilítala
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });
    }


    //Metodo que navega a los distintos fragmentos, según el tab seleccionado
    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_characters)
            navController.navigate(R.id.navigation_characters);
        else if (menuItem.getItemId() == R.id.nav_worlds)
            navController.navigate(R.id.navigation_worlds);
        else
            navController.navigate(R.id.navigation_collectibles);
        return true;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gestiona el clic en el ítem de información
        if (item.getItemId() == R.id.action_info) {
            showInfoDialog();  // Muestra el diálogo
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showInfoDialog() {
        // Crear un diálogo de información
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_about)
                .setMessage(R.string.text_about)
                .setPositiveButton(R.string.accept, null)
                .show();
    }



    //Bloquea el bottomNavigation
    public void blokedBottomNavigation() {
        Log.d("BottomNav", "bloqueando el BottomNavigationView");
        Menu menu = binding.navView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setEnabled(false);
        }
    }

    //Desbloquea el bottomNavigation
    public void restartBottomNavigation() {
        Log.d("BottomNav", "desbloqueando el BottomNavigationView");
        Menu menu = binding.navView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setEnabled(true);
        }
        binding.navView.invalidate();
    }



    //Metodo que dibuja un rectángulo en el BottonNavigationView
    public void drawCircle(int itemId, int circle) {
        View itemView = binding.navView.findViewById(itemId);

        if (itemView != null) {
            // Esperar a que el layout esté listo
            itemView.post(() -> {
                // Obtener coordenadas del icono RELATIVAS AL BottomNavigationView
                int[] location = new int[2];
                itemView.getLocationOnScreen(location);  // Usamos getLocationOnScreen para coordenadas reales

                int iconCenterX = location[0] + (itemView.getWidth() / 2);
                int iconCenterY = location[1] + (itemView.getHeight() / 2);

                // Crear vista del círculo
                View rectangleView = new View(this);
                // rectangleView.setBackgroundResource(R.drawable.oval_message);
                rectangleView.setBackgroundResource(circle);

                // Agregar la vista a `binding.navView`, para asegurar que esté en la misma referencia
                ViewGroup navView = binding.navView;
                navView.addView(rectangleView);
                navView.setClipChildren(false);
                navView.setClipToPadding(false);


                // **Corrección del tamaño** - debe ser solo un poco más grande que el icono
                int rectangleSize = (int) (itemView.getWidth() * 0.7);
                int rectHeight = (int) (itemView.getHeight() * 1.2); // Aumentamos un poco
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(rectangleSize, rectHeight);


                // **Corrección de la posición**
                // FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(rectangleSize, rectangleSize);
                params.leftMargin = (itemView.getLeft() + (itemView.getWidth() / 2)) - (rectangleSize / 2);
                // params.topMargin = (itemView.getTop() + (itemView.getHeight() / 2)) - (rectangleSize / 2);
                params.topMargin -= 10;  // Subirlo 10 píxeles para que no se recorte

                rectangleView.setLayoutParams(params);
                // Crear animación para hacer que el círculo aparezca y crezca
                // ObjectAnimator animAlpha = ObjectAnimator.ofInt(paint, "alpha", 0, 255);
                ObjectAnimator animScaleX = ObjectAnimator.ofFloat(rectangleView, "scaleX", 0f, 1f);
                ObjectAnimator animScaleY = ObjectAnimator.ofFloat(rectangleView, "scaleY", 0f, 1f);

                // Configurar duración y ejecutar animación
                animScaleX.setRepeatCount(3);
                animScaleY.setRepeatCount(3);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animScaleX, animScaleY);
                animatorSet.setDuration(1000); // Duración de 1000ms
                animatorSet.start();
                Log.d("ICON_CENTER", "Centro del icono: X=" + iconCenterX + ", Y=" + iconCenterY);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // if (needToStartGuide) {
                        super.onAnimationEnd(animation);

                        // binding.characterImage.setVisibility(View.GONE);
                        rectangleView.setVisibility(View.GONE);

                    }


                });
            });

        } else {
            Log.e("ICON_CENTER", "No se encontró el ítem con ID: " + itemId);
        }

    }


    private void animation() {


        drawCircle(R.id.nav_characters, R.drawable.circle_bottom_navigation);


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


            }
        });

    }//fin método animation


   private boolean menuBloqueado = false; // Variable para controlar el bloqueo

    @Override//01/03
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menuBloqueado) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setEnabled(false); // Deshabilita los ítems
            }
        } else {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setEnabled(true); // Habilita los ítems
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void bloquearMenuActionBar(boolean bloquear) {
        menuBloqueado = bloquear;
        invalidateOptionsMenu(); // Refresca el menú para aplicar los cambios
    }


}