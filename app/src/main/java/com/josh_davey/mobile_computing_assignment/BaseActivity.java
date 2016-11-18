package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearRecent:
                SQLiteDb db = new SQLiteDb(this);
                db.clearDatabaseTables();

                Storage storage = new Storage();
                storage.clearImgCache(this,false);
                return true;

            case R.id.unlinkTwitter:
                Tweets tweet = new Tweets(this,this,null,null,null);
                tweet.unlinkTwitter();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
