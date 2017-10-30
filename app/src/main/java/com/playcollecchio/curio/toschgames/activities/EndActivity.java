package com.playcollecchio.curio.toschgames.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.playcollecchio.curio.toschgames.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EndActivity extends AppCompatActivity implements View.OnClickListener
{
    Bitmap certificate;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        ImageView imageShare = (ImageView)findViewById(R.id.imageShare);
        ImageView imageSave = (ImageView)findViewById(R.id.imageSave);
        imageShare.setOnClickListener(this);
        imageSave.setOnClickListener(this);
        certificate = BitmapFactory.decodeResource(getResources(),
                R.drawable.mario);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.imageShare:
                share();
                break;
            case R.id.imageSave:
                save();
                break;
        }
    }
    public void share()
    {
        Uri imageUri = Uri.parse("android.resource://" + getPackageName()
                + "/drawable/" + "collecchio.jpg");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Guarda, sono stato a Collecchio!").setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri).setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Condividi questa grande esperienza"));
    }

    public void save()
    {

        MediaStore.Images.Media.insertImage(getContentResolver(), certificate, "Attestato di Collecchio" , "Questo attestato" +
                "dimostra che il soggetto ha completato correttamente il percorso per visitare Collecchio e dintorni");
        Toast.makeText(this,"Salvato",Toast.LENGTH_SHORT).show();
    }
}
