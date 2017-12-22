package hong.monitor.cpu;

import android.os.Process;

import java.util.ArrayList;
import java.util.List;

import hong.monitor.base.MonitorDataListener;
import hong.monitor.base.BaseMonitor;
import hong.monitor.utils.DoubleUtils;


/**
 * Created by HONGDA on 2017/12/19.
 */

public class CpuMonitor implements BaseMonitor {

    public static final String TAG = "CpuMonitor";

    private long cpuFrequency = 1000;//取样间隔 单位ms

    private double pCpu = 0.0;
    private double aCpu = 0.0;
    private double o_pCpu = 0.0;
    private double o_aCpu = 0.0;
    private volatile boolean enabled = false;
    private int currentPid;
    private MonitorDataListener monitorDataListener;

    Runnable cpuProcessTask = new Runnable() {
        @Override
        public void run() {
            while (enabled) {
                String usage = getProcessCpuUsage(currentPid);
                if (monitorDataListener != null) {
                    monitorDataListener.onShowCPU(usage);
                }
                try {
                    Thread.sleep(cpuFrequency);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void setCpuFrequency(long cpuFrequency) {
        this.cpuFrequency = cpuFrequency;
    }

    public void setMonitorDataListener(MonitorDataListener monitorDataListener) {
        this.monitorDataListener = monitorDataListener;
    }

    @Override
    public void start() {
        if (!enabled) {
            enabled = true;
            currentPid = Process.myPid();
            new Thread(cpuProcessTask).start();
        }
    }

    @Override
    public void stop() {
        if (enabled) {
            enabled = false;
        }
    }


    public String getProcessCpuUsage(int pid) {
        String result = "";
        String[] result1 = null;
        String[] result2 = null;
        if (pid >= 0) {

            result1 = CpuUtils.getProcessCpuAction(pid);
            if (null != result1) {
                pCpu = Double.parseDouble(result1[1])
                        + Double.parseDouble(result1[2]);
            }
            result2 = CpuUtils.getCpuAction();
            if (null != result2) {
                aCpu = 0.0;
                for (int i = 2; i < result2.length; i++) {

                    aCpu += Double.parseDouble(result2[i]);
                }
            }
            double usage = 0.0;
            if ((aCpu - o_aCpu) != 0) {
                usage = DoubleUtils.div(((pCpu - o_pCpu) * 100.00),
                        (aCpu - o_aCpu), 2);
                if (usage < 0) {
                    usage = 0;
                } else if (usage > 100) {
                    usage = 100;
                }

            }
            o_pCpu = pCpu;
            o_aCpu = aCpu;
            result = String.valueOf(usage) + "%";
        }
        return result;
    }

}
