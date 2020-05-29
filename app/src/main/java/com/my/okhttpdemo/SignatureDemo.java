package com.my.okhttpdemo;

import android.util.Log;

import java.util.LinkedHashMap;

public class SignatureDemo {

    private static String TAG = "t2";

    private static String getSig(String[] strArr, String str) {
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

    public static String getSig(LinkedHashMap<String, String> params, String secretKey) {
        int num = params.size();
        String[] strArr = new String[num];
        int index = 0;
        for (LinkedHashMap.Entry<String, String> entity : params.entrySet()) {
            strArr[index] = String.format("%s=%s", entity.getKey(), entity.getValue());
            index++;
        }
//        Log.e(TAG, "strArr:" + strArr.toString());
        String sig = getSig(strArr, secretKey);
        Log.e(TAG, "sig:" + sig);
        return sig;
    }

}
