package com.my.okhttpdemo.util;

import android.util.Log;

import org.json.JSONObject;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EncryptUtil {

    private static final String TAG = "t1";
    public static String rkey = null;

    private static PublicKey getPublicKey() {
        PublicKey publicKey = null;
        try {
            //从网络上请求加密所需的参数
            OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
            Request request = new Request.Builder().url("http://login.renren.com/ajax/getEncryptKey").build();//创建一个Request对象
            Response response = client.newCall(request).execute();//发送请求获取返回数据
            String responseData = response.body().string();//处理返回的数据
            JSONObject jsonObject = new JSONObject(responseData);
            rkey = jsonObject.get("rkey").toString();//储存rkey

            String e = jsonObject.getString("e");
            String n = jsonObject.getString("n");
            publicKey = makePublicKey(n, e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    //拿到两个参数生成公钥
    private static PublicKey makePublicKey(String n, String e) {
        BigInteger n1 = new BigInteger(n, 16);
        BigInteger e1 = new BigInteger(e, 16);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n1, e1);
        PublicKey publicKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            Log.w(TAG, "publicKey：" + publicKey);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (InvalidKeySpecException ex) {
            ex.printStackTrace();
        }
        return publicKey;
    }

    private static void showPublicKey(PublicKey publicKey) {
        byte[] bytes = publicKey.getEncoded();
        String publicKeyStr = byteArrayToHexString(bytes);
        Log.w(TAG, "publicKeyStr:\n" + publicKeyStr);
        Log.w(TAG, "legth:" + publicKeyStr.length());
    }


    //加密方法
    public static String encrypt(String plaintext) {
        PublicKey publicKey = getPublicKey();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (publicKey == null) {
            Log.w(TAG, "publicKey 为空");
            return null;
        }
//        showPublicKey(publicKey);
        String ciphertext = null;
        try {
            Cipher instance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            instance.init(1, publicKey);
            ciphertext = byteArrayToHexString(instance.doFinal(plaintext.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return ciphertext;
    }

    public static String byteArrayToHexString(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bArr) {
            stringBuffer.append(Integer.toString((b & 255) + 256, 16).substring(1));
        }
        return stringBuffer.toString();
    }
}
