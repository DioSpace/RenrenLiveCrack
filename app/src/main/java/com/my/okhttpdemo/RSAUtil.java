package com.my.okhttpdemo;

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

public class RSAUtil {

    private static final String TAG = "T1";
    private static PublicKey publicKey = null;

    private static void getKeyParam() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder().url("http://login.renren.com/ajax/getEncryptKey").build();//创建一个Request对象
                    Response response = client.newCall(request).execute();//发送请求获取返回数据
                    String responseData = response.body().string();//处理返回的数据
                    JSONObject jsonObject = new JSONObject(responseData);
                    String e = jsonObject.getString("e");
                    String n = jsonObject.getString("n");

                    publicKey = makePublicKey(n, e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static String encrypt(String plaintext) {
        getKeyParam();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (publicKey == null) {
            return null;
        }

        String ciphertext = null;
        try {
            Cipher instance = Cipher.getInstance("RSA");
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
        publicKey = null;
        return ciphertext;
    }


 /*   public static void start() {
        String str = "bfe215c9428f34a3e1cae865d9e3aabf2032acc11616f7693aa1d05c4cd03177";
        String str2 = "10001";
        handle(str, str2);

        try {
            String result = P("test123456");
            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    private static PublicKey makePublicKey(String n, String e) {
        BigInteger n1 = new BigInteger(n, 16);
        BigInteger e1 = new BigInteger(e, 16);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n1, e1);
        PublicKey publicKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            System.out.println("publicKey：" + publicKey);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (InvalidKeySpecException ex) {
            ex.printStackTrace();
        }
        return publicKey;
    }

//    public static String P(String str) throws Exception {
//        Cipher instance = Cipher.getInstance("RSA");
//        instance.init(1, publicKey);
//        return byteArrayToHexString(instance.doFinal(str.getBytes()));
//    }

    private static String byteArrayToHexString(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bArr) {
            stringBuffer.append(Integer.toString((b & 255) + 256, 16).substring(1));
        }
        return stringBuffer.toString();
    }
}
