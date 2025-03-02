package dam.pmdm.spyrothedragon;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class GuidePreferences {
    private static final String PREF_NAME = "GuidePrefs";
    private static final String GUIDE_COMPLETED = "GuideCompleted";
    private static final String GUIDE_ABANDONED1 = "GuideUncompleted1";
    private static final String GUIDE_ABANDONED2 = "GuideUncompleted2";
    private static final String GUIDE_ABANDONED3 = "GuideUncompleted3";
    private static final String SCREEN_PREFIX = "Screen_";
    private static final String DESACTIVE_GUIDE = "GuideDesactive";

    private SharedPreferences sharedPreferences;

    public GuidePreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

    }


    //Metodo para resetear todas las preferencias
    public void resetGuidePreferences() {
        //Borramos todas las preferencias anteriores
        sharedPreferences.edit().clear().apply();

    }

    // Guardar que una pantalla específica ha sido vista
    public void setScreenViewed(int screenNumber) {
        sharedPreferences.edit().putBoolean(SCREEN_PREFIX + screenNumber, true).apply();
    }

    // Verificar si una pantalla fue vista
    public boolean isScreenViewed(int screenNumber) {
        return sharedPreferences.getBoolean(SCREEN_PREFIX + screenNumber, false);
    }

    // Saber si la guía se ha abandonado
    public boolean isGuideAbandoned0() {
        return sharedPreferences.getBoolean(GUIDE_ABANDONED3, false);
    }

    // Marcar la guía como abandonada
    public void setGuideAbandoned0() {
        sharedPreferences.edit().putBoolean(GUIDE_ABANDONED3, true).apply();
    }

    //Resetear si se ha abandonado la guia
    public void setResetGuideAbandoned0() {
        sharedPreferences.edit().putBoolean(GUIDE_ABANDONED3, false).apply();
    }

    // Saber si la guía se ha abandonado
    public boolean isGuideAbandoned1() {
        return sharedPreferences.getBoolean(GUIDE_ABANDONED1, false);
    }

    // Marcar la guía como abandonada
    public void setGuideAbandoned1() {
        sharedPreferences.edit().putBoolean(GUIDE_ABANDONED1, true).apply();
    }

    //Resetear si se ha abandonado la guia
    public void setResetGuideAbandoned1() {
        sharedPreferences.edit().putBoolean(GUIDE_ABANDONED1, false).apply();
    }

    // Saber si la guía se ha abandonado
    public boolean isGuideAbandoned2() {
        return sharedPreferences.getBoolean(GUIDE_ABANDONED2, false);
    }

    // Marcar la guía como abandonada
    public void setGuideAbandoned2() {
        sharedPreferences.edit().putBoolean(GUIDE_ABANDONED2, true).apply();
    }

    //Resetear si se ha abandonado la guia
    public void setResetGuideAbandoned2() {
        sharedPreferences.edit().putBoolean(GUIDE_ABANDONED2, false).apply();
    }

    // Saber si la guía se ha abandonado
    public boolean isGuideAbandoned3() {
        return sharedPreferences.getBoolean(GUIDE_ABANDONED3, false);
    }

    // Marcar la guía como abandonada
    public void setGuideAbandoned3() {
        sharedPreferences.edit().putBoolean(GUIDE_ABANDONED3, true).apply();
    }

    //Resetear si se ha abandonado la guia
    public void setResetGuideAbandoned3() {
        sharedPreferences.edit().putBoolean(GUIDE_ABANDONED3, false).apply();
    }


    // Marcar la guía como completada
    public void setGuideCompleted() {
        sharedPreferences.edit().putBoolean(GUIDE_COMPLETED, true).apply();
    }

    // Saber si la guía ya se completó
    public boolean isGuideCompleted() {
        return sharedPreferences.getBoolean(GUIDE_COMPLETED, false);
    }


    //Reiniciar la guía, reinicia el estado de la guia a false
    public void resetGuide() {
        sharedPreferences.edit().putBoolean(GUIDE_COMPLETED, false).apply();
    }

    // Marcar la guía como desactivada
    public void setGuideDesactive() {
        sharedPreferences.edit().putBoolean(DESACTIVE_GUIDE, true).apply();
    }

    // Saber si la guía se ha desactivado
    public boolean isGuideDesactive() {
        return sharedPreferences.getBoolean(DESACTIVE_GUIDE, false);
    }
}
