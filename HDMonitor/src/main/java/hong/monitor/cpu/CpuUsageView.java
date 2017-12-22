
package hong.monitor.cpu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import hong.monitor.base.BaseMonitor;
import hong.monitor.base.MonitorDataListener;

//所有CPU的信息
public class CpuUsageView extends Button {
    static final String TAG = "LHD CpuUsageView";
    static final int COLUMNS = 11;
    private volatile boolean mAttached;
    private int mColor = 0xffff00e0;
    private MonitorDataListener monitorDataListener;
    float mCpuUsage = 0.0f;
    List<Float> mListUsage;//绘图使用
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                refresh();
            }
        }
    };

    public void setMonitorDataListener(MonitorDataListener monitorDataListener) {
        this.monitorDataListener = monitorDataListener;
    }

    /**
     * @param context
     */
    public CpuUsageView(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public CpuUsageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CpuUsageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setBackgroundColor(0x77005023);
        mListUsage = new LinkedList<Float>();

        for (int i = 0; i < COLUMNS; i++) {
            mListUsage.add(1.0f);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            mAttached = true;
            new Thread(runnable).start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            mAttached = false;
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (mAttached) {
                int user, nice, sys, idle, iowait, irq, softirq;
                int all1, all2, idle1, idle2;

                BufferedReader bReader = null;
                try {
                    //该文件包含了所有CPU活动的信息，该文件中的所有值都是从系统启动开始累计到当前时刻。
                    //所有CPU活动的信息
                    bReader = new BufferedReader(new FileReader("/proc/stat"));

                    String strTemp = null;
                    strTemp = bReader.readLine();

                    String[] listStrings = strTemp.split(" ");

                    user = Integer.parseInt(listStrings[2]);
                    nice = Integer.parseInt(listStrings[3]);
                    sys = Integer.parseInt(listStrings[4]);
                    idle = Integer.parseInt(listStrings[5]);
                    iowait = Integer.parseInt(listStrings[6]);
                    irq = Integer.parseInt(listStrings[7]);
                    softirq = Integer.parseInt(listStrings[8]);

                    all1 = user + nice + sys + idle + iowait + irq + softirq;
                    idle1 = idle;
                    bReader.close();

                    Thread.sleep(1000);


                    bReader = new BufferedReader(new FileReader("/proc/stat"));
                    strTemp = bReader.readLine();

                    listStrings = strTemp.split(" ");

                    user = Integer.parseInt(listStrings[2]);
                    nice = Integer.parseInt(listStrings[3]);
                    sys = Integer.parseInt(listStrings[4]);
                    idle = Integer.parseInt(listStrings[5]);
                    iowait = Integer.parseInt(listStrings[6]);
                    irq = Integer.parseInt(listStrings[7]);
                    softirq = Integer.parseInt(listStrings[8]);

                    all2 = user + nice + sys + idle + iowait + irq + softirq;
                    idle2 = idle;
                    bReader.close();

                    mCpuUsage = (float) (all2 - all1 - (idle2 - idle1)) / (all2 - all1) * 100;
                    Log.i("LHD", "CPU使用率: " + mCpuUsage);
                    synchronized (mListUsage) {
                        mListUsage.add(mCpuUsage);
                        if (mListUsage.size() > COLUMNS) {
                            mListUsage.remove(0);
                        }
                    }
                    if (monitorDataListener != null) {
                        monitorDataListener.onShowCpuSate(mCpuUsage);
                    }

                    //Log.e(TAG, "usage : " + mCpuUsage);
                    mHandler.sendEmptyMessage(1);

                } catch (Exception e1) {
                    e1.printStackTrace();

                } finally {
                    if (bReader != null) {
                        try {
                            bReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    private final void refresh() {
        setText("" + Math.round(mCpuUsage) + "%");
    }

    public void setTextColor(int color) {
        mColor = color;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(0x7700ff00);
        int width = getWidth();
        int heigth = getHeight();
        float fPecentage = (float) heigth / 100;
        List<Float> listX = new LinkedList<Float>();
        listX.add(0.0f);
        float deltaX = (float) width / 10;
        for (int i = 1; i < COLUMNS; i++) {
            listX.add(deltaX * (i));
        }

        Path path = new Path();
        path.moveTo(0, heigth);

        synchronized (mListUsage) {
            int size = mListUsage.size();
            if (size > 1) {
                for (int i = 0; i < size; i++) {
                    path.lineTo(listX.get(i), heigth - mListUsage.get(i) * fPecentage);
                }

                path.lineTo(width, heigth);
                path.moveTo(0, heigth);
                canvas.drawPath(path, paint);
            }

        }
    }

}
