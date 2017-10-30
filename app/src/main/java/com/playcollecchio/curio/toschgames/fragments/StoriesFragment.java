package com.playcollecchio.curio.toschgames.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playcollecchio.curio.toschgames.R;
import com.playcollecchio.curio.toschgames.adapters.AdapterStories;
import com.playcollecchio.curio.toschgames.models.Story;

import io.realm.Realm;

/**
 * Created by Utente on 24/10/2017.
 */

public class StoriesFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if(Realm.getDefaultInstance()
                .where(Story.class)
                .findAll().size()>0)
            return inflater.inflate(R.layout.storiesfragmentlayout,container,false);
        else
            return inflater.inflate(R.layout.layoutzerostories,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if(Realm.getDefaultInstance()
                .where(Story.class)
                .findAll().size()>0)
        {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewStories);
            AdapterStories allStories = new AdapterStories(Realm.getDefaultInstance()
                    .where(Story.class)
                    .findAll());
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(allStories);
            recyclerView.addItemDecoration(new SimpleDecorator());
        }
    }

    private class SimpleDecorator extends RecyclerView.ItemDecoration
    {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            int position = parent.getChildAdapterPosition(view);
            if (position > 0)
            {
                DisplayMetrics metrics = view.getContext().getResources().getDisplayMetrics();
                outRect.top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, metrics);
            }
            else
                outRect.top = 0;
        }
    }
}
