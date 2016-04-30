package com.example.mmagdy_pc.movieguide;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends ActionBarActivity implements ListInfoListener
{

public boolean mTwoPane ;
    public static SQLiteDatabase sql ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        sql = openOrCreateDatabase("favoritesdb",0, null);
            Log.w("creating Main " , " here");
        FrameLayout flPanel2 = (FrameLayout) findViewById(R.id.fl_panel2);
        if(null == flPanel2)
        {
            mTwoPane = false;
        }
        else {
            mTwoPane = true ;
        }

        MainActivityFragment mainActivityFragment = new MainActivityFragment();
        mainActivityFragment.setListInfoListenter(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_panel1,mainActivityFragment)
                    .commit();
        }

            //setContentView(R.layout.fragment_main);

            //updateData();

            //setHasOptionsMenu(true) ;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void setSelectedList(Information info) {
        if (mTwoPane)
        {
            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            Bundle extra =  new Bundle() ;
            extra.putString("t1", info.Title);
            extra.putString("t2", info.Date);
            extra.putString("t3", info.Vote);

            extra.putString("t4", info.OverView);

            extra.putString("pic", info.PIC);
            extra.putInt("id", info.id);
            detailActivityFragment.setArguments(extra);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_panel2 ,detailActivityFragment).commit();

        }
        else {

            Intent intent = new Intent(this, DetailActivity.class);
            //intent.putExtra("t1", MovieData.get(position).Title);
            intent.putExtra("t1", info.Title);
            intent.putExtra("t2", info.Date);
            intent.putExtra("t3", info.Vote);

            intent.putExtra("t4", info.OverView);

            intent.putExtra("pic", info.PIC);
            intent.putExtra("id", info.id);


            startActivity(intent);
        }

    }
}
