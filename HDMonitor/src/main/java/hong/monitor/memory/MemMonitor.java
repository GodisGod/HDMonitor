package hong.monitor.memory;

import android.content.Context;
import android.os.Process;

import hong.monitor.base.BaseMonitor;
import hong.monitor.base.MonitorDataListener;

/**
 * Created by HONGDA on 2017/12/19.
 */


public class MemMonitor implements BaseMonitor {

    public static final String TAG = "MemMonitor";

    private volatile boolean enabled = false;

    private MonitorDataListener monitorDataListener;
    private int currentPid;
    private Context context;


    long memFrequency = 1000;

    Runnable memProcessTask = new Runnable() {
        @Override
        public void run() {
            while (enabled) {
                long totalPss = getTotalPss();
                long totalPrivateDirty = getTotalPrivateDirty();
                if (monitorDataListener != null) {
                    monitorDataListener.onShowPss(totalPss);
                }
                if (monitorDataListener != null) {
                    monitorDataListener.onShowPrivateDirty(totalPrivateDirty);
                }
                try {
                    Thread.sleep(memFrequency);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    public void setMemFrequency(long memFrequency) {
        this.memFrequency = memFrequency;
    }

    @Override
    public void start() {
        if (!enabled) {
            enabled = true;
            currentPid = Process.myPid();
            new Thread(memProcessTask).start();
        }
    }

    @Override
    public void stop() {
        if (enabled) {
            enabled = false;
        }
    }

    public MemMonitor(Context context) {
        this.context = context;
    }

    public void setMonitorDataListener(MonitorDataListener monitorDataListener) {
        this.monitorDataListener = monitorDataListener;
    }

    private long getTotalPss() {
        return MemUtils.getPSS(context, currentPid)[2];
    }

    private long getTotalPrivateDirty() {
        return MemUtils.getPrivDirty(context, currentPid)[2];
    }
}
