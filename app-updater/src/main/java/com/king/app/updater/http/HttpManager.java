package com.king.app.updater.http;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import com.king.app.updater.constant.Constants;
import com.king.app.updater.util.SSLSocketFactoryUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * HttpManager使用{@link HttpURLConnection}实现{@link IHttpManager}
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class HttpManager implements IHttpManager {

    private static final int DEFAULT_TIME_OUT = 20000;

    private int mTimeout;

    private boolean isCancel;

    private static volatile HttpManager INSTANCE;

    public static HttpManager getInstance(){
        if(INSTANCE == null){
            synchronized (HttpManager.class){
                if(INSTANCE == null){
                    INSTANCE = new HttpManager();
                }
            }
        }

        return INSTANCE;
    }

    private HttpManager(){
        this(DEFAULT_TIME_OUT);
    }

    /**
     * HttpManager对外暴露。如果没有特殊需求，推荐使用{@link HttpManager#getInstance()}
     */
    public HttpManager(int timeout){
        this.mTimeout = timeout;
    }

    @Override
    public void download(String url, String path, String filename, @Nullable Map<String,String> requestProperty, DownloadCallback callback) {
        isCancel = false;
        new DownloadTask(url,path,filename,requestProperty,callback).execute();
    }

    @Override
    public void cancel() {
        isCancel = true;
    }

    /**
     * 异步下载任务
     */
    private class DownloadTask extends AsyncTask<Void,Long,File> {
        private String url;

        private String path;

        private String filename;

        private Map<String,String> requestProperty;

        private DownloadCallback callback;

        private Exception exception;

        public DownloadTask(String url, String path, String filename ,@Nullable Map<String,String> requestProperty, DownloadCallback callback){
            this.url = url;
            this.path = path;
            this.filename = filename;
            this.callback = callback;
            this.requestProperty = requestProperty;

        }

        @Override
        protected File doInBackground(Void... voids) {

            try {
                HttpsURLConnection.setDefaultSSLSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(SSLSocketFactoryUtils.createTrustAllHostnameVerifier());
                HttpURLConnection connect = (HttpURLConnection)new URL(url).openConnection();
                connect.setRequestMethod("GET");
                connect.setRequestProperty("Accept-Encoding", "identity");

                connect.setReadTimeout(mTimeout);
                connect.setConnectTimeout(mTimeout);

                if(requestProperty!=null){
                    for(Map.Entry<String,String> entry : requestProperty.entrySet()){
                        connect.setRequestProperty(entry.getKey(),entry.getValue());
                    }
                }

                connect.connect();
                int responseCode = connect.getResponseCode();
                Log.d(Constants.TAG,"Content-Type:" + connect.getContentType());
                if(responseCode == HttpURLConnection.HTTP_OK){

                    InputStream is = connect.getInputStream();

                    long length = connect.getContentLength();

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        length = connect.getContentLengthLong();
                    }

                    Log.d(Constants.TAG,"contentLength:" + length);

                    long progress = 0;

                    byte[] buffer = new byte[8192];

                    int len;
                    File file = new File(path,filename);
                    FileOutputStream fos = new FileOutputStream(file);
                    while ((len = is.read(buffer)) != -1){
                        if(isCancel){
                            cancel(true);
                            break;
                        }
                        fos.write(buffer,0,len);
                        progress += len;
                        //更新进度
                        if(length>0){
                            publishProgress(progress,length);
                        }
                    }

                    fos.flush();
                    fos.close();
                    is.close();

                    connect.disconnect();

                    return file;
                }else {//连接失败
                    throw new ConnectException(String.format("responseCode = %d",responseCode));
                }

            } catch (Exception e) {
                this.exception = e;
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(callback!=null){
                callback.onStart(url);
            }
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if(callback!=null){
                if(file!=null){
                    callback.onFinish(file);
                }else{
                    callback.onError(exception);
                }

            }
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            if(callback!=null){
                if(!isCancelled()){
                    callback.onProgress(values[0],values[1]);
                }

            }
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();
            if(callback!=null){
                callback.onCancel();
            }
        }


    }

}
