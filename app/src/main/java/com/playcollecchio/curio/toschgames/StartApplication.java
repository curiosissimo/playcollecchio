package com.playcollecchio.curio.toschgames;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Utente on 28/10/2017.
 */

public class StartApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Realm.init(getApplicationContext());
    }
}
