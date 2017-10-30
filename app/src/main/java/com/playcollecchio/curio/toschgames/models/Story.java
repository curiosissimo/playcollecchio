package com.playcollecchio.curio.toschgames.models;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Utente on 26/10/2017.
 */

public class Story extends RealmObject implements Serializable
{
    public String id;
    public String name;
    public String description;
    public String image;
    public boolean scanned;
}
