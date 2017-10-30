package com.playcollecchio.curio.toschgames.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.playcollecchio.curio.toschgames.Constants;
import com.playcollecchio.curio.toschgames.R;
import com.playcollecchio.curio.toschgames.fragments.CollecchioMapFragment;
import com.playcollecchio.curio.toschgames.fragments.QrCodeFragment;
import com.playcollecchio.curio.toschgames.fragments.StoriesFragment;
import com.playcollecchio.curio.toschgames.models.Story;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
{
    private BottomNavigationView mBottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        mBottomNavigationView.inflateMenu(R.menu.menunavigation);
        startFragment(new CollecchioMapFragment());
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Realm realm = Realm.getDefaultInstance();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if(Constants.NUMBER_OF_PLACES==realm.where(Story.class).findAll().size() && sharedPref.getBoolean(Constants.KEY_GAME_COMPLETED,false))
        {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(Constants.KEY_GAME_COMPLETED,true);
            editor.commit();
            //TODO:NEGARE LA SECONDA OPZIONE
            startActivity(new Intent(this,EndActivity.class));
        }
        realm.close();
    }

    public void startFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.codeItem:
                startFragment(new QrCodeFragment());
                break;
            case R.id.mapItem:
                startFragment(new CollecchioMapFragment());
                break;
            case R.id.storyItem:
                startFragment(new StoriesFragment());
                break;
        }
        return true;
    }

}
