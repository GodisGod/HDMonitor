package hong.monitor;

import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hong.monitor.base.MonitorDataListener;
import hong.monitor.cpu.CpuUsageView;
import hong.monitor.fps.FPSResult;
import hong.monitor.result.ResultBean;
import hong.monitor.result.ResultBeanDao;
import hong.monitor.utils.CsvUtil;


class MonitorView implements MonitorDataListener {

    private TextView fpsText, cpuText, pssText, privateDirtyText, netState;
    private WindowManager windowManager;
    private CpuUsageView cpuUsageView;
    private View monitorView;

    private List<String> cpus = new ArrayList<>();
    private List<String> allCpus = new ArrayList<>();
    private List<String> fpss = new ArrayList<>();
    private List<String> psss = new ArrayList<>();
    private List<String> privateDirtys = new ArrayList<>();
    private List<String> nets = new ArrayList<>();
    private List<ResultBean> resultBeens = new ArrayList<>();

    private boolean isMonitoring = false;

    MonitorView(Context context) {
        monitorView = LayoutInflater.from(context).inflate(R.layout.view_meter, null);
        fpsText = (TextView) monitorView.findViewById(R.id.fps);
        onShowFPS(new FPSResult(60, FPSResult.Metric.GOOD));
        cpuText = (TextView) monitorView.findViewById(R.id.cpu);
        onShowCPU("0%");
        pssText = (TextView) monitorView.findViewById(R.id.mem_pss);
        onShowPss(0);
        privateDirtyText = (TextView) monitorView.findViewById(R.id.mem_private_dirty);
        onShowPrivateDirty(0);
        netState = (TextView) monitorView.findViewById(R.id.net_state);
        onShowNetState("0 k/s");
        cpuUsageView = (CpuUsageView) monitorView.findViewById(R.id.cpu_state);
        // grab window manager and add view to the window
        windowManager = (WindowManager) monitorView.getContext().getSystemService(Service.WINDOW_SERVICE);

        cpuUsageView.setMonitorDataListener(this);
    }

    public void showMonitorView() {
        isMonitoring = true;
        addViewToWindow(monitorView);
    }

    private void addViewToWindow(View view) {
        WindowManager.LayoutParams paramsF = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        // configure starting coordinates
        paramsF.x = 0;
        paramsF.y = 0;
        paramsF.gravity = Gravity.TOP | Gravity.START;
        // add view to the window
        windowManager.addView(view, paramsF);
        // attach touch listener
        view.setOnTouchListener(new hong.monitor.MonitorTouchListener(paramsF, windowManager));
        // disable haptic feedback
        view.setHapticFeedbackEnabled(false);
    }

    public void removeViewFromWindow() {
        if (monitorView != null) {
            isMonitoring = false;
            windowManager.removeView(monitorView);
        }
    }

    public boolean isMonitoring() {
        return isMonitoring;
    }

    @Override
    public void onShowFPS(FPSResult fps) {
        fpss.add(fps.value + "");
        switch (fps.metric) {
            case GOOD:
                fpsText.setTextColor(Color.GREEN);
                break;
            case MEDIUM:
                fpsText.setTextColor(Color.YELLOW);
                break;
            case BAD:
                fpsText.setTextColor(Color.RED);
                break;
            default:
                break;
        }
        fpsText.setText(String.format("FPS : %d", fps.value));
    }


    @Override
    public void onShowCPU(final String usage) {
        cpus.add(usage + "%");
        cpuText.post(new Runnable() {
            @Override
            public void run() {
                cpuText.setText(String.format("CPU : %s", usage));
            }
        });
    }

    @Override
    public void onShowPss(final long pss) {
        psss.add(pss / 1024 + "m");
        pssText.post(new Runnable() {
            @Override
            public void run() {
                pssText.setText(String.format("Pss : %dm", pss / 1024));
            }
        });
    }

    @Override
    public void onShowPrivateDirty(final long privateDirty) {
        privateDirtys.add(privateDirty / 1024 + "m");
        privateDirtyText.post(new Runnable() {
            @Override
            public void run() {
                privateDirtyText.setText(String.format("PrivateDirty : %dm", privateDirty / 1024));
            }
        });
    }

    @Override
    public void onShowNetState(final String state) {
        nets.add(state);
        netState.post(new Runnable() {
            @Override
            public void run() {
                netState.setText("net:" + state);
            }
        });
    }

    @Override
    public void onShowCpuSate(float cpuUsage) {
        allCpus.add("" + Math.round(cpuUsage) + "%");
        Log.i("LHD", "CPU总的使用率: " + cpuUsage);
    }

    public void saveMonitorResult(ResultBeanDao resultBeanDao) {
        int i = cpus.size() > allCpus.size() ? cpus.size() : allCpus.size();
        int j = psss.size() > privateDirtys.size() ? psss.size() : privateDirtys.size();
        int k = fpss.size() > nets.size() ? fpss.size() : nets.size();

        int z = i > j ? i : j;
        int result = z > k ? z : k;
        Log.i("LHD", "cpus " + cpus.size() + "  allCpu: " + allCpus.size() + "  pss: " + psss.size() + "  fps: " + fpss.size());
        Log.i("LHD", "nets: " + nets.size() + " privateDirtys: " + privateDirtys.size());
        resultBeens.clear();
        for (int m = 0; m < result; m++) {
            ResultBean resultBean = new ResultBean();
            if (m < cpus.size()) {
                resultBean.setPidCpu(cpus.get(m));
            }

            if (m < allCpus.size()) {
                resultBean.setTotalCpu(allCpus.get(m));
            }

            if (m < fpss.size()) {
                resultBean.setFps(fpss.get(m));
            }

            if (m < nets.size()) {
                resultBean.setNet(nets.get(m));
            }

            if (m < psss.size()) {
                resultBean.setPss(psss.get(m));
            }

            if (m < privateDirtys.size()) {
                resultBean.setPrivateDirty(privateDirtys.get(m));
            }
            resultBeens.add(resultBean);
        }
        Log.i("LHD", "save result");
//        resultBeanDao.saveInTx(resultBeens);

        CsvUtil.open();
        for (ResultBean rr :
                resultBeens) {
            Log.i("LHD", rr.getId() + " " + rr.getPidCpu() + " " + rr.getTotalCpu() + " " + rr.getFps() + "  " + rr.getNet() + "  " + rr.getPss() + "  " + rr.getPrivateDirty());
            CsvUtil.writeCsv(rr.getId(),
                    rr.getPidCpu(), rr.getTotalCpu(), rr.getFps(),
                    rr.getNet(), rr.getPss(), rr.getPrivateDirty());
        }
        CsvUtil.flush();

    }

}