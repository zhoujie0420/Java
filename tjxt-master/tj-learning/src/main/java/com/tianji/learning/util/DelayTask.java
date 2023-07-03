package com.tianji.learning.util;

import lombok.Data;

import java.time.Duration;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
public class DelayTask<D> implements Delayed {

    /**
     * 任务存储的数据
     */
    private D data;

    /**
     * 用户指定的延迟时间
     */
    private Long deadlineNanos;

    /**
     * 有参构造方法，用于创建延迟任务使用
     * @param data              任务数据
     * @param deadlineNanos     任务延迟时间
     */
    public DelayTask(D data, Duration deadlineNanos) {
        this.data = data;
        this.deadlineNanos = System.nanoTime() + deadlineNanos.toNanos();
    }

    /**
     * 计算当前任务剩余延迟时间(系统总的延迟时间-已用时间)
     * @param unit  时间单位
     * @return      剩余延迟时间
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(Math.max(0, deadlineNanos - System.nanoTime()), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {

        long l = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        if (l > 0) {
            return 1;
        } else if (l < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
