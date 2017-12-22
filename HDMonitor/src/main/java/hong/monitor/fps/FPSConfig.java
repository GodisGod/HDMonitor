package hong.monitor.fps;

import java.util.concurrent.TimeUnit;

import hong.monitor.base.BaseMonitor;


/**
 * Created by HONGDA on 2017/12/19.
 */


class FPSConfig {

    float refreshRate = 60; //60fps
    float deviceRefreshRateInMs = 16.6f; //设备刷新速率 单位ms

    private long fpsFrequency = 1000;
    FPSConfig(long fpsFrequency) {
        this.fpsFrequency = fpsFrequency;
    }

    long getSampleTimeInNs() {
        return TimeUnit.NANOSECONDS.convert(fpsFrequency, TimeUnit.MILLISECONDS);
    }

    long getDeviceRefreshRateInNs() {
        float value = deviceRefreshRateInMs * 1000000f;
        return (long) value;
    }
}
