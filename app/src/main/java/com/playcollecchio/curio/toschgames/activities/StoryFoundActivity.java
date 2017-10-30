package com.playcollecchio.curio.toschgames.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.playcollecchio.curio.toschgames.Constants;
import com.playcollecchio.curio.toschgames.R;
import com.playcollecchio.curio.toschgames.models.Story;

import io.realm.Realm;

public class StoryFoundActivity extends AppCompatActivity implements Realm.Transaction
{
    private Story story;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewholderstorylayout);
        story = (Story) getIntent().getSerializableExtra(Constants.SERIZIABLE_KEY_MONUMENT);
        setAllComponents();
        Realm realm = Realm.getDefaultInstance();
        if(realm.where(Story.class).equalTo("id",story.id).findAll().size()==0)
            realm.executeTransaction(this);
        realm.close();
    }

    public void setAllComponents()
    {
        TextView nameStroy = (TextView) findViewById(R.id.nameStory);
        TextView descriptionStory = (TextView) findViewById(R.id.desciptionStory);
        ImageView imageStory = (ImageView) findViewById(R.id.imageStory);
        nameStroy.setText(story.name);
        descriptionStory.setText(story.description);
        int idImage =  getResources().getIdentifier (story.image, "drawable", getPackageName());
        imageStory.setImageResource(idImage);
    }

    @Override
    public void execute(Realm realm)
    {
        realm.insertOrUpdate(story);
    }
}
