package hong.monitor;

import android.app.Application;
import android.content.Context;

import hong.monitor.base.BaseBuilder;
import hong.monitor.cpu.CpuMonitor;
import hong.monitor.fps.FPSMonitor;
import hong.monitor.memory.MemMonitor;
import hong.monitor.net.NetMonitor;

/**
 * Created by HONGDA on 2017/12/20.
 */

public class HDMonitorBuilder extends BaseBuilder {
    private FPSMonitor fpsMonitor;
    private CpuMonitor cpuMonitor;
    private MemMonitor memMonitor;
    private NetMonitor netMonitor;
    private MonitorView monitorView;

    private long cpuFrequency = 1000;
    private long fpsFrequency = 1000;
    private long memFrequency = 1000;
    private long netFrequency = 3000;
    private Context context;

    @Override
    public BaseBuilder setCpuFrequency(long TimeMilliseconds) {
        this.cpuFrequency = TimeMilliseconds;
        return this;
    }

    @Override
    public BaseBuilder setFpsFrequency(long TimeMilliseconds) {
        this.fpsFrequency = TimeMilliseconds;
        return this;
    }

    @Override
    public BaseBuilder setMemFrequency(long TimeMilliseconds) {
        this.memFrequency = TimeMilliseconds;
        return this;
    }

    @Override
    public BaseBuilder setNetFrequency(long TimeMilliseconds) {
       this.netFrequency = TimeMilliseconds;
        return this;
    }

    @Override
    public BaseBuilder setContext(Context context) {
        this.context = context;
        return this;
    }

    @Override
    public HDMonitor build() {
        monitorView = new MonitorView(context);
        fpsMonitor = new FPSMonitor();
        cpuMonitor = new CpuMonitor();
        memMonitor = new MemMonitor(context);
        netMonitor = new NetMonitor(context);

        HDMonitor hdMonitor = new HDMonitor();
        hdMonitor.setCpuFrequency(cpuFrequency);
        hdMonitor.setFpsFrequency(fpsFrequency);
        hdMonitor.setMemFrequency(memFrequency);
        hdMonitor.setNetFrequency(netFrequency);
        hdMonitor.setCpuMonitor(cpuMonitor);
        hdMonitor.setMemMonitor(memMonitor);
        hdMonitor.setFpsMonitor(fpsMonitor);
        hdMonitor.setNetMonitor(netMonitor);
        hdMonitor.setMonitorView(monitorView);

        fpsMonitor.setMonitorDataListener(monitorView);
        cpuMonitor.setMonitorDataListener(monitorView);
        memMonitor.setMonitorDataListener(monitorView);
        netMonitor.setMonitorDataListener(monitorView);

        return hdMonitor;
    }


}
