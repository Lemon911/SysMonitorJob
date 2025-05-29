package com.example.sysmonitorjob.controller;

import com.example.sysmonitorjob.service.CpuMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @date 2025/5/28 10:32
 */
@RestController
@RequestMapping("/init")
public class InitController {
    @Autowired
    private CpuMonitorService cpuMonitorService;

    @GetMapping("/test")
    public String test() {
        return null;
    }
}
