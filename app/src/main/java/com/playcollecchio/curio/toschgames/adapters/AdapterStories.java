package com.playcollecchio.curio.toschgames.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playcollecchio.curio.toschgames.R;
import com.playcollecchio.curio.toschgames.models.Story;
import com.playcollecchio.curio.toschgames.viewholder.StoryViewHolder;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by Utente on 28/10/2017.
 */

public class AdapterStories extends RealmRecyclerViewAdapter<Story,StoryViewHolder>
{
    public AdapterStories(RealmResults<Story> stories)
    {
        super(stories, true, true);
    }

    @Override
    public StoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.viewholderstorylayout,parent,false);
        StoryViewHolder storyViewHolder = new StoryViewHolder(view);
        return storyViewHolder;
    }

    @Override
    public void onBindViewHolder(StoryViewHolder holder, int position)
    {
        holder.bind(getItem(position));
    }
}
