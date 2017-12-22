package hong.monitor.base;

import android.content.Context;

import hong.monitor.HDMonitor;

/**
 * Created by HONGDA on 2017/12/20.
 */

public abstract class BaseBuilder {

    public abstract BaseBuilder setCpuFrequency(long TimeMilliseconds);

    public abstract BaseBuilder setFpsFrequency(long TimeMilliseconds);

    public abstract BaseBuilder setMemFrequency(long TimeMilliseconds);

    public abstract BaseBuilder setNetFrequency(long TimeMilliseconds);

    public abstract BaseBuilder setContext(Context context);

    public abstract HDMonitor build();

}
