package com.example.mmagdy_pc.movieguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */

public class MainActivityFragment extends Fragment {


    private GridView grid ;
    private ImageAdapter imgrid  ;
    public List<Information> MovieData  = new ArrayList<Information>();
    public List<Information> MovieData2  = new ArrayList<Information>();
    public List<String> MoviePic  = new ArrayList<String>();
    public List<String> MovieTitle  = new ArrayList<String>();
    public static SQLiteDatabase sql ;

    DataBase db;
    ListInfoListener fListener ;
    View rootView;


    public MainActivityFragment() {

    }



   @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater Inflater) {

        Inflater.inflate(R.menu.moviesfragment, menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int Id = item.getItemId();
        if(Id ==R.id.action_refresh)
        {
            updatePage();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        Log.w("creating Main fragment ", " here");
         rootView = inflater.inflate(R.layout.fragment_main, container, false);
        grid = (GridView) rootView.findViewById(R.id.grid1);
        db = new DataBase(MainActivity.sql);
        db.createTable();


        Log.w("Top or Most here", " ");
        return rootView ;
    }

    public void updatePage() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String kindofdata = prefs.getString(getString(R.string.pref_kind_key),
                getString(R.string.pref_kind_most_popular));
        FetchMoviesTask moviesTask = new FetchMoviesTask() ;
        Log.w("Top or Most here", kindofdata);
        moviesTask.execute(kindofdata);



    }

    @Override
    public void onStart()
    {
        super.onStart();

        updatePage();
    }


    public class FetchMoviesTask extends AsyncTask<String ,Void ,List<Information> > {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName() ;







        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private List <Information> getMoviesDataFromJson(String MoviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_PIC = "poster_path";
            final String TMDB_OverView = "overview";
            final String TMDB_Date = "release_date";
            final String TMDB_Title = "title";
            final String TMDB_Vote = "vote_average";
            final String TMDB_ID = "id";

            final String MOVIE_PIC_BASE_URL = "http://image.tmdb.org/t/p/w185//";



            JSONObject MoviesJson = new JSONObject(MoviesJsonStr);
            JSONArray MoviesArray = MoviesJson.getJSONArray(TMDB_RESULTS);

            MovieData.clear() ;
            MovieData2.clear() ;
            for(int i = 0; i < MoviesArray.length(); i++) {
                Information movie1 = new Information();
                JSONObject Movie = MoviesArray.getJSONObject(i);
                movie1.PIC = MOVIE_PIC_BASE_URL+Movie.getString(TMDB_PIC);

                MovieTitle.add( Movie.getString(TMDB_Title));
                movie1.Title= Movie.getString(TMDB_Title);
                movie1.OverView= Movie.getString(TMDB_OverView);
                movie1.Vote= Movie.getString(TMDB_Vote);
                movie1.Date= Movie.getString(TMDB_Date);
                movie1.id= Movie.getInt(TMDB_ID);

                MovieData.add(movie1) ;
                MovieData2.add(movie1) ;



            }
            Log.v("data coming " , MovieTitle.get(0));


            return MovieData;
        }

        @Override

        protected List <Information> doInBackground(String... params) {



            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String format = "json";



            if(params[0].equals(getString(R.string.pref_kind_favorites)))
            {

                return db.getAll();
            }

            try {

                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //http://api.themoviedb.org/3/movie/popular?api_key=179a8cf9fc6fab0def62671610a2704b
                final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/"+params[0];



                final String APPID_PARAM ="api_key";


                Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();


                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movies JSON String " + moviesJsonStr);

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try{

                return getMoviesDataFromJson(moviesJsonStr);


            }
            catch (JSONException e)
            {
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();


            }


            return null;
        }
        @Override
        protected void onPostExecute(List <Information> result)
        {
            MovieData2.clear();
            if(result != null)
            {

                //MovieData.clear();
                //MoviePic.clear();

                for(int i = 0 ; i < result.size() ; i++)
                {
                    MovieData2.add(result.get(i));

                    //MovieData.add(result.get(i));
                    //MoviePic.add(result.get(i).PIC);
                }
                //MovieData2 = result ;

            }
            // making the grid here :



            imgrid = new ImageAdapter(getActivity() , MovieData2);
            grid.setAdapter(imgrid);
            Log.v("Grid is made here","grid");
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(MainActivity.this, "Hello MAL " + position, Toast.LENGTH_SHORT).show();

                    fListener.setSelectedList(MovieData2.get(position));


                }
            });

        }

    } ;

    //setter for listener
    public void setListInfoListenter(ListInfoListener lsn)
    {
        fListener = lsn  ;
    }



    //the custom adapter

    public class ImageAdapter extends BaseAdapter
    {


        List <Information> list = new ArrayList<Information> ();
        private Context mContext ;
        public ImageAdapter (Context c , List <Information >s)
        {
            mContext = c ;
            list =s ;

        }



        @Override
        public int getCount() {


            return list.size();
            //return MovieData2.size();
        }

        @Override
        public Object getItem(int position) {


            return list.get(position);
           // return MovieData2.get(position).PIC;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView view ;
            if (convertView == null) {
                view = new ImageView(mContext);

                convertView = view ;
                view.setPadding(1,1,1,1);
            }
            else {
                view = (ImageView) convertView ;
            }

            Picasso.with(mContext)
                    .load(list.get(position).PIC)
                    //.load(MovieData2.get(position).PIC)
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .into(view);




            return view;
        }
    }




}
