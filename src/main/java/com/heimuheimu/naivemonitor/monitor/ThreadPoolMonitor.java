/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 heimuheimu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.heimuheimu.naivemonitor.monitor;

import com.heimuheimu.naivemonitor.util.MonitorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程池信息监控器，对已注册的 {@code ThreadPoolExecutor} 进行监控，可提供当前活跃线程数近似值总和、核心线程数总和、最大线程数总和、
 * 当前线程数总和、历史最大线程数总和、拒绝执行的任务总数等信息。
 *
 * <p><strong>说明：</strong>{@code ThreadPoolMonitor} 类是线程安全的，可在多个线程中使用同一个实例。</p>
 *
 * @see ThreadPoolExecutor
 * @see com.heimuheimu.naivemonitor.falcon.support.AbstractThreadPoolDataCollector
 * @author heimuheimu
 */
public class ThreadPoolMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolMonitor.class);

    /**
     * 监控器正在监控的线程池列表
     */
    private final CopyOnWriteArrayList<ThreadPoolExecutor> currentThreadPoolList = new CopyOnWriteArrayList<>();

    /**
     * 线程池抛出 {@link RejectedExecutionException} 异常总次数
     */
    private final AtomicLong rejectedCount = new AtomicLong();

    /**
     * 将该线程池加入到监控的线程池列表中，当该线程池关闭后，会自动从监控的线程池列表中移除。
     *
     * @param executor 需要进行监控的线程池
     */
    public void register(ThreadPoolExecutor executor) {
        if (executor != null) {
            currentThreadPoolList.add(executor);
        }
    }

    /**
     * 对线程池抛出 {@link RejectedExecutionException} 异常的操作进行监控。
     */
    public void onRejected() {
        MonitorUtil.safeAdd(rejectedCount, 1);
    }

    /**
     * 获得线程池拒绝执行的任务总数。
     *
     * @return 线程池拒绝执行的任务总数
     */
    public long getRejectedCount() {
        return rejectedCount.get();
    }

    /**
     * 获得所有线程池中当前活跃线程数近似值总和。
     *
     * @return 所有线程池中当前活跃线程数近似值总和
     * @see ThreadPoolExecutor#getActiveCount()
     */
    public int getActiveCount() {
        int activeCount = 0;
        try {
            for (ThreadPoolExecutor executor : currentThreadPoolList) {
                if (executor != null) {
                    if (!executor.isShutdown()) {
                        activeCount += executor.getActiveCount();
                    } else {
                        currentThreadPoolList.remove(executor);
                    }
                }
            }
            return activeCount;
        } catch (Exception e) {
            //should not happen
            LOGGER.error("Unexpected error. Current thread pool list: `" + currentThreadPoolList + "`.", e);
            return -1;
        }
    }

    /**
     * 获得所有线程池配置的核心线程数总和。
     *
     * @return 所有线程池配置的核心线程数总和
     * @see ThreadPoolExecutor#getCorePoolSize()
     */
    public int getCorePoolSize() {
        int corePoolSize = 0;
        try {
            for (ThreadPoolExecutor executor : currentThreadPoolList) {
                if (executor != null) {
                    if (!executor.isShutdown()) {
                        corePoolSize += executor.getCorePoolSize();
                    } else {
                        currentThreadPoolList.remove(executor);
                    }
                }
            }
            return corePoolSize;
        } catch (Exception e) {
            //should not happen
            LOGGER.error("Unexpected error. Current thread pool list: `" + currentThreadPoolList + "`.", e);
            return -1;
        }
    }

    /**
     * 获得所有线程池配置的最大线程数总和。
     *
     * @return 所有线程池配置的最大线程数总和
     * @see ThreadPoolExecutor#getMaximumPoolSize()
     */
    public int getMaximumPoolSize() {
        int maximumPoolSize = 0;
        try {
            for (ThreadPoolExecutor executor : currentThreadPoolList) {
                if (executor != null) {
                    if (!executor.isShutdown()) {
                        maximumPoolSize += executor.getMaximumPoolSize();
                    } else {
                        currentThreadPoolList.remove(executor);
                    }
                }
            }
            return maximumPoolSize;
        } catch (Exception e) {
            //should not happen
            LOGGER.error("Unexpected error. Current thread pool list: `" + currentThreadPoolList + "`.", e);
            return -1;
        }
    }

    /**
     * 获得所有线程池当前线程数总和。
     *
     * @return 所有线程池当前线程数总和
     * @see ThreadPoolExecutor#getPoolSize()
     */
    public int getPoolSize() {
        int poolSize = 0;
        try {
            for (ThreadPoolExecutor executor : currentThreadPoolList) {
                if (executor != null) {
                    if (!executor.isShutdown()) {
                        poolSize += executor.getPoolSize();
                    } else {
                        currentThreadPoolList.remove(executor);
                    }
                }
            }
            return poolSize;
        } catch (Exception e) {
            //should not happen
            LOGGER.error("Unexpected error. Current thread pool list: `" + currentThreadPoolList + "`.", e);
            return -1;
        }
    }

    /**
     * 获得所有线程池出现过的最大线程数总和。
     * <p>注意：不同线程池出现最大线程数时间可能不一致，此数据仅做参考。</p>
     *
     * @return 所有线程池出现过的最大线程数总和
     * @see ThreadPoolExecutor#getLargestPoolSize()
     */
    public int getPeakPoolSize() {
        int peakPoolSize = 0;
        try {
            for (ThreadPoolExecutor executor : currentThreadPoolList) {
                if (executor != null) {
                    if (!executor.isShutdown()) {
                        peakPoolSize += executor.getLargestPoolSize();
                    } else {
                        currentThreadPoolList.remove(executor);
                    }
                }
            }
            return peakPoolSize;
        } catch (Exception e) {
            //should not happen
            LOGGER.error("Unexpected error. Current thread pool list: `" + currentThreadPoolList + "`.", e);
            return -1;
        }
    }

    @Override
    public String toString() {
        return "ThreadPoolMonitor{" +
                "currentThreadPoolList=" + currentThreadPoolList +
                ", rejectedCount=" + rejectedCount +
                '}';
    }
}
