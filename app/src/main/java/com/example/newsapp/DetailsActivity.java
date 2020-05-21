package com.example.newsapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {

    AppBarLayout app_bar_layout_home;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        setContentView(R.layout.activity_details);

        app_bar_layout_home = findViewById(R.id.app_bar_layout_home);


        final String author = getIntent().getStringExtra("author");
        String title_text = getIntent().getStringExtra("title");
        String time = getIntent().getStringExtra("time");
        String description_text = getIntent().getStringExtra("description");
        final String img_url = getIntent().getStringExtra("image");
        final String site = getIntent().getStringExtra("site");



        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView author_details = findViewById(R.id.author_details);
        TextView title_head = findViewById(R.id.title_head);
        final TextView toolbar_author = findViewById(R.id.toolbar_author);
        TextView description = findViewById(R.id.desc_head);
        TextView date = findViewById(R.id.date_details);
        final ImageView image = findViewById(R.id.image_details);
        final ImageView back = findViewById(R.id.back);

       // author_details.setText(author);
        title_head.setText(title_text);
        date.setText(dateTime(time));
        description.setText(description_text);
        Picasso.get().load(img_url).fetch(new Callback() {
            @Override
            public void onSuccess() {
                Picasso.get().load(img_url).into(image);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        app_bar_layout_home.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {
                    //  Collapsed
                    toolbar_author.setText(author);
                    back.setColorFilter(Color.argb(0, 0, 0, 0));
                }
                else
                {
                    //Expanded
                    toolbar_author.setText("");
                    back.setColorFilter(Color.argb(255, 255, 255, 255));

                }
            }
        });
        author_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
                builder.setMessage("Open this news on the site?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(site));
                                startActivity(i);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
    public String dateTime(String date){
        Date date1;
        String t = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            date1 = simpleDateFormat.parse(date);
            t = formatter.format(date1).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return t;
    }
}
