package com.example.mmagdy_pc.movieguide;

import android.provider.BaseColumns;

/**
 * Created by M.Magdy-pc on 4/29/2016.
 */
public class MovieContract {

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorites";


        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_PIC_LINK = "pic_link";
        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_VOTE = "vote";
        public static final String COLUMN_OVERVIEW = "over_view";
        public static final String COLUMN_DATE = "date";



    }
}
