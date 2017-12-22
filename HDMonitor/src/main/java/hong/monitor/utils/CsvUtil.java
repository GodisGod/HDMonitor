package hong.monitor.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HONGDA on 2017/12/21.
 */

public class CsvUtil {
    public static final String mComma = ",";
    private static StringBuilder mStringBuilder = null;
    private static String mFileName = null;
    private static String folderName = null;

    public static void open() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (path != null) {
                folderName = path + "/CSV";
                Log.i("LHD", "folderName : " + folderName);
            }
        }

        File fileRobo = new File(folderName);
        if (!fileRobo.exists()) {
            fileRobo.mkdir();
        }

        mStringBuilder = new StringBuilder();
        mStringBuilder.append("id");
        mStringBuilder.append(mComma);
        mStringBuilder.append("pidCpu");
        mStringBuilder.append(mComma);
        mStringBuilder.append("totalCpu");
        mStringBuilder.append(mComma);
        mStringBuilder.append("fps");
        mStringBuilder.append(mComma);
        mStringBuilder.append("net");
        mStringBuilder.append(mComma);
        mStringBuilder.append("pss");
        mStringBuilder.append(mComma);
        mStringBuilder.append("privateDirty");
        mStringBuilder.append("\n");


    }

    public static void writeCsv(Long id,
                                String pidCpu, String totalCpu, String fps,
                                String net, String pss, String privateDirty) {
        mStringBuilder.append(id);
        mStringBuilder.append(mComma);
        mStringBuilder.append(pidCpu);
        mStringBuilder.append(mComma);
        mStringBuilder.append(totalCpu);
        mStringBuilder.append(mComma);
        mStringBuilder.append(fps);
        mStringBuilder.append(mComma);
        mStringBuilder.append(net);
        mStringBuilder.append(mComma);
        mStringBuilder.append(pss);
        mStringBuilder.append(mComma);
        mStringBuilder.append(privateDirty);
        mStringBuilder.append("\n");
    }

    /**
     * 获取时间 小时:分 HH:mm
     *
     * @param time 毫秒
     * @return
     */
    public static String getTimeShort(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static void flush() {
        mFileName = folderName + "/" + "hdMonitor_" + getTimeShort(System.currentTimeMillis()) + "_result.csv";
        File fileMonitor = new File(mFileName);
        if (!fileMonitor.exists()) {
            try {
                fileMonitor.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mFileName != null) {
            try {
                File file = new File(mFileName);
                FileOutputStream fos = new FileOutputStream(file, false);
                Log.i("LHD", "内容: " + mStringBuilder.toString());
                fos.write(mStringBuilder.toString().getBytes());
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("You should call open() before flush()");
        }
    }

}
