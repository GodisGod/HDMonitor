package hong.monitor;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

class MonitorTouchListener implements View.OnTouchListener {

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

    private WindowManager.LayoutParams paramsF;
    private WindowManager windowManager;

    MonitorTouchListener(WindowManager.LayoutParams paramsF,
                         WindowManager windowManager) {
        this.windowManager = windowManager;
        this.paramsF = paramsF;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = paramsF.x;
                initialY = paramsF.y;
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                windowManager.updateViewLayout(v, paramsF);
                break;
        }
        return false;
    }

}
