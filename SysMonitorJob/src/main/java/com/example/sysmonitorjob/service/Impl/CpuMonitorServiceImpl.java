package com.example.sysmonitorjob.service.Impl;


import com.example.sysmonitorjob.entity.CpuInfo;
import oshi.SystemInfo;
import com.example.sysmonitorjob.service.CpuMonitorService;
import org.springframework.stereotype.Service;
import oshi.hardware.CentralProcessor;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


/**
 * @date 2025/5/28 10:39
 */
@Service
public class CpuMonitorServiceImpl implements CpuMonitorService {
    @Override
    public CpuInfo getCpuTotalThreadsAndHandles() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        // 获取所有进程
        OSProcess[] processes = os.getProcesses().toArray(new OSProcess[0]);

        // 初始化统计变量
        long totalThreads = 0;
        long totalHandles = 0;

        // 遍历所有进程
        for (OSProcess process : processes) {
            // 累加线程数
            totalThreads += process.getThreadCount();

            // 累加句柄数（Windows）或文件描述符数（Linux/Mac）
            // 注意：Linux的句柄数需要用其他方式获取（此处仅作近似）
            totalHandles += process.getOpenFiles();
        }
        CpuInfo cpuInfo = new CpuInfo();
        cpuInfo.setTotal_threads(totalThreads);
        cpuInfo.setTotal_metrics(totalHandles);
        // 输出结果
        System.out.println("所有进程的线程总数: " + totalThreads);
        System.out.println("所有进程的句柄/文件描述符总数: " + totalHandles);
        return cpuInfo;
    }

    @Override
    public long getCpuThreads() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        // 获取所有进程
        OSProcess[] processes = os.getProcesses().toArray(new OSProcess[0]);
        // 初始化统计变量
        long totalThreads = 0;

        // 遍历所有进程
        for (OSProcess process : processes) {
            // 累加线程数
            totalThreads += process.getThreadCount();
        }
        return totalThreads;
    }

    @Override
    public long getCpuMetrics() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        // 获取所有进程
        OSProcess[] processes = os.getProcesses().toArray(new OSProcess[0]);
        // 初始化统计变量
        long totalHandles = 0;

        // 遍历所有进程
        for (OSProcess process : processes) {
            // 累加句柄数（Windows）或文件描述符数（Linux/Mac）
            // 注意：Linux的句柄数需要用其他方式获取（此处仅作近似）
            totalHandles += process.getOpenFiles();
        }
        return totalHandles;
    }

    //获取Ip地址
    public String getIP() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address) {
                    return addr.getHostAddress();
                }
            }
        }
        return "127.0.0.1"; // Fallback
    }

    //获取CPU使用率
    public double getCpuUsage() throws InterruptedException {
        SystemInfo si = new SystemInfo();
        CentralProcessor cpu = si.getHardware().getProcessor();
        long[] prevTicks = cpu.getSystemCpuLoadTicks();

        // 第一次采样需间隔计算
        Thread.sleep(1000);

        // 计算 CPU 利用率（0.0-1.0）
        double cpuUsage = cpu.getSystemCpuLoadBetweenTicks(prevTicks);
        System.out.printf("CPU 利用率: %.2f%%\n", cpuUsage * 100);
        return cpuUsage * 100;
    }
}



