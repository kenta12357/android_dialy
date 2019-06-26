package com.example.user.gamearticlesmenu;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BuildMenu.toMainListener{

    private BuildMenu buildMenu;
    private FileManagement fileManagement;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecycleViewAdapter adapter;
    private BottomNavigationView bottomNavigationView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;

    private ArrayList<Articles> allItem = new ArrayList<>();
    private ArrayList<Articles> newItem = new ArrayList<>();
    private ArrayList<Articles> historyItem = new ArrayList<>();
    private ArrayList<Articles> showItem = new ArrayList<>();
    private final String fileName = "Article_NoRead.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileManagement = new FileManagement(this);
        fileManagement.ReadArticle(allItem, fileName);

        buildMenu = new BuildMenu(allItem, newItem, historyItem, getApplicationContext());
        buildMenu.setListener(this);
        buildMenu.DownloadSource();

        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.settingsToolbar:
                        Intent settingActivityIntent = new Intent(getApplication(), SettingsActivity.class);
                        startActivity(settingActivityIntent);
                        return true;
                    case R.id.debugButton:
                        // Write here debug action code.
                        return true;
                }
                return false;
            }
        });

        toolbar.setTitle("ALL");
        buildMenu.setShowItem(allItem, showItem);
        recyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        BuildRecyclerView(showItem);

        swipeRefreshLayout = findViewById(R.id.refreshLayoutID);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.red), ContextCompat.getColor(this, R.color.green), ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.yellow));
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        swipeRefreshLayout.setRefreshing(true);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switchDisplayArticle(menuItem.getItemId());
                return true;
            }
        });

    }

    @Override
    public void onSuccess(){
        if (!newItem.isEmpty()) {
            fileManagement.WriteArticle(allItem, fileName);
            bottomNavigationView.setSelectedItemId(R.id.bottom_new);
            buildMenu.setShowItem(newItem, showItem);
            adapter.notifyDataSetChanged();
        }
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     *
     * @param list RecyclerView で表示したいリスト
     */
    public void BuildRecyclerView(final ArrayList<Articles> list){
        adapter = new RecycleViewAdapter(list, getApplicationContext()) {
            @Override
            protected void onItemClick(int position){
                super.onItemClick (position);
                StartBrowse (list.get(position).url);
                list.get(position).isRead = true;
                list.get(position).isNew = false;
                adapter.notifyItemChanged(position);
                if (!BuildMenu.IsContain(historyItem, list.get(position).url)) {
                    historyItem.add(new Articles());
                    historyItem.set(historyItem.size()-1, list.get(position));
                }
                fileManagement.WriteArticle(allItem, fileName);
            }
        };
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void StartBrowse (String url) {
        Uri uri = Uri.parse(url);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.setStartAnimations(this, R.anim.right_to_left, R.anim.nothing);
        builder.setExitAnimations(this, R.anim.nothing, R.anim.left_to_right);
        CustomTabsIntent customTabsIntent = builder.build();

        customTabsIntent.launchUrl(this, uri);
        /*
        setContentView(R.layout.webview);
        WebView webView = findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/h.html");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        */
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            bottomNavigationView.setSelectedItemId(R.id.bottom_all);
            buildMenu.DownloadSource();
        }
    };

    private void switchDisplayArticle (int itemId) {
        if (bottomNavigationView.getSelectedItemId() == itemId) {
            recyclerView.smoothScrollToPosition(0);
        } else {
            switch (itemId) {
                case R.id.bottom_all:
                    toolbar.setTitle("ALL");
                    buildMenu.setShowItem(allItem, showItem);
                    break;

                case R.id.bottom_new:
                    toolbar.setTitle("NEW");
                    buildMenu.setShowItem(newItem, showItem);
                    break;

                case R.id.bottom_history:
                    toolbar.setTitle("HISTORY");
                    buildMenu.setShowItem(historyItem, showItem);
                    break;
            }
            adapter.notifyDataSetChanged();
        }
    }
}
