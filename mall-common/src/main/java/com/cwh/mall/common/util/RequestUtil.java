package com.cwh.mall.common.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 请求工具类
 * 例如获取请求中ip等功能
 * @author cwh
 * @date 2021/7/20 15:25
 */
public class RequestUtil {
    /**
     *多个ip地址中的逗号分隔符
     */
    private static final String DOT_SEPERATOR = ",";
    /**
     * localhost的ip地址
     */
    private static final String LOCAL_HOST1 = "127.0.0.1";
    private static final String LOCAL_HOST2 = "0:0:0:0:0:0:0:1";
    /**
     * 通过header获取ip时可能返回unknown，表示没有该header
     */
    private static final String UNKNOWN = "unknown";


    /**
     * 获取请求的ip
     * @param request
     * @return
     */
    public static String getRequestIp(HttpServletRequest request){

        //如果通过代理服务器则会添加该header
        String ipAddress = request.getHeader("x-forwarded-for");
        //一层层测试是否为代理过后的ip
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        //非代理情况，直接调用getRemoteAddr()
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            // 从本地访问时根据网卡取本机配置的IP
            if (LOCAL_HOST1.equals(ipAddress) || LOCAL_HOST2.equals(ipAddress)) {
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                //获取本地ip
                ipAddress = inetAddress.getHostAddress();
            }
        }

        //多级代理情况即返回多个ip,以逗号连接，第一个ip则为客户端ip地址
        if(ipAddress.indexOf(DOT_SEPERATOR) > 0){
            ipAddress = ipAddress.substring(0,ipAddress.indexOf(DOT_SEPERATOR));
        }

        return ipAddress;
    }

}



