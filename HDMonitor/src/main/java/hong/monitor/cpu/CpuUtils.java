package hong.monitor.cpu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import hong.monitor.utils.FileUtil;


/**
 * CPU相关工具类。
 */
class CpuUtils {
    //当前进程的CPU信息
    static String[] getProcessCpuAction(int pid) {
        ///proc/<pid>/stat文件
//        该文件包含了某一进程所有的活动的信息，该文件中的所有值都是从系统启动开始累计
//        到当前时刻。
        String cpuPath = "/proc/" + pid + "/stat";
        String cpu = "";
        String[] result = new String[3];

        File f = new File(cpuPath);
        if (!f.exists() || !f.canRead()) {
            /*
             * 进程信息可能无法读取，
			 * 同时发现此类进程的PSS信息也是无法获取的，用PS命令会发现此类进程的PPid是1，
			 * 即/init，而其他进程的PPid是zygote,
			 * 说明此类进程是直接new出来的，不是Android系统维护的
			 */
            return result;
        }

        FileReader fr = null;
        BufferedReader localBufferedReader = null;

        try {
            fr = new FileReader(f);
            localBufferedReader = new BufferedReader(fr, 8192);
            cpu = localBufferedReader.readLine();
            if (null != cpu) {
                String[] cpuSplit = cpu.split(" ");
                result[0] = cpuSplit[1];
                result[1] = cpuSplit[13];
                result[2] = cpuSplit[14];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtil.closeReader(localBufferedReader);
        return result;
    }

    static String[] getCpuAction() {
        String cpuPath = "/proc/stat";
        String cpu = "";
        String[] result = new String[7];

        File f = new File(cpuPath);
        if (!f.exists() || !f.canRead()) {
            return result;
        }

        FileReader fr = null;
        BufferedReader localBufferedReader = null;

        try {
            fr = new FileReader(f);
            localBufferedReader = new BufferedReader(fr, 8192);
            cpu = localBufferedReader.readLine();
            if (null != cpu) {
                result = cpu.split(" ");

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtil.closeReader(localBufferedReader);
        return result;
    }
}