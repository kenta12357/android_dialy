package com.example.user.gamearticlesmenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * ファイル書き込み、読み込みを行う
 */

public class FileManagement {

    private Context context;

    FileManagement(Context c) {
        context = c;
    }

    /**
     * 引数に与えられた list をファイルに書き込む
     * @param list 記事のリスト
     */
    public void WriteArticle (ArrayList<Articles> list, String fileName) {

        String separator = "\\";
        String articleSet;
        Articles article;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int articleMaxSize = Integer.parseInt(sharedPreferences.getString("number_article", "10"));
        if (articleMaxSize > 1000) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("number_article", "1000");
            editor.apply();
        }

        try {
            FileOutputStream outputStream;
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            if (list.size() <= 100) {
                articleMaxSize = list.size();
            }

            for (int i = 0; i < articleMaxSize; i++) {
                article = list.get(i);
                articleSet = article.title + separator + article.date + separator + article.url + separator + article.isRead + separator;
                outputStream.write(articleSet.getBytes());
            }

            outputStream.write("<>".getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 読み込んだ記事をファイルにカンマ区切りで書き込みます。
     * @param item ファイルに書き込むアイテム
     * @param fileName 書き込むファイルの名前
     */
    public void ReadArticle (ArrayList<Articles> item, String fileName) {

        String lineBuffer = null;

        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
            lineBuffer = reader.readLine();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lineBuffer != null) {
            int i;
            while ((i = lineBuffer.indexOf("\\")) != -1) {
                item.add(new Articles());
                item.get(item.size()-1).title = lineBuffer.substring(0, i);
                lineBuffer = lineBuffer.substring(i + 1);
                i = lineBuffer.indexOf("\\");
                item.get(item.size()-1).date = Long.parseLong(lineBuffer.substring(0, i));
                lineBuffer = lineBuffer.substring(i + 1);
                i = lineBuffer.indexOf("\\");
                item.get(item.size()-1).url = lineBuffer.substring(0, i);
                lineBuffer = lineBuffer.substring(i + 1);
                i = lineBuffer.indexOf("\\");
                item.get(item.size()-1).isRead = Boolean.valueOf(lineBuffer.substring(0,i));
                lineBuffer = lineBuffer.substring(i + 1);
            }
        }


    }
}
