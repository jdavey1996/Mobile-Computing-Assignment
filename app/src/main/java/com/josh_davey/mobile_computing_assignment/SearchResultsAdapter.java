package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/*References:
    https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 */
public class SearchResultsAdapter extends ArrayAdapter<RecipeConstructor> {
    //Variables.
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
        //Inflate view for recipe item.
        LayoutInflater taskInflater = LayoutInflater.from(getContext());
        if (view == null) {
            view = taskInflater.inflate(R.layout.recipe_list_item, parent, false);
        }

        //Get item at current position.
        final RecipeConstructor item = getItem(position);

        //Set textfields to the correct values based on the position as the arraylist is looped through.
        TextView recipeTitle = (TextView) view.findViewById(R.id.recipeTitle);
        recipeTitle.setText(item.getTitle());

        TextView recipeReadyIn = (TextView) view.findViewById(R.id.recipeReadyIn);
        recipeReadyIn.setText("Ready in "+item.getReadyInMinutes()+" minutes");

        //Set recipeThumbnail ImageView to the correct cached image based on the corresponding recipeId.
        ImageView recipeThumbnail = (ImageView)view.findViewById(R.id.recipeThumbnail);
        Storage getImage = new Storage();
        //Check if image was loaded from cache or just viewed via search query. Then set correct name of image in imagePath.
        String imagePath;
        if (loadedFromCache) {
            //If loaded from cache, get and set image for this recipe from permanent internal storage location.
            imagePath = item.getId() + "_thumbnail";
            recipeThumbnail.setImageBitmap(getImage.getImg(ctx,imagePath,false));
        }else
        {
            //If not loaded from cache, get and set image for this recipe from internal cache directory location.
            imagePath = item.getId() + "_thumbnail_temp";
            recipeThumbnail.setImageBitmap(getImage.getImg(ctx,imagePath,true));
        }

        //Onclick method for clicking a list item.
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if image was loaded from cache or just viewed via search query.
                if (loadedFromCache)
                {
                    //Get recipe data from cache (SQLite Database).
                    SQLiteGetRecipeAsync sqLiteGetRecipeAsync = new SQLiteGetRecipeAsync(ctx);
                    sqLiteGetRecipeAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,item.getId(), item.getTitle(),item.getReadyInMinutes());

                }
                else {
                    //Checks if device is connected to either a wifi network or mobile network.
                    NetworkStatus networkStatus = new NetworkStatus(getContext());
                    if(networkStatus.checkConnection()) {
                        //Download details about the the selected recipe.
                        RecipeDetailAsync getRecipeDetail = new RecipeDetailAsync(activity, ctx);
                        getRecipeDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getId(), item.getTitle(), item.getReadyInMinutes());
                    }
                }
            }
        });
        return view;
    }
}

