package hong.monitor.net;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import hong.monitor.base.BaseMonitor;
import hong.monitor.base.MonitorDataListener;

/**
 * Created by HONGDA on 2017/12/19.
 */

public class NetMonitor implements BaseMonitor {


    // 系统流量文件
    public final String DEV_FILE = "/proc/self/net/dev";

    // 流量数据
    String[] ethData = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0",
            "0", "0", "0", "0", "0"};
    String[] gprsData = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0",
            "0", "0", "0", "0", "0", "0"};
    String[] wifiData = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0",
            "0", "0", "0", "0", "0", "0"};

    // 用来存储前一个时间点的数据
    String[] data = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0",
            "0"};

    // 以太网
    final String ETHLINE = "eth0";
    // wifi
    final String WIFILINE = "wlan0";
    // gprs
    final String GPRSLINE = "rmnet0";
    private volatile boolean enabled = false;
    private MonitorDataListener monitorDataListener;
    private Context context;

    long netFrequency = 3000;

    /**
     * 定义线程周期性地获取网速
     */
    private Runnable mRunnable = new Runnable() {
        // 每3秒钟获取一次数据，求平均，以减少读取系统文件次数，减少资源消耗
        @Override
        public void run() {
            while (enabled) {
                String netState = refresh();
                Log.i("LHD", "LHD网络状况: " + netState);
                if (monitorDataListener != null) {
                    monitorDataListener.onShowNetState(netState);
                }
                try {
                    Thread.sleep(netFrequency);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void setNetFrequency(long netFrequency) {
        this.netFrequency = netFrequency;
    }

    public NetMonitor(Context context) {
        this.context = context;
    }

    public void setMonitorDataListener(MonitorDataListener monitorDataListener) {
        this.monitorDataListener = monitorDataListener;
    }

    @Override
    public void start() {
        if (!enabled) {
            new Thread(mRunnable).start();
            enabled = true;
        }
    }

    @Override
    public void stop() {
        if (enabled) {
            enabled = false;
        }
    }


    /**
     * 读取系统流量文件
     */
    public void readDev() {
        FileReader fr = null;
        try {
            fr = new FileReader(DEV_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "系统流量文件读取失败", Toast.LENGTH_SHORT).show();
            Log.i("LHD", "系统流量文件读取失败");
        }

        BufferedReader bufr = new BufferedReader(fr, 500);
        String line;
        String[] data_temp;
        String[] netData;
        int k;
        int j;
        // 读取文件，并对读取到的文件进行操作
        try {
            while ((line = bufr.readLine()) != null) {
                data_temp = line.trim().split(":");
                if (line.contains(ETHLINE)) {
                    netData = data_temp[1].trim().split(" ");
                    for (k = 0, j = 0; k < netData.length; k++) {
                        if (netData[k].length() > 0) {
                            ethData[j] = netData[k];
                            j++;
                        }
                    }
                } else if (line.contains(GPRSLINE)) {
                    netData = data_temp[1].trim().split(" ");
                    for (k = 0, j = 0; k < netData.length; k++) {
                        if (netData[k].length() > 0) {
                            gprsData[j] = netData[k];
                            j++;
                        }
                    }
                } else if (line.contains(WIFILINE)) {
                    netData = data_temp[1].trim().split(" ");
                    for (k = 0, j = 0; k < netData.length; k++) {
                        if (j < wifiData.length) {
                            try {
                                Integer.parseInt(netData[k]);
                                wifiData[j] = netData[k];
                            } catch (Exception ex) {
                                wifiData[j] = "0";
                            }
                        }
                        j++;
                    }
                }
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 实时读取系统流量文件，更新
     */
    public String refresh() {
        // 读取系统流量文件

        readDev();

        // 计算增量
        int[] delta = new int[12];
        delta[0] = Integer.parseInt(ethData[0]) - Integer.parseInt(data[0]);
        delta[1] = Integer.parseInt(ethData[1]) - Integer.parseInt(data[1]);
        delta[2] = Integer.parseInt(ethData[8]) - Integer.parseInt(data[2]);
        delta[3] = Integer.parseInt(ethData[9]) - Integer.parseInt(data[3]);
        delta[4] = Integer.parseInt(gprsData[0]) - Integer.parseInt(data[4]);
        delta[5] = Integer.parseInt(gprsData[1]) - Integer.parseInt(data[5]);
        delta[6] = Integer.parseInt(gprsData[8]) - Integer.parseInt(data[6]);
        delta[7] = Integer.parseInt(gprsData[9]) - Integer.parseInt(data[7]);
        delta[8] = Integer.parseInt(wifiData[0]) - Integer.parseInt(data[8]);
        delta[9] = Integer.parseInt(wifiData[1]) - Integer.parseInt(data[9]);
        delta[10] = Integer.parseInt(wifiData[8]) - Integer.parseInt(data[10]);
        delta[11] = Integer.parseInt(wifiData[9]) - Integer.parseInt(data[11]);

        data[0] = ethData[0];
        data[1] = ethData[1];
        data[2] = ethData[8];
        data[3] = ethData[9];
        data[4] = gprsData[0];
        data[5] = gprsData[1];
        data[6] = gprsData[8];
        data[7] = gprsData[9];
        data[8] = wifiData[0];
        data[9] = wifiData[1];
        data[10] = wifiData[8];
        data[11] = wifiData[9];

        // 每秒下载的字节数
        int traffic_data = delta[0] + delta[4] + delta[8];
        // System.out.println("每秒流量"+traffic_data);

        Log.i("LHD", "traffic_data = " + traffic_data + " 0: " + delta[0] + " 4: " + delta[4] + " delta[8]: " + delta[8]);
        if ((float) (traffic_data) / 3 > 1024 * 1024) {
            // System.out.println((float) (msg.arg1 / 1024) + "kb/s");
            Log.i("LHD", traffic_data + " 111当前网速 m/s: " + (float) (traffic_data / (1024 * 1024 * 3)) + "m/s");
            return (float) (traffic_data / (1024 * 1024 * 3)) + "m/s";
        } else {
            // System.out.println((float) (msg.arg1 / 1024) + "kb/s");
            Log.i("LHD", traffic_data + " 222当前网速 k/s: " + (float) (traffic_data / (1024 * 3)) + "k/s");
            return (float) (traffic_data / (1024 * 3)) + "k/s";
        }

    }


}
