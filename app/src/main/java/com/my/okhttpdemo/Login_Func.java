package com.my.okhttpdemo;

import android.util.Log;

import com.my.okhttpdemo.util.EncryptUtil;
import com.my.okhttpdemo.util.NetworkUtil;
import com.my.okhttpdemo.util.SignatureUtil;

import java.util.LinkedHashMap;

public class Login_Func {

    private static final String TAG = "t1";
    //登录时固定用来加密的 私钥
    private static final String secretKey = "ad974a0756d84cec80fcea72fcbfba9f";
    LinkedHashMap<String, String> params = new LinkedHashMap<>();

    public Login_Func() {
        paramsInit();
    }

    private void paramsInit() {
        params.put("v", "1.0");
        String password_rsa = EncryptUtil.encrypt("text123456");//对密码进行加密
//        Log.w(TAG, "password_rsa:" + password_rsa);
        params.put("password", password_rsa);//"2dd5fed2920a8ef8a7766c0c3875be5cb37c81f0dbbabda35fc8c7e51711be47");
        params.put("client_info", "{\"uniqid\":\"359250050014594\",\"screen\":\"1080*1776\",\"imei\":\"359250050014594\",\"other\":\",\",\"mac\":\"02:00:00:00:00:00\",\"version\":\"9.7.6\",\"os_type\":11,\"terminal_type\":2,\"from\":9000602,\"os\":\"23_46.4.4\",\"imsi\":\"null\",\"model\":\"iPhone 8\"}");
        long currentTime = System.currentTimeMillis();
        String call_id = currentTime + "";
        params.put("call_id", call_id);//"1590993274313");//时间戳
        params.put("gz", "compression");
        Log.w(TAG, "rkey:" + EncryptUtil.rkey);
        params.put("rkey", EncryptUtil.rkey);//"3afd1091ca2e47a678e0e1be8c084154");//储存的rkey
        params.put("ext_info", "{}");
        params.put("tab_sequence", "1");
        params.put("api_key", "9e1874c67e0b42d79cc16f787b644339");
        params.put("uniq_id", "359250050014594");
        params.put("user", "17683927317");
        params.put("format", "JSON");
    }

    private void sigHandle() {
        LinkedHashMap<String, String> paramsForsig = params;
        String password_rsa = params.get("password");
        paramsForsig.put("password", password_rsa.substring(0, 50));
        String client_info = params.get("client_info");
        paramsForsig.put("client_info", client_info.substring(0, 50));

        //第一次签名
        String sig = SignatureUtil.getSig(params, secretKey);

        //把第一次签名添到参数里
        paramsForsig.put("sig", sig);
        //进行第二次签名
        String sig2 = SignatureUtil.getSig(params, secretKey);
        Log.e(TAG, "sig:\n" + sig);
        Log.e(TAG, "sig2:\n" + sig2);

        //把两个签名加进网络请求的 param列表中
        params.put("sig", sig);
        params.put("sig2", sig2);
    }

    public void login() {
        final String urlStr = "http://api.m.renren.com/api/client/login";
        sigHandle();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = NetworkUtil.getInstance().submitFormdata(urlStr, params);
                Log.e(TAG, "success:\n" + response);
            }
        }).start();
    }

}
