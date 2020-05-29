package com.my.okhttpdemo;

import android.util.Log;

public class SigUtil {

    private static String TAG = "t2";

    public static String getSig(String[] strArr, String str) {
        for (int i = 0; i < strArr.length; i++) {
            for (int length = strArr.length - 1; length > i; length--) {
                int i2 = length - 1;
                if (strArr[length].compareTo(strArr[i2]) < 0) {
                    String str2 = strArr[length];
                    strArr[length] = strArr[i2];
                    strArr[i2] = str2;
                }
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (String append : strArr) {
            stringBuffer.append(append);
        }
        stringBuffer.append(str);
//        Log.e(TAG,"sb:"+stringBuffer);
        return MD5.getMD5(stringBuffer.toString(), 32);
    }

    public static String getSig2(
                                String v, String call_id, String gz,
                                String log_info,String uniq_key, int entry_id,
                                String extension,String api_key, String content,
                                int owner_id,String session_key, int type,
                                String format,String secretKey
    ) {
        String[] strArr = {
                "v=" + v,
                "call_id=" + call_id,
                "gz=" + gz,
                "log_info=" + log_info,
                "uniq_key=" + uniq_key,
                "entry_id=" + entry_id,
                "extension=" + extension,
                "api_key=" + api_key,
                "content=" + content,
                "owner_id=" + owner_id,
                "session_key=" + session_key,
                "type=" + type,
                "format=" + format
        };
//        Log.e(TAG,"strArr:"+strArr.toString());
        String sig = getSig(strArr, secretKey);
//        Log.e(TAG,"sig:"+sig);
        return sig;
    }
}
