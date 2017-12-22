package com.example.test.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;


import hong.monitor.HDMonitor;
import hong.monitor.HDMonitorBuilder;


public class MainActivity extends Activity {
    static final String TAG = "LHD MainActivity";
    private HDMonitor hdMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hdMonitor = new HDMonitorBuilder()
                .setContext(MainActivity.this)
                .setCpuFrequency(1000)
                .setFpsFrequency(1000)
                .setMemFrequency(2000)
                .setNetFrequency(3000)
                .build();

        findViewById(R.id.btn_start).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hdMonitor.isMonitoring()) {
                    hdMonitor.startMonitor();
                }
            }
        });

        findViewById(R.id.remove_id).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hdMonitor != null && hdMonitor.isMonitoring()) {
                    hdMonitor.stopMonitor();
                }
            }
        });

        findViewById(R.id.save_result).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hdMonitor != null && !hdMonitor.isMonitoring()) {
                    hdMonitor.saveResultToCsvFile();
                }
            }
        });

    }


}
