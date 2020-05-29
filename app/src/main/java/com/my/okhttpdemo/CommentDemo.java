package com.my.okhttpdemo;

import android.util.Log;

import java.util.LinkedHashMap;

public class CommentDemo {

    private static String TAG = "t1";
    private static String commentUrl = "http://api.m.renren.com/api/comment/addComment";

    //这个secretKey长期不变
    String secretKey = "d787468a5646e5e0d7657893ffcb9c79";

    public void comment(String content) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("v", "1.0");
        long currentTime = System.currentTimeMillis();
        String call_id = currentTime + "";
        params.put("call_id", call_id);//这个时间戳 是变化的
        params.put("gz", "compression");
        params.put("log_info", "{source=comment-live}");
        params.put("uniq_key", "5cce1b992a3f65c46d53f698f0696630");
        params.put("entry_id", "2849275");
        params.put("extension", "{\"replaceUbb\":\"false\"}");
        params.put("api_key", "9e1874c67e0b42d79cc16f787b644339");
        params.put("content", content);//发送内容，可以自己设置
        params.put("owner_id", "967291139");
        params.put("session_key", "e2qCVFtSE2WhV1Ss");
        params.put("type", "39");
        params.put("format", "JSON");

        String sig = SignatureDemo.getSig(params, secretKey);//对以上数据进行签名
        Log.e(TAG, "sig:\n" + sig);
        params.put("sig", sig);

        MyOkHttp.getInstance().submitFormdata(commentUrl, params, new MyOkHttp.OkHttpCallBack<String>() {
            @Override
            public void requestSuccess(String s) {
                Log.e(TAG, "success:" + s);
            }

            @Override
            public void requestFailure(String message) {
                Log.e(TAG, "failure:" + message);
            }
        }, String.class);
    }

}
