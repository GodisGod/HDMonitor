package hong.monitor.base;


import hong.monitor.fps.FPSResult;

/**
 * Created by HONGDA on 2017/12/19.
 */


public interface MonitorDataListener {
    void onShowFPS(FPSResult fps);
    void onShowCPU(String usage);
    void onShowPss(long pss);
    void onShowPrivateDirty(long privateDirty);
    void onShowNetState(String state);
    void onShowCpuSate(float cpuUsage);
}
