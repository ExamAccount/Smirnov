package com.example.tinkoffexamapp;

import android.content.Context;

import com.bumptech.glide.Glide;

public class Clear implements Runnable{

   private Context context;

    public Clear(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Glide.get(context).clearDiskCache();
    }
}
