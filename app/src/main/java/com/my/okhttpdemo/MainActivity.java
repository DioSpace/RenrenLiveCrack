package com.my.okhttpdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "T1";
    TextView show_board;
    EditText input_bard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show_board = (TextView) findViewById(R.id.request_text);
        input_bard = (EditText) findViewById(R.id.etId);
    }

    public void request_func(View view) {
        String plaintext = "test123456";
        if (input_bard.getText().toString().length() != 0) {
            plaintext = input_bard.getText().toString();
        }
        Log.w(TAG, "plaintext:"+ plaintext);
        String ciphertext = RSAUtil.encrypt(plaintext);
        if (ciphertext != null) {
            show_board.setText(ciphertext);
        }
    }

    public void comment_func(View view) {
        CommentUtil commentUtil = new CommentUtil();
        String content = input_bard.getText().toString();
        commentUtil.comment(content);
    }
}
