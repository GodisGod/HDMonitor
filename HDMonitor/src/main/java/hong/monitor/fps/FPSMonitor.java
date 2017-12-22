package hong.monitor.fps;


import android.view.Choreographer;

import hong.monitor.base.BaseMonitor;
import hong.monitor.base.MonitorDataListener;


public class FPSMonitor implements BaseMonitor {

    public static final String TAG = "FPSMonitor";

    private FPSConfig fpsConfig;
    private FPSFrameCallback frameCallback;
    private long fpsFrequency = 1000;
    public FPSMonitor() {
        fpsConfig = new FPSConfig(fpsFrequency);
        frameCallback = new FPSFrameCallback(fpsConfig);
    }

    public void setFpsFrequency(long fpsFrequency) {
        this.fpsFrequency = fpsFrequency;
    }

    public void setMonitorDataListener(MonitorDataListener monitorDataListener) {
        frameCallback.setMonitorDataListener(monitorDataListener);
    }

    @Override
    public void start() {
        Choreographer.getInstance().postFrameCallback(frameCallback);
    }

    @Override
    public void stop() {
        Choreographer.getInstance().removeFrameCallback(frameCallback);
    }
}