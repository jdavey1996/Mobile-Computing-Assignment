package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResultsAdapter extends ArrayAdapter<RecipeConstructor> {
    Activity activity;
    Context ctx;
    Boolean loadedFromCache;
    public SearchResultsAdapter(Activity activity, Context ctx, ArrayList<RecipeConstructor > data,Boolean loadedFromCache) {
        super(activity, 0, data);
        this.activity = activity;
        this.ctx = ctx;
        this.loadedFromCache = loadedFromCache;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        //Inflate layout using recipe list item layout.
        LayoutInflater taskInflater = LayoutInflater.from(getContext());
        if (view == null) {
            view = taskInflater.inflate(R.layout.recipe_list_item, parent, false);
        }

        //Get item at current position.
        final RecipeConstructor item = getItem(position);

        //Set recipeTitle TextView with data from arraylist.
        TextView recipeTitle = (TextView) view.findViewById(R.id.recipeTitle);
        recipeTitle.setText(item.getTitle());

        //Set recipeReadyIn TextView with data from arraylist.
        TextView recipeReadyIn = (TextView) view.findViewById(R.id.recipeReadyIn);
        recipeReadyIn.setText("Ready in "+item.getReadyInMinutes()+" minutes");

        //Set recipeThumbnail ImageVie the correct cached image based on the corresponding recipeId.
        ImageView recipeThumbnail = (ImageView)view.findViewById(R.id.recipeThumbnail);
        Storage getImage = new Storage();

        String imagePath;
        if (loadedFromCache) {
            imagePath = item.getId() + "_thumbnail";

        }else
        {
            imagePath = item.getId() + "_thumbnail_temp";
        }

        recipeThumbnail.setImageBitmap(getImage.getTepImg(ctx,imagePath));
        //Onclick method for clicking a list item.
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadedFromCache)
                {
                    //Get data from database and start intent to display it.
                    SQLiteDb sql = new SQLiteDb(ctx);
                    Cursor c1 = sql.getTABLE_RECIPE_INGREDIENTS(item.getId());
                    ArrayList<RecipeIngredientsConstructor> ingredients = new ArrayList<RecipeIngredientsConstructor>();
                    while (c1.moveToNext()) {

                        ingredients.add(new RecipeIngredientsConstructor(c1.getString(2),c1.getString(3),c1.getString(4)));
                    }

                    Cursor c2 = sql.getTABLE_RECIPE_INSTRUCTIONS(item.getId());
                    ArrayList<String> instructions = new ArrayList<String>();
                    while (c2.moveToNext()) {

                        instructions.add(c2.getString(2));
                    }

                    Intent intent = new Intent(ctx, RecipeActivity.class);
                    intent.putExtra("loadedFromCache",true);
                    intent.putExtra("recipeId", item.getId());
                    intent.putExtra("title", item.getTitle());
                    intent.putExtra("readyIn", item.getReadyInMinutes());
                    intent.putStringArrayListExtra("steps", instructions);
                    intent.putParcelableArrayListExtra("ingredients",ingredients);
                    ctx.startActivity(intent);

                }
                else {
                    RecipeDetailAsync getRecipeDetail = new RecipeDetailAsync(activity, ctx);
                    if (getRecipeDetail.getStatus() != AsyncTask.Status.RUNNING) {
                        //http://stackoverflow.com/questions/30618600/asynctask-takes-a-long-time-before-entering-doinbackground
                        getRecipeDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getId(), item.getTitle(), item.getReadyInMinutes());
                    } else {
                        Toast.makeText(getContext(), "running", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }
}

