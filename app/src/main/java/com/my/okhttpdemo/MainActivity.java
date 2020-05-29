package com.my.okhttpdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "T1";

    EditText input_bard;
    EditText comment_board;
    TextView show_board;

    //密码RSA加密后
    private static String password_rsa = "";
    //登录时固定用来加密的 私钥
    private static final String secretKey = "ad974a0756d84cec80fcea72fcbfba9f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show_board = (TextView) findViewById(R.id.request_text);
        input_bard = (EditText) findViewById(R.id.etId);
        comment_board = (EditText) findViewById(R.id.commentTVId);
    }

    public void password_rsa_func(View view) {
        //18866674203  text123456
        //对password进行加密
        String passwords = "text123456";
        if (input_bard.getText().toString().length() != 0) {
            passwords = input_bard.getText().toString();
        }
        Log.w(TAG, "plaintext:" + passwords);
        password_rsa = EncryptionDemo.encrypt(passwords);
        if (password_rsa != null) {
            Log.e(TAG, "password_rsa:" + password_rsa);
            showResponse(password_rsa);
        } else {
            Toast.makeText(this, "请再次点击", Toast.LENGTH_LONG).show();
        }
    }

    public void login_func(View view) {
        String urlStr = "http://api.m.renren.com/api/client/login";

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("v", "1.0");
        params.put("password", password_rsa);
        params.put("client_info", "{\"uniqid\":\"359250050014594\",\"screen\":\"1080*1776\",\"");
        long currentTime = System.currentTimeMillis();
        String call_id = currentTime + "";
        params.put("call_id", call_id);
        params.put("gz", "compression");
        params.put("rkey", "f920ed45b874074c0f2f21b3056d5eff");
        params.put("ext_info", "{}");
        params.put("tab_sequence", "1");
        params.put("api_key", "9e1874c67e0b42d79cc16f787b644339");
        params.put("uniq_id", "359250050014594");
        params.put("user", "18866674203");
        params.put("format", "JSON");

        //根据以上参数第一次签名
        String sig = SignatureDemo.getSig(params, secretKey);
        Log.e(TAG, "sig:\n" + sig);
        params.put("sig", sig);

        //在上面的参数加上第一次签名后的sig，然后进行第二次签名
        String sig2 = SignatureDemo.getSig(params, secretKey);
        Log.e(TAG, "sig2:\n" + sig2);
        params.put("sig2", sig2);

        MyOkHttp.getInstance().submitFormdata(urlStr, params, new MyOkHttp.OkHttpCallBack<String>() {
            @Override
            public void requestSuccess(String s) {
                Log.e(TAG, "success:" + s);
                showResponse(s);//更新ui
            }

            @Override
            public void requestFailure(String message) {
                Log.e(TAG, "failure:" + message);
                showResponse(message);//更新ui
            }
        }, String.class);
    }

    public void comment_func(View view) {
        String content = comment_board.getText().toString();
        if (content == null || content == "") {
            Toast.makeText(this, "评论内容不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        CommentDemo commentUtil = new CommentDemo();
        commentUtil.comment(content);
    }

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                show_board.setText(response);
            }
        });
    }
}
