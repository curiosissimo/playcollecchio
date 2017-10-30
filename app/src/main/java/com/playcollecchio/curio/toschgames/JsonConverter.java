package com.playcollecchio.curio.toschgames;

import android.content.Context;

import com.playcollecchio.curio.toschgames.models.Place;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playcollecchio.curio.toschgames.models.Story;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Utente on 26/10/2017.
 */

public class JsonConverter
{
    public static  ArrayList createPOJO(String json, String key)
    {
        ArrayList results=null;
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            switch (key)
            {
                case "Place":
                    results = mapper.readValue(json, new TypeReference<ArrayList<Place>>() { } );
                    break;
                case "Story":
                    results = mapper.readValue(json, new TypeReference<ArrayList<Story>>() { } );
                    break;
            }
            return results;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String loadJSONFromAsset(Context context, String file)
    {
        String json = "";
        try
        {

            InputStream is = context.getAssets().open(file);
            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
