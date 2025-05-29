package com.example.sysmonitorjob.service.Impl;


import com.example.sysmonitorjob.entity.CpuInfo;
import com.example.sysmonitorjob.service.GroupReportsOfSwarmRobotService;

import com.example.sysmonitorjob.util.SwarmRobotUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 分组表信息推送功能实现类--群机器人
 */
@Service
public class GroupReportsOfSwarmRobotServiceImpl implements GroupReportsOfSwarmRobotService {

    //微信机器人的webhook
    @Value("${swarmRobot.robotOfInformationDepart.WECHAT_GROUP}")
    private String WECHAT_GROUP;


    /**
     * 系统服务器资源告警推送提醒--群聊机器人
     *
     * @param data 推送的数据
     * @return
     */
    @Override
    public String systemResourceNoticeByMarkDown(Map<String, Object> data) throws Exception {
        String ip = (String) data.get("ip");
        String exception = null;
        CpuInfo cpuInfo = (CpuInfo) data.get("cpuInfo");
        // 创建 DecimalFormat 对象，设置格式为保留两位小数
        DecimalFormat df = new DecimalFormat("#.##");
        String description = null;
        boolean flag = false;
        int i = 0;
        if (cpuInfo.getTotal_threads() > 3500 && cpuInfo.getTotal_metrics() > 60000) {
            exception = "线程和句柄异常告警";
            flag = true;
            i = 1;
        } else if (cpuInfo.getTotal_threads() > 3500) {
            exception = "线程异常告警";
            flag = true;
            i = 2;
        } else if (cpuInfo.getTotal_metrics() > 60000) {
            exception = "句柄异常告警";
            flag = true;
            i = 3;
        } else if (cpuInfo.getCpu_usage() > 90) {
            exception = "CPU使用率异常告警";
            flag = true;
            i = 4;
        }

        if (i == 1) {
            description = "  CPU利用率：" + df.format(cpuInfo.getCpu_usage()) + "% \n" + "   <font color=\"warning\">已使用线程：" + cpuInfo.getTotal_threads() + "</font>\n" + "  <font color=\"warning\">已使用句柄：" + cpuInfo.getTotal_metrics() + "</font>";
        } else if (i == 2) {
            description = "  CPU利用率：" + df.format(cpuInfo.getCpu_usage()) + "% \n" + "   <font color=\"warning\">已使用线程：" + cpuInfo.getTotal_threads() + "</font>\n" + "  已使用句柄：" + cpuInfo.getTotal_metrics();
        } else if (i == 3) {
            description = "  CPU利用率：" + df.format(cpuInfo.getCpu_usage()) + "% \n" + "  已使用线程：" + cpuInfo.getTotal_threads() + "\n" + "  <font color=\"warning\">已使用句柄：" + cpuInfo.getTotal_metrics() + "</font>";
        } else if (i == 4) {
            description = "  <font color=\"warning\">CPU利用率：" + df.format(cpuInfo.getCpu_usage()) + "%</font> \n" + "  已使用线程：" + cpuInfo.getTotal_threads() + "\n" + "  已使用句柄：" + cpuInfo.getTotal_metrics();
        }

        //拼接机器人助手发布的内容
        String content = "【服务器-资源告警提醒】\n" +
                ">ip：" + ip + " \n " +
                ">异常类型：<font color=\"warning\">" + exception + "</font>\n" +
                ">时间：<font color=\"comment\">" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "</font>\n" +
                ">**服务器资源使用情况：**  \n" + description + "\n";
        if (flag) {
            //通知机群器人
            SwarmRobotUtil swarmRobotUtil = new SwarmRobotUtil(WECHAT_GROUP, false);
            swarmRobotUtil.sendMarKDownMsg(content);
        }
        return "success";
    }


}
