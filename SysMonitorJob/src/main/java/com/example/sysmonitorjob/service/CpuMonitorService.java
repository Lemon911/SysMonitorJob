package com.example.sysmonitorjob.service;

import com.example.sysmonitorjob.entity.CpuInfo;
import org.springframework.stereotype.Service;

/**
 * @author sjn
 * @date 2025/5/28 10:35
 */

public interface CpuMonitorService {
    CpuInfo getCpuTotalThreadsAndHandles();

    long getCpuThreads();

    long getCpuMetrics();
}
