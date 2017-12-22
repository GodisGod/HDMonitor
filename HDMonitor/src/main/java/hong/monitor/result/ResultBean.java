package hong.monitor.result;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by HONGDA on 2017/12/20.
 */

@Entity
public class ResultBean {
    @Id
    private Long id;

    private String pidCpu;
    private String totalCpu;
    private String fps;
    private String net;
    private String pss;
    private String privateDirty;
    @Generated(hash = 1476451597)
    public ResultBean(Long id, String pidCpu, String totalCpu, String fps,
            String net, String pss, String privateDirty) {
        this.id = id;
        this.pidCpu = pidCpu;
        this.totalCpu = totalCpu;
        this.fps = fps;
        this.net = net;
        this.pss = pss;
        this.privateDirty = privateDirty;
    }
    @Generated(hash = 2137771703)
    public ResultBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPidCpu() {
        return this.pidCpu;
    }
    public void setPidCpu(String pidCpu) {
        this.pidCpu = pidCpu;
    }
    public String getTotalCpu() {
        return this.totalCpu;
    }
    public void setTotalCpu(String totalCpu) {
        this.totalCpu = totalCpu;
    }
    public String getFps() {
        return this.fps;
    }
    public void setFps(String fps) {
        this.fps = fps;
    }
    public String getNet() {
        return this.net;
    }
    public void setNet(String net) {
        this.net = net;
    }
    public String getPss() {
        return this.pss;
    }
    public void setPss(String pss) {
        this.pss = pss;
    }
    public String getPrivateDirty() {
        return this.privateDirty;
    }
    public void setPrivateDirty(String privateDirty) {
        this.privateDirty = privateDirty;
    }

}
