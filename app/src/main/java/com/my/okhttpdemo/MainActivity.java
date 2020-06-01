package com.my.okhttpdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.my.okhttpdemo.util.EncryptUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "t1";

    EditText input_bard;
    EditText comment_board;
    TextView show_board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show_board = (TextView) findViewById(R.id.request_text);
        input_bard = (EditText) findViewById(R.id.etId);
        comment_board = (EditText) findViewById(R.id.commentTVId);
    }

    public void encrpt_func(View view) {
        //393943e76e563740df135dcdbe887f8b698acac266da5afbaf
        //288158ed1ce45803df9205ce4f146634d4538483dabbd1a206e8184dfb332fa5
        //180869d4238df2e73bfe5c17a200a067300a8703a2327280d0
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ciphertext = EncryptUtil.encrypt("text123456");
                Log.e(TAG, "ciphertext:\n" + ciphertext);
            }
        }).start();
    }

    public void login_func(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Login_Func login_func = new Login_Func();
                login_func.login();
            }
        }).start();
    }

    public void comment_func(View view) {
        String content = comment_board.getText().toString();
        if (content == null || content == "") {
            Toast.makeText(this, "评论内容不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        Comment_Func commentUtil = new Comment_Func();
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
