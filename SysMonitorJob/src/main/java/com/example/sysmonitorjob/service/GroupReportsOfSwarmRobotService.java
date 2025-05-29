package com.example.sysmonitorjob.service;

import java.util.Map;

/**
 * @author sjn
 * @date 2025/5/28 13:35
 */
public interface GroupReportsOfSwarmRobotService {
    String systemResourceNoticeByMarkDown(Map<String, Object> data) throws Exception;
}
