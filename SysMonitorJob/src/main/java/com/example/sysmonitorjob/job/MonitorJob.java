package com.example.sysmonitorjob.job;

import com.example.sysmonitorjob.entity.CpuInfo;
import com.example.sysmonitorjob.service.Impl.CpuMonitorServiceImpl;
import com.example.sysmonitorjob.service.Impl.GroupReportsOfSwarmRobotServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @date 2025/5/28 13:13
 */
@Component
@EnableScheduling
public class MonitorJob {

    @Autowired
    private CpuMonitorServiceImpl cpuMonitorService;
    @Autowired
    private GroupReportsOfSwarmRobotServiceImpl groupReportsOfSwarmRobotService;

    @Scheduled(cron = "0 0/10 * * * ?")//每十分钟执行一次
    public void getCpuInfoJob() {
        try {
            CpuInfo cpuTotalThreadsAndHandles = cpuMonitorService.getCpuTotalThreadsAndHandles();
            String ip = cpuMonitorService.getIP();
            double cpuUsage = cpuMonitorService.getCpuUsage();
            cpuTotalThreadsAndHandles.setCpu_usage(cpuUsage);
            //System.out.println("ip:" + ip + "  cpuTotalThreadsAndHandles:" + cpuTotalThreadsAndHandles);
            //System.out.println("Cpu使用率：%.2f%%\n" + cpuUsage );
            HashMap<String, Object> map = new HashMap<>();
            map.put("ip", ip);
            map.put("cpuInfo", cpuTotalThreadsAndHandles);
            groupReportsOfSwarmRobotService.systemResourceNoticeByMarkDown(map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
