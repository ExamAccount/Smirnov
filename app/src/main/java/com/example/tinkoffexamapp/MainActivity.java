package com.example.tinkoffexamapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.tinkoffexamapp.API.APIConfig;
import com.example.tinkoffexamapp.API.APIService;
import com.example.tinkoffexamapp.Models.Gif;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private AppCompatImageView imageView;
    private ExtendedFloatingActionButton nextButton, previousButton;
    private ProgressBar progressBar;
    private AppCompatTextView textView;
    private Retrofit retrofit;
    private APIService apiService;
    private ArrayList<Gif> cashGifs = new ArrayList<>();
    private int currentGif = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        loadNextGif();
        setListeners();;
    }

    private void init() {
        progressBar = findViewById(R.id.progress_bar);
        textView = findViewById(R.id.main_text_view);
        imageView = findViewById(R.id.main_image_view);
        nextButton = findViewById(R.id.next_button);
        previousButton = findViewById(R.id.previous_button);

        retrofit = new Retrofit.Builder()
                .baseUrl(APIConfig.HOST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(APIService.class);
        previousButton.setBackgroundColor(Color.GRAY);
    }

    private void setListeners()
    {
        nextButton.setOnClickListener(view -> {
            loadNextGif();
        });

        previousButton.setOnClickListener(view -> {
            loadPreviousGif();
        });
    }

    private void loadPreviousGif() {
        progressBar.setVisibility(View.VISIBLE);
        if (currentGif > 0)
        {
            currentGif--;
            show(cashGifs.get(currentGif));
        }

        if (currentGif == 0)
        {
            if (cashGifs.size() > 1)
        {
            previousButton.setBackgroundColor(Color.GRAY);
        }
        }
    }

    private void loadNextGif()
    {
        progressBar.setVisibility(View.VISIBLE);
        if (currentGif == cashGifs.size() - 1)
        {
            Call<Gif> gifCall = apiService.getRandomGif();
            gifCall.enqueue(new Callback<Gif>() {
                @Override
                public void onResponse(Call<Gif> call, Response<Gif> response) {
                    if (!response.body().equals("Error"))
                    {
                        cashGifs.add(response.body());
                        currentGif++;
                        show(response.body());
                    }
                }

                @Override
                public void onFailure(Call<Gif> call, Throwable t) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Snackbar.make(imageView, "Что-то пошло не так. Попробуйте еще раз.",
                            Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            currentGif++;
            show(cashGifs.get(currentGif));
        }
        if (cashGifs.size() > 0)
        {
            previousButton.setBackgroundColor(getResources().getColor(R.color.teal_200));
        }
    }

    private void show (Gif gif)
    {

        if (!gif.getGifURL().equals(""))
        {
            Glide
                    .with(MainActivity.this)
                    .asGif()
                    .load(gif.getGifURL())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .listener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.INVISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    })
                    .apply(new RequestOptions().transform(new RoundedCorners(20)))
                    .into(imageView);
            textView.setText(gif.getDescription());
        } else {
            Glide
                    .with(MainActivity.this)
                    .asGif()
                    .load(R.drawable.error_race)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .listener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.INVISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    })
                    .apply(new RequestOptions().transform(new RoundedCorners(20)))
                    .into(imageView);
            textView.setText("Ошибка загрузки");
        }
    }

    @Override
    protected void onDestroy() {
        Glide.get(getApplicationContext()).clearMemory();
        new Thread(new Clear(getApplicationContext())).start();
        super.onDestroy();
    }
}