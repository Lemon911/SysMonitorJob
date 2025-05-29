package com.example.sysmonitorjob.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @date 2025/5/28 11:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CpuInfo {
    private long total_threads;//总线程数
    private long total_metrics;//总句柄数
    private String ip;//ip地址
    private double cpu_usage;//cpu使用率
}
