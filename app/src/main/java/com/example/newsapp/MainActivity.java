package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.newsapp.https.PostClient;
import com.example.newsapp.models.Articles;
import com.example.newsapp.models.Head;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    Toolbar toolbar;
    String apiKey = "95942b0cae0d4f9fb0ebace232cc4d5c";
    String country = "ru";
    DrawerLayout drawer_layout;
    List<Articles> articles = new ArrayList<>();
    Adapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    SwipeRefreshLayout refreshLayout;
    Boolean isRefreshed = true;

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.home_toolbar);
        drawer_layout = findViewById(R.id.drawer_layout);
        progressBar = findViewById(R.id.progress_loading);
        refreshLayout = findViewById(R.id.refresh);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(articles, MainActivity.this);
        recyclerView.setAdapter(adapter);
        setUp();
        if (InternalStorage.exist(this, "NEWS")) {
            try {
                articles = (List<Articles>) InternalStorage.readObject(this, "NEWS");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            adapter = new Adapter(articles, MainActivity.this);
            recyclerView.setAdapter(adapter);
        } else {
            getData(country, apiKey);
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    progressBar.setVisibility(View.VISIBLE);
                    getData(country, apiKey);
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshed = true;
                getData(country, apiKey);
            }
        });
        adapter.setClickListener(new Adapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("title", articles.get(position).getTitle());
                intent.putExtra("time", articles.get(position).getPublishedAt());
                intent.putExtra("description", articles.get(position).getDescription());
                intent.putExtra("author", articles.get(position).getSource().getName());
                intent.putExtra("image", articles.get(position).getUrlToImage());
                intent.putExtra("site", articles.get(position).getUrl());
                startActivity(intent);
            }
        });
    }

    private void getData(String country, String apiKey) {
        PostClient.getINSTANCE().login(country, apiKey).enqueue(new Callback<Head>() {
            @Override
            public void onResponse(Call<Head> call, Response<Head> response) {
                if (response.isSuccessful()) {
                    if (isRefreshed) {
                        articles.clear();
                        isRefreshed = false;
                    }
                    adapter.addItems(response.body().getArticles());
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);
                    try {
                        InternalStorage.writeObject(MainActivity.this, "NEWS", articles);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Head> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Something goes wrong!Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUp() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        navigationView.setItemIconTintList(null);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer_layout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        MaterialShapeDrawable navViewBackground = (MaterialShapeDrawable) navigationView.getBackground();
        navViewBackground.setShapeAppearanceModel(
                navViewBackground.getShapeAppearanceModel()
                        .toBuilder()
                        .setBottomRightCorner(CornerFamily.ROUNDED, 160)
                        .build());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        refreshLayout.setRefreshing(true);
        isRefreshed = true;
        drawer_layout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.russia:
                country = "ru";
                getData(country, apiKey);
                return true;
            case R.id.usa:
                country = "us";
                getData(country, apiKey);
                return true;
            case R.id.italy:
                country = "it";
                getData(country, apiKey);
                return true;
            default:
                return false;
        }
    }
}
