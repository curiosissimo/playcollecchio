package com.playcollecchio.curio.toschgames.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.playcollecchio.curio.toschgames.R;
import com.playcollecchio.curio.toschgames.models.Story;

/**
 * Created by Utente on 28/10/2017.
 */

public class StoryViewHolder extends RecyclerView.ViewHolder
{
    public TextView nameStory;
    public TextView desciptionStory;
    public ImageView imageStory;
    public View itemView;
    public StoryViewHolder(View itemView)
    {
        super(itemView);
        this.itemView = itemView;
        nameStory = (TextView) itemView.findViewById(R.id.nameStory);
        desciptionStory = (TextView) itemView.findViewById(R.id.desciptionStory);
        imageStory = (ImageView) itemView.findViewById(R.id.imageStory);
    }

    public void bind(Story story)
    {
        nameStory.setText(story.name);
        desciptionStory.setText(story.description);
        int idImage =  itemView.getContext().getResources().getIdentifier (story.image, "drawable", itemView.getContext().getPackageName());
        imageStory.setImageResource(idImage);
    }
}
