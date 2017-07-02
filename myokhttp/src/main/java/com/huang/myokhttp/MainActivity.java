package com.huang.myokhttp;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huang.myokhttp.http.OkHttpUtil;
import com.huang.myokhttp.http.ProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = MainActivity.class.getName();
    private ProgressBar download_progress,post_progress;
    private TextView download_text,post_text;
    public static String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/okhttp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        download_progress = (ProgressBar) findViewById(R.id.download_progress);
        download_text = (TextView) findViewById(R.id.download_text);

        post_progress = (ProgressBar) findViewById(R.id.post_progress);
        post_text = (TextView) findViewById(R.id.post_text);
        findViewById(R.id.ok_post_file).setOnClickListener(this);


        findViewById(R.id.ok_download).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ok_download:
                String url = "http://192.168.0.104:8080/OkHttpServer/download/2.jpg";
                final String fileName = url.split("/")[url.split("/").length - 1];
                Log.i(TAG, "fileName==" + fileName);
                OkHttpUtil.downloadFile(url, new ProgressListener() {
                    @Override
                    public void onProgress(long currentBytes, long contentLength, boolean done) {
                        Log.i(TAG, "currentBytes==" + currentBytes + "==contentLength==" + contentLength + "==done==" + done);
                        int progress = (int) (currentBytes * 100 / contentLength);
                        download_progress.setProgress(progress);
                        download_text.setText(progress + "%");
                    }
                }, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }


                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response != null) {
                            InputStream is = response.body().byteStream();
                            FileOutputStream fos = new FileOutputStream(new File(basePath + "/" + fileName));
                            int len = 0;
                            byte[] buffer = new byte[2048];
                            while (-1 != (len = is.read(buffer))) {
                                fos.write(buffer, 0, len);
                            }
                            fos.flush();
                            fos.close();
                            is.close();
                        }
                    }
                });
                break;
            case R.id.ok_post_file:
                File file = new File(basePath + "/1.mp4");
                String postUrl = "http://192.168.0.104:8080/OkHttpServer/UploadFileServlet";

                OkHttpUtil.postFile(postUrl, new ProgressListener() {
                    @Override
                    public void onProgress(long currentBytes, long contentLength, boolean done) {
                        Log.i(TAG, "currentBytes==" + currentBytes + "==contentLength==" + contentLength + "==done==" + done);
                        int progress = (int) (currentBytes * 100 / contentLength);
                        post_progress.setProgress(progress);
                        post_text.setText(progress + "%");
                    }
                }, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response != null) {
                            String result = response.body().string();
                            Log.i(TAG, "result===" + result);
                        }
                    }
                }, file);

                break;
        }
    }
}
