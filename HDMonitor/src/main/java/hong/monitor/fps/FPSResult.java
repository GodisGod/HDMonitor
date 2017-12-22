package hong.monitor.fps;

/**
 * Created by HONGDA on 2017/12/19.
 */


public class FPSResult {
    public enum Metric {GOOD, BAD, MEDIUM}

    public long value;
    public Metric metric;

    public FPSResult(long value, Metric metric) {
        this.value = value;
        this.metric = metric;
    }
}
