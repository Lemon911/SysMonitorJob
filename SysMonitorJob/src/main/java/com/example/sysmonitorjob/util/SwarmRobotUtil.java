package com.example.sysmonitorjob.util;


import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Base64;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 群机器人工具类
 */

public class SwarmRobotUtil {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // 配置的群机器人Webhook地址
    private final String toUrl;
    // 配置代理服务器
    private String hostName;
    private int port;

    /**
     * 构造方法
     *
     * @param toUrl    群机器人url地址
     * @param hostName 代理主机地址
     * @param port     端口号
     */
    public SwarmRobotUtil(String toUrl, String hostName, int port) {
        this.toUrl = toUrl;
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * 构造方法
     *
     * @param toUrl   群机器人url地址
     * @param byProxy 是否需要使用代理
     */
    public SwarmRobotUtil(String toUrl, boolean byProxy) {
        this.toUrl = toUrl;
        if (byProxy) {
            hostName = System.getProperty("proxyHost");
            port = Integer.parseInt(System.getProperty("proxyPort"));
        }
    }


    /**
     * 群机器人
     * 呼叫对应群机器人，发送指定信息
     *
     * @param reqBody 发送的内容
     */
    public String callWeChatBot(String reqBody) throws Exception {
        log.info("请求参数：" + reqBody);

        // 构造RequestBody对象，用来携带要提交的数据；需要指定MediaType，用于描述请求/响应 body 的内容类型
        MediaType contentType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(contentType, reqBody);

        // 调用群机器人
        String respMsg = okHttp(body, toUrl);

        if ("0".equals(respMsg.substring(11, 12))) {
            log.info("向群发送消息成功！");
        } else {
            log.info("请求失败！");
            // 发送错误信息到群
            sendTextMsg("群机器人推送消息失败，错误信息：\n" + respMsg);
        }
        return respMsg;
    }


    /**
     * @param body 携带需要提交的数据
     * @param url  请求地址
     * @return
     * @throws Exception
     */
    public String okHttp(RequestBody body, String url) throws Exception {
        // 构造和配置OkHttpClient
        OkHttpClient client;
        if (hostName != null && port != 0) {
            client = new OkHttpClient.Builder()
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostName, port))) // 内网使用代理，不需要可注释
                    .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间
                    .readTimeout(20, TimeUnit.SECONDS) // 设置读取超时时间
                    .build();
        } else {
            client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间
                    .readTimeout(20, TimeUnit.SECONDS) // 设置读取超时时间
                    .build();
        }

        // 构造Request对象
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("cache-control", "no-cache") // 响应消息不缓存
                .addHeader("Content-Type", "application/json")
                .build();

        // 构建Call对象，通过Call对象的execute()方法提交异步请求
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 请求结果处理
        byte[] data = response.body().bytes();
        String respMsg = new String(data);
        log.info("返回结果：" + respMsg);

        return respMsg;
    }


    /**
     * 发送文字消息
     *
     * @param msg 需要发送的消息
     * @return
     * @throws Exception
     */
    public String sendTextMsg(String msg) throws Exception {
        JSONObject text = new JSONObject();
        text.put("content", msg);
        JSONObject reqBody = new JSONObject();
        reqBody.put("msgtype", "text");
        reqBody.put("text", text);
        reqBody.put("safe", 0);

        return callWeChatBot(reqBody.toString());
    }

    /**
     * 发送图片消息，需要对图片进行base64编码并计算图片的md5值
     *
     * @param path 需要发送的图片路径
     * @return
     * @throws Exception
     */
    public String sendImgMsg(String path) throws Exception {

        String base64 = "";
        String md5 = "";

        // 获取Base64编码
        try {
            FileInputStream inputStream = new FileInputStream(path);
            byte[] bs = new byte[inputStream.available()];
            inputStream.read(bs);
            base64 = Base64.getEncoder().encodeToString(bs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 获取md5值
        try {
            FileInputStream inputStream = new FileInputStream(path);
            byte[] buf = new byte[inputStream.available()];
            inputStream.read(buf);
            md5 = DigestUtils.md5Hex(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject image = new JSONObject();
        image.put("base64", base64);
        image.put("md5", md5);
        JSONObject reqBody = new JSONObject();
        reqBody.put("msgtype", "image");
        reqBody.put("image", image);
        reqBody.put("safe", 0);

        return callWeChatBot(reqBody.toString());
    }

    /**
     * 发送MarKDown消息
     *
     * @param msg 需要发送的消息
     * @return
     * @throws Exception
     */
    public String sendMarKDownMsg(String msg) throws Exception {
        JSONObject markdown = new JSONObject();
        markdown.put("content", msg);
        JSONObject reqBody = new JSONObject();
        reqBody.put("msgtype", "markdown");
        reqBody.put("markdown", markdown);
        reqBody.put("safe", 0);
        return callWeChatBot(reqBody.toString());
    }

    /**
     * 发送file文件信息
     *
     * @param path 文件路劲
     * @return
     * @throws Exception
     */
    public String sendFileMessage(String path) throws Exception {
        File file = new File(path);
        // 构造RequestBody对象，用来携带要提交的数据；需要指定MediaType，用于描述请求/响应 body 的内容类型
        MediaType contentType = MediaType.parse("application/form-data; boundary");
        RequestBody body = RequestBody.create(contentType, file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), body)
                .build();

        // 上传到临时素材
        String key = toUrl.substring(toUrl.indexOf("key="));
        System.out.println(key);
        String mediaUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/upload_media?type=file&" + key;
        log.info("将文件" + path + "上传到临时素材：" + mediaUrl);
        String respMsg = okHttp(requestBody, mediaUrl);

        // 获取临时素材id
        JSONObject result = JSONObject.parseObject(respMsg);
        String media_id = result.getString("media_id");

        JSONObject fileJson = new JSONObject();
        fileJson.put("media_id", media_id);
        JSONObject reqBody = new JSONObject();
        reqBody.put("msgtype", "file");
        reqBody.put("file", fileJson);
        reqBody.put("safe", 0);

        // 调用群机器人发送消息
        return callWeChatBot(reqBody.toString());
    }

    /**
     * 发送图文类型消息
     *
     * @param msg
     * @return
     * @throws Exception
     */
    public String sendNewsMessage(List<JSONObject> msg) throws Exception {
        JSONObject[] jsonObjects = new JSONObject[msg.size()];
        for (int i = 0; i < msg.size(); i++) {
            jsonObjects[i] = msg.get(i);
        }
        JSONObject news = new JSONObject();
        news.put("articles", jsonObjects);
        JSONObject reqBody = new JSONObject();
        reqBody.put("msgtype", "news");
        reqBody.put("news", news);
        reqBody.put("safe", 0);

        return callWeChatBot(reqBody.toString());
    }

    //TODO 模版卡片类型(文本通知模版卡片、图文展示模版卡片)
}
