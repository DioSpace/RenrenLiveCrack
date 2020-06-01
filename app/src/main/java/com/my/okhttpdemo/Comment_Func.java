package com.my.okhttpdemo;

import android.util.Log;

import com.my.okhttpdemo.util.NetworkUtil;
import com.my.okhttpdemo.util.SignatureUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.zip.GZIPInputStream;

public class Comment_Func {

    private static String TAG = "t1";
    private static String commentUrl = "http://api.m.renren.com/api/comment/addComment";

    //这个secretKey长期不变
    String secretKey = "e364e27bffe4316fde2f8fb1c988da18";

    public void comment(String content) {
        final LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("v", "1.0");
        long currentTime = System.currentTimeMillis();
        String call_id = currentTime + "";
        params.put("call_id", call_id);//这个时间戳 是变化的
        params.put("gz", "compression");
        params.put("log_info", "{source=comment-live}");
        params.put("uniq_key", "5cce1b992a3f65c46d53f698f0696630");
        params.put("entry_id", "2849670");
        params.put("extension", "{\"replaceUbb\":\"false\"}");
        params.put("api_key", "9e1874c67e0b42d79cc16f787b644339");
        params.put("content", content);//发送内容，可以自己设置
        params.put("owner_id", "974072986");
        params.put("session_key", "4eNxXaK6k2WhV1S9");
        params.put("type", "39");
        params.put("format", "JSON");

        String sig = SignatureUtil.getSig(params, secretKey);//对以上数据进行签名
        Log.e(TAG, "sig:\n" + sig);
        params.put("sig", sig);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = NetworkUtil.getInstance().submitFormdata(commentUrl, params);
//                showResponse(response);
                Log.e(TAG, "success:" + response);
            }
        }).start();
//        MyOkHttp.getInstance().submitFormdata(commentUrl, params, new MyOkHttp.OkHttpCallBack<String>() {
//
//            @Override
//            public void requestSuccess(String s) {
////                byte[] response1 = uncompress(s.getBytes());
////                String respone2 = new String(response1);
//                Log.e(TAG, "success:" + s);
////                Log.e(TAG, "success2:" + respone2);
////                Log.e(TAG, "success:" + s);
//            }
//
//            @Override
//            public void requestFailure(String message) {
//                Log.e(TAG, "failure:" + message);
//            }
//
//        }, String.class);
    }
}
