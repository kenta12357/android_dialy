package com.example.user.gamearticlesmenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * ページのソースを取得して、リストを作る
 */

public class BuildMenu {

    private final String JIN_URL = "http://jin115.com/archives/cat_50029367.html";
    private final String PS4_URL = "http://openworldnews.net/";
    private final String SWITCH_URL = "http://xn--eckybzahmsm43ab5g5336c9iug.com/";
    private final String AUTOMATON = "https://automaton-media.com/";
    private String sourceStr;
    private final String[] URLs = {JIN_URL, PS4_URL, SWITCH_URL, AUTOMATON};
    private final int TITLE = 0;
    private final int URL = 1;
    private final int DATE = 2;
    private int countUrl = 0;
    private boolean showJin, showPs4, showSwitch, showAutomaton;

    private Context context;
    private ArrayList<String> sources = new ArrayList<>();
    private ArrayList<Articles> allItem;
    private ArrayList<Articles> newItem;
    private ArrayList<Articles> historyItem;

    private toMainListener listener;

    BuildMenu(ArrayList<Articles> list, ArrayList<Articles> listNew, ArrayList<Articles> listHistory, Context c){
        allItem = list;
        newItem = listNew;
        historyItem = listHistory;
        context = c;
    }

    interface toMainListener {
        void onSuccess ();
    }

    void setListener (toMainListener listener) {
        this.listener = listener;
    }

    /**
     * リストを取得するときのトリガー
     */
    public void DownloadSource () {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.setListener(createListener());
        if (countUrl < URLs.length) {
            downloadTask.execute(URLs[countUrl]);
        } else {
            MakeMenu();
            addHistoryItem();
            SortItems(allItem);
            SortItems(newItem);
            listener.onSuccess();
            countUrl = 0;
        }
    }

    private DownloadTask.Listener createListener(){
        return new DownloadTask.Listener(){
            @Override
            public void onSuccess(String bmp){
                if (bmp == null){
                    Toast.makeText(context, "Couldn't get source \""+ URLs[countUrl] + "\"", Toast.LENGTH_SHORT).show();
                    sources.add(null);
                } else {
                    if (bmp.length() > 1000000) {
                        sourceStr = bmp.substring(0, 1000000);
                    } else {
                        sourceStr = bmp;
                    }
                    sources.add(sourceStr);
                }
                countUrl++;
                DownloadSource();
            }
        };
    }

    private void MakeMenu(){
        if (sources.size() != 4) {
            return;
        }

        int i;
        String tmp;

        // jin
        String str = sources.get(0);
        if (str != null) {
            while ((i = str.indexOf("article-title\"")) != -1) {
                str = str.substring(i);
                i = str.indexOf("http");
                str = str.substring(i);
                i = str.indexOf("\"");
                tmp = str.substring(0, i);
                if (allItem.size() > 0) {
                    if (IsContain(allItem, tmp)) break;
                }
                allItem.add(new Articles());
                addItem(tmp, URL);
                i = str.indexOf(">");
                str = str.substring(i);
                i = str.indexOf("<");
                addItem(str.substring(1, i).replace("&quot;", "\""), TITLE);
                i = str.indexOf("datetime=\"20");
                str = str.substring(i);
                i = str.indexOf("20");
                str = str.substring(i);
                i = str.indexOf("\"");
                tmp = str.substring(0, i);
                tmp = tmp.replace("-", "");
                tmp = tmp.replace(" ", "");
                tmp = tmp.replace(":", "");
                addItem(tmp, DATE);
                allItem.get(allItem.size() - 1).isNew = true;
            }
        }

        // ps4
        str = sources.get(1);
        if (str != null) {
            while ((i = str.indexOf("article-title\"")) != -1) {
                str = str.substring(i);
                i = str.indexOf("http");
                str = str.substring(i);
                i = str.indexOf("\"");
                tmp = str.substring(0, i);
                if (IsContain(allItem, tmp)) break;
                allItem.add(new Articles());
                addItem(tmp, URL);
                i = str.indexOf(">");
                str = str.substring(i);
                i = str.indexOf("<");
                addItem(str.substring(1, i).replace("&quot;", "\""), TITLE);
                i = str.indexOf("datetime=\"20");
                str = str.substring(i);
                i = str.indexOf("20");
                str = str.substring(i);
                i = str.indexOf("\"");
                tmp = str.substring(0, i);
                tmp = tmp.replace("-", "");
                tmp = tmp.replace(" ", "");
                tmp = tmp.replace(":", "");
                addItem(tmp, DATE);
                allItem.get(allItem.size() - 1).isNew = true;
            }
        }

        // switch
        str = sources.get(2);
        if (str != null) {
            str = str.substring(str.indexOf("list ect-big-card"));
            while ((str.indexOf("entry-card-wrap a-wrap cf")) != -1) {
                str = str.substring(str.indexOf("<a href=") + 9);
                tmp = str.substring(0, str.indexOf("\""));
                if (IsContain(allItem, tmp)) break;
                allItem.add(new Articles());
                addItem(tmp, URL);
                str = str.substring(str.indexOf("title=") + 7);
                addItem(str.substring(0, str.indexOf("\"")), TITLE);
                str = str.substring(str.indexOf("post-date") + 11);
                tmp = str.substring(0, str.indexOf("<"));
                tmp = tmp.replace("/", "");
                tmp = tmp.replace(" ", "");
                tmp = tmp.replace(":", "");
                allItem.get(allItem.size() - 1).date = Long.parseLong(tmp) * 100;
                allItem.get(allItem.size() - 1).isNew = true;
            }
        }

        // automaton
        str = sources.get(3);
        if (str != null) {
            str = str.substring(str.indexOf("<span>最新記事"));
            for (int j = 0; j < 17; j++) {
                str = str.substring(str.indexOf("<a href=") + 9);
                tmp = str.substring(0, str.indexOf("\""));
                if (IsContain(allItem, tmp)) break;
                allItem.add(new Articles());
                addItem(tmp, URL);
                str = str.substring(str.indexOf("title=") + 7);
                addItem(str.substring(0, str.indexOf("\"")), TITLE);
                str = str.substring(str.indexOf("datetime") + 10);
                tmp = str.substring(0, str.indexOf("+"));
                tmp = tmp.replace("-", "");
                tmp = tmp.replace("T", "");
                tmp = tmp.replace(":", "");
                addItem(tmp, DATE);
                allItem.get(allItem.size() - 1).isNew = true;
            }
        }

        // newItem に追加
        for (int j = allItem.size() -1; j > 0; j--) {
            if (allItem.get(j).isNew) {
                newItem.add(new Articles());
                newItem.set(newItem.size()-1, allItem.get(j));
            } else break;
        }

        sources.clear();
    }

    private void SortItems(ArrayList<Articles> list){
        Articles tmp;
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).date < list.get(j).date) {
                    tmp = list.set(i, list.get(j));
                    list.set(j, tmp);
                }
            }
        }
    }

    /**
     *
     * @param checkList 検索されたいリスト
     * @param checkURL 検索したい URL
     * @return 含まれるとき true を返す
     */
    public static boolean IsContain (ArrayList<Articles> checkList, String checkURL) {
        for (int j = 0; j < checkList.size()-1; j++) {
            if (checkList.get(j).url.equals(checkURL)) {
                return true;
            }
        }
        return false;
    }

    private void addItem (String item, int id) {
        switch (id) {
            case TITLE:
                allItem.get(allItem.size()-1).title= item;
                break;

            case URL:
                allItem.get(allItem.size()-1).url = item;
                break;

            case DATE:
                allItem.get(allItem.size()-1).date = Long.parseLong(item);
                break;
        }
    }

    private void addHistoryItem () {
        for (int i = 0; i < allItem.size(); i++) {
            if (allItem.get(i).isRead) {
                historyItem.add(new Articles());
                historyItem.set(historyItem.size() - 1, allItem.get(i));
            }
        }
    }

    public void setShowItem (ArrayList<Articles> fromItem, ArrayList<Articles> toItem) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        showJin = sharedPreferences.getBoolean("jin_check", true);
        showPs4 = sharedPreferences.getBoolean("ps4_check", true);
        showSwitch = sharedPreferences.getBoolean("switch_check", true);
        showAutomaton = sharedPreferences.getBoolean("automaton_check", true);

        toItem.clear();

        for (int i = 0; i < fromItem.size(); i ++) {
            toItem.add(new Articles());
            if (fromItem.get(i).url.contains("http://jin115.com/") && showJin)
                toItem.set(toItem.size() - 1, fromItem.get(i));
            else if (fromItem.get(i).url.contains("http://openworldnews.net/") && showPs4)
                toItem.set(toItem.size()-1, fromItem.get(i));
            else if (fromItem.get(i).url.contains("http://xn--eckybzahmsm43ab5g5336c9iug.com/") && showSwitch)
                toItem.set(toItem.size()-1, fromItem.get(i));
            else if (fromItem.get(i).url.contains("https://jp.automaton.am/") && showAutomaton)
                toItem.set(toItem.size()-1, fromItem.get(i));
            else toItem.remove(toItem.size()-1);
        }
    }
}
