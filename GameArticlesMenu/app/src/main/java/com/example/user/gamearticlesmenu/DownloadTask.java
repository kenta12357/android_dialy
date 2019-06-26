package com.example.user.gamearticlesmenu;

/**
 * Created by Kenta on 2018/02/13.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadTask extends AsyncTask<String, Void, String> {

    private Listener listener;

    // 非同期処理
    @Override
    protected String doInBackground (String... params) {

        return downloadImage (params[0]);
    }

    // 途中経過をメインスレッドに返す
    /*@Override
    protected void onProgressUpdate (Void... progress) {
        // 途中経過をメインスレッドに返す処理
    }*/

    // 非同期処理が終了後、結果をメインスレッドに返す
    @Override
    protected void onPostExecute (String bmp) {
        if (listener != null) {
            listener.onSuccess (bmp);
        }
    }


    private String downloadImage(String address) {
        // Bitmap 宣言
        String bmp = null;

        // final ?
        final StringBuilder result = new StringBuilder();

        // 宣言
        HttpURLConnection urlConnection = null;

        try {
            // URL型 url に address を格納
            URL url = new URL (address);

            // HttpURLConnection インスタンス生成
            urlConnection = (HttpURLConnection) url.openConnection();

            // タイムアウト設定
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(20000);

            // リクエストメソッド
            urlConnection.setRequestMethod("GET");

            // リダイレクトを自動で許可しない設定
            urlConnection.setInstanceFollowRedirects(false);

            // ヘッダーの設定(複数設定可能)
            urlConnection.setRequestProperty("Accept-Language", "jp");

            // 接続
            urlConnection.connect();

            // resp にレスポンスコードを入れる？
            int resp = urlConnection.getResponseCode();

            // resp によってスイッチ
            switch (resp){
                // HTTP_OK が返ってきたときは
                case HttpURLConnection.HTTP_OK:
                    // InputStream を準備
                    InputStream is = null;
                    try{
                        // is にネットからファイルをダウンロード
                        is = urlConnection.getInputStream();
                        // bmp にファイルを変換して格納
                        bmp = convertInputStreamToString(is);
                        // is をクローズ
                        is.close();
                        // エラー処理
                    } catch(IOException e){
                        e.printStackTrace();
                    } finally{
                        if(is != null){
                            is.close();
                        }
                    }
                    break;
                // HTTP_UNAUTHORIZED だったら break
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    break;
                default:
                    break;
            }
        // エラー処理
        } catch (Exception e) {
            Log.d("debug", "downloadImage error");
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        // bmp を返す
        return bmp;
    }


    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(String bmp);
    }

    static String convertInputStreamToString (InputStream is) throws IOException {
        InputStreamReader reader = new InputStreamReader(is);
        StringBuilder builder= new StringBuilder();
        char[] buffer = new char[512];
        int read;
        while (0 <= (read = reader.read(buffer))) {
            builder.append(buffer, 0, read);
        }
        return builder.toString();
    }

    public static boolean newtWorkCheck(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info != null){
            return info.isConnected();
        } else{
            return false;
        }
    }
}
