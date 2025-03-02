package dam.pmdm.spyrothedragon.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FireView extends View {

    private Paint paint;
    private List<FireParticle> particles;
    private Random random;
    private boolean isAnimating = false;
    private Handler handler;

    public FireView(Context context) {
        super(context);
        init();
    }

    public FireView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        particles = new ArrayList<>();
        random = new Random();
        handler = new Handler();

        // Iniciar animación
        startAnimation();
    }

    private void startAnimation() {
        isAnimating = true;
        handler.post(animationRunnable);
    }

    private void stopAnimation() {
        isAnimating = false;
        handler.removeCallbacks(animationRunnable);
    }

    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAnimating) {
                generateParticles();
                invalidate();
                handler.postDelayed(this, 30);
            }
        }
    };

    private void generateParticles() {
        if (particles.size() < 50) { // Límite de partículas para optimizar rendimiento
            particles.add(new FireParticle(getWidth() / 2, getHeight() / 2));
        }

        Iterator<FireParticle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            FireParticle particle = iterator.next();
            particle.update();
            if (particle.alpha <= 0) {
                iterator.remove();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (FireParticle particle : particles) {
            paint.setColor(Color.argb(particle.alpha, particle.red, particle.green, 0));
            canvas.drawCircle(particle.x, particle.y, particle.size, paint);
        }
    }

    private class FireParticle {
        float x, y, size;
        int alpha, red, green;
        float velocityX, velocityY;

        FireParticle(float startX, float startY) {
            x = startX + random.nextInt(30) - 15; // Variación en X
            y = startY;
            size = random.nextInt(10) + 5; // Tamaño aleatorio
            alpha = 255;
            red = 255;
            green = random.nextInt(156) + 100; // Verde aleatorio para efecto realista
            velocityX = random.nextFloat() * 4 - 2; // Movimiento horizontal
            velocityY = -random.nextFloat() * 5 - 2; // Movimiento ascendente
        }

        void update() {
            x += velocityX;
            y += velocityY;
            alpha -= 5; // Se desvanece
            if (alpha < 0) alpha = 0;
        }
    }
}
