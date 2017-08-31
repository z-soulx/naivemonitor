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

package com.heimuheimu.naivemonitor.falcon.support;

import com.heimuheimu.naivemonitor.compress.CompressionInfo;
import com.heimuheimu.naivemonitor.compress.CompressionMonitor;
import com.heimuheimu.naivemonitor.falcon.FalconData;

import java.util.ArrayList;
import java.util.List;

/**
 * 压缩、解压信息采集器抽象实现
 *
 * @author heimuheimu
 */
public abstract class AbstractCompressionInfoCollector extends AbstractFalconDataCollector {

    private volatile long lastReduceBytes = 0;

    /**
     * 获得压缩、解压信息采集器所依赖的数据源
     *
     * @return 压缩、解压信息采集器所依赖的数据源
     */
    protected abstract CompressionMonitor getCompressionMonitor();


    @Override
    public List<FalconData> getList() {
        CompressionInfo compressionInfo = getCompressionMonitor().getCompressionInfo();
        List<FalconData> falconDataList = new ArrayList<>();

        long reduceBytes = compressionInfo.getPreCompressed().get() - compressionInfo.getCompressed().get();
        falconDataList.add(create("_compression_reduce_bytes", reduceBytes - lastReduceBytes));
        lastReduceBytes = reduceBytes;

        return falconDataList;
    }
}
