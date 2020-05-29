package com.my.okhttpdemo;

import android.util.Log;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CommentUtil {
    private static String TAG = "t1";

    private static String commentUrl = "http://api.m.renren.com/api/comment/addComment";

    String v = "1.0";                           //1
    String call_id = "1590671961681";           //2
    String gz = "compression";                  //3
    String log_info = "{source=comment-live}";              //4
    String uniq_key = "427af61ca8f88972179a84c02090e57f";       //5
    int entry_id = 2848521;                             //6
    String extension = "{\"replaceUbb\":\"false\"}";        //7
    String api_key = "9e1874c67e0b42d79cc16f787b644339";        //8
//    String content = "主播像学生";                  //9
    int owner_id = 969914816;                   //10
    String session_key = "x2JThqT3G2v4TNxr";    //11
    int type = 39;                              //12
    String format = "JSON";                     //13
    String sig;                                 //14
    String secretKey = "d787468a5646e5e0d7657893ffcb9c79";//15

    public void comment(String content) {
        long currentTime = System.currentTimeMillis();
        call_id = currentTime + "";
        sig = SigUtil.getSig2(
                v, call_id, gz,
                log_info, uniq_key, entry_id,
                extension, api_key, content,
                owner_id, session_key, type,
                format, secretKey);
        Log.e(TAG, "sig:" + sig);

        //表单数据参数填入
        RequestBody body = new FormBody.Builder()
                .add("v", v)            //1
                .add("call_id", call_id)        //2
                .add("gz", gz)          //3
                .add("log_info", log_info)      //4
                .add("uniq_key", uniq_key)       //5
                .add("entry_id", entry_id + "")       //6
                .add("extension", extension)         //7
                .add("api_key", api_key)             //8
                .add("content", content)             //9
                .add("owner_id", owner_id + "")           //10
                .add("session_key", session_key)         //11
                .add("type", type + "")                   //12
                .add("format", format)               //13
                .add("sig", sig)                     //14
                .build();
        MyOkHttp.getInstance().submitFormdata(commentUrl, body, new MyOkHttp.OkHttpCallBack<String>() {
            @Override
            public void requestSuccess(String s) {
                Log.e(TAG, "success :\n" + s);
            }

            @Override
            public void requestFailure(String message) {
                Log.e(TAG, "failre:\n" + message);
            }
        }, String.class);
    }

}
