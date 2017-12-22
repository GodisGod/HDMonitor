package hong.monitor;

import android.content.Context;
import android.util.Log;

import hong.monitor.base.BaseMonitor;
import hong.monitor.cpu.CpuMonitor;
import hong.monitor.fps.FPSMonitor;
import hong.monitor.memory.MemMonitor;
import hong.monitor.net.NetMonitor;
import hong.monitor.result.DaoMaster;
import hong.monitor.result.DaoSession;
import hong.monitor.result.ResultBeanDao;

/**
 * Created by HONGDA on 2017/12/19.
 */

public class HDMonitor {

    private FPSMonitor fpsMonitor;
    private CpuMonitor cpuMonitor;
    private MemMonitor memMonitor;
    private NetMonitor netMonitor;
    private MonitorView monitorView;

    private Context context;
    private long cpuFrequency = 1000;
    private long fpsFrequency = 1000;
    private long memFrequency = 1000;
    private long netFrequency = 3000;

    private ResultBeanDao resultBeanDao;

    public void startMonitor() {
        Log.i("LHD", "startMonitor");
        monitorView.showMonitorView();

        fpsMonitor.setFpsFrequency(fpsFrequency);
        cpuMonitor.setCpuFrequency(cpuFrequency);
        memMonitor.setMemFrequency(memFrequency);
        netMonitor.setNetFrequency(netFrequency);

        fpsMonitor.start();
        cpuMonitor.start();
        memMonitor.start();
        netMonitor.start();

//        createGreendao(context);

    }

    private void createGreendao(Context context) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "HDMonitor.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        DaoSession daoSession = daoMaster.newSession();
        resultBeanDao = daoSession.getResultBeanDao();
    }

    //// TODO: 2017/12/20 停止监控
    public void stopMonitor() {
        Log.i("LHD", "stopMonitor");
        if (fpsMonitor == null || cpuMonitor == null || memMonitor == null || netMonitor == null)
            return;
        fpsMonitor.stop();
        cpuMonitor.stop();
        memMonitor.stop();
        netMonitor.stop();
        monitorView.removeViewFromWindow();
    }

    //todo 保存结果为csv文件
    public void saveResultToCsvFile() {
        monitorView.saveMonitorResult(resultBeanDao);
    }

    public long getCpuFrequency() {
        return cpuFrequency;
    }

    public void setCpuFrequency(long cpuFrequency) {
        this.cpuFrequency = cpuFrequency;
    }

    public long getFpsFrequency() {
        return fpsFrequency;
    }

    public void setFpsFrequency(long fpsFrequency) {
        this.fpsFrequency = fpsFrequency;
    }

    public long getMemFrequency() {
        return memFrequency;
    }

    public void setMemFrequency(long memFrequency) {
        this.memFrequency = memFrequency;
    }

    public long getNetFrequency() {
        return netFrequency;
    }

    public void setNetFrequency(long netFrequency) {
        this.netFrequency = netFrequency;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public FPSMonitor getFpsMonitor() {
        return fpsMonitor;
    }

    public void setFpsMonitor(FPSMonitor fpsMonitor) {
        this.fpsMonitor = fpsMonitor;
    }

    public CpuMonitor getCpuMonitor() {
        return cpuMonitor;
    }

    public void setCpuMonitor(CpuMonitor cpuMonitor) {
        this.cpuMonitor = cpuMonitor;
    }

    public MemMonitor getMemMonitor() {
        return memMonitor;
    }

    public void setMemMonitor(MemMonitor memMonitor) {
        this.memMonitor = memMonitor;
    }

    public NetMonitor getNetMonitor() {
        return netMonitor;
    }

    public void setNetMonitor(NetMonitor netMonitor) {
        this.netMonitor = netMonitor;
    }

    public MonitorView getMonitorView() {
        return monitorView;
    }

    public void setMonitorView(MonitorView monitorView) {
        this.monitorView = monitorView;
    }

    public boolean isMonitoring() {
        return monitorView.isMonitoring();
    }

}
