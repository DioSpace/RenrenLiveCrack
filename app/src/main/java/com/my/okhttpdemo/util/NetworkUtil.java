package com.my.okhttpdemo.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

public class NetworkUtil {

    private static final String TAG = "t1";

    //类初始化时，不初始化这个对象(延时加载，真正用的时候再创建)
    private static NetworkUtil instance;

    //构造器私有化
    private NetworkUtil() {
    }

    //方法同步，调用效率低
    public static synchronized NetworkUtil getInstance() {
        if (instance == null) {
            instance = new NetworkUtil();
        }
        return instance;
    }


    public void sendGet(String urlStr) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlStr);//新建URL
            connection = (HttpURLConnection) url.openConnection();//发起网络请求
            connection.setRequestMethod("GET");//请求方式
            connection.setConnectTimeout(8000);//连接最大时间
            connection.setReadTimeout(8000);//读取最大时间
            InputStream in = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));//写入reader
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            System.out.println(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /*
     * 传入一个Url地址  返回一个JSON字符串
     * 网络请求的情况分析:
     *   如果是404 500 ... 代表网络(Http协议)请求失败
     *   200 服务器返回成功
     *       业务成功  /业务失败
     * */
    public String doGet(String urlPath) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlPath);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                return reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return "{ \"success\": false,\n   \"errorMsg\": \"后台服务器开小差了!\",\n     \"result\":{}}";
    }

    /*
     * 传入一个Url地址  返回一个JSON字符串
     * */
    public String doPost(String urlPath, Map<String, String> paramsMap) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlPath);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            //--------------------------------
            connection.setDoOutput(true);//是否写入参数
            String paramLast = getParams(paramsMap);//拼接参数
            connection.getOutputStream().write(paramLast.getBytes());
            //--------------------------------
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                return reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return "{ \"success\": false,\n   \"errorMsg\": \"后台服务器开小差了!\",\n     \"result\":{}}";
    }

    private String getParams(Map<String, String> paramsMap) {
        String result = "";
        for (HashMap.Entry<String, String> entity : paramsMap.entrySet()) {
            if (entity.getKey() == "sig2") {
                result += "&" + "sig" + "=" + entity.getValue();
                break;
            }
            result += "&" + entity.getKey() + "=" + entity.getValue();
        }
        return result.substring(1);
    }


    /**
     * @param filePath 要上传的文件绝对路径，如：e:/upload/SSD4k对齐分区.zip
     * @param urlStr   上传路径端口号和项目名称，如：http://192.168.1.209:9080/gjbmj
     */
    public String uploadFile(String filePath, String urlStr) {
        Log.e(TAG, "=========开始上传=============");
        String result = null;
        String uuid = UUID.randomUUID().toString();
        String BOUNDARY = uuid;
        String NewLine = "\r\n";

        HttpURLConnection connection = null;
        DataInputStream bis = null;
        FileInputStream fis = null;
        DataOutputStream bos = null;
        try {
            File file = new File(filePath);
            Log.e(TAG, " file = " + file.length());
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setChunkedStreamingMode(1024 * 1024);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Charsert", "UTF-8");
            connection.setConnectTimeout(50000);
            connection.setRequestProperty("User-Agent", "Android Client Agent");
            connection.setRequestProperty("Content-Type", "multipart/form-data; charset=utf-8; boundary=" + BOUNDARY);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setChunkedStreamingMode(1024 * 50);
            connection.connect();

            bos = new DataOutputStream(connection.getOutputStream());
            if (file.exists()) {
                fis = new FileInputStream(file);
                byte[] buff = new byte[1024];
                bis = new DataInputStream(fis);
                int cnt = 0;
                //数据以--BOUNDARY开始
                bos.write(("--" + BOUNDARY).getBytes());
                //换行
                bos.write(NewLine.getBytes());
                //内容描述信息
                String content = "Content-Disposition: form-data; name=file; filename=\"" + file.getName() + "\"";
                bos.write(content.getBytes());
                bos.write(NewLine.getBytes());
                bos.write(NewLine.getBytes());
                //空一行后，开始通过流传输文件数据
                while ((cnt = bis.read(buff)) != -1) {
                    bos.write(buff, 0, cnt);
                }
                bos.write(NewLine.getBytes());
                //结束标志--BOUNDARY--
                bos.write(("--" + BOUNDARY + "--").getBytes());
                bos.write(NewLine.getBytes());
                bos.flush();
            }
            int res = connection.getResponseCode();
            Log.e(TAG, "res------------------>>" + res);
            if (res == 200) {
                InputStream input = connection.getInputStream();
                StringBuilder sbs = new StringBuilder();
                int ss;
                while ((ss = input.read()) != -1) {
                    sbs.append((char) ss);
                }
                result = sbs.toString();
                Log.e(TAG, "result------------------>>" + result);
                return result;
            }
            Log.e(TAG, "=========上传完成=============");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
            new File(filePath).delete();
        }
        return "{ \"success\": false,\n   \"errorMsg\": \"后台服务器开小差了!\",\n     \"result\":{}}";
    }

    //提交表单
    public String submitFormdata(String urlPath, LinkedHashMap<String, String> paramsMap) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlPath);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");//关键代码 application/x-www-form-urlencoded
            //--------------------------------
            connection.setDoOutput(true);//是否写入参数
            String paramLast = getParams(paramsMap);
//            Log.w(TAG, "parmas:\n" + paramLast);
            connection.getOutputStream().write(paramLast.getBytes());
            //--------------------------------
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
//                reader = new BufferedReader(new InputStreamReader(is));
//                return reader.readLine();
                String result = zipInputStream(is);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return "{ \"success\": false,\n   \"errorMsg\": \"后台服务器开小差了!\",\n     \"result\":{}}";
    }

    //解码服务端返回的gzip格式压缩的数据
    private String zipInputStream(InputStream is) throws IOException {
        GZIPInputStream gzip = new GZIPInputStream(is);
        BufferedReader in = new BufferedReader(new InputStreamReader(gzip, "UTF-8"));
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = in.readLine()) != null)
            buffer.append(line + "\n");
        is.close();
        return buffer.toString();
    }
}
