package com.example.mmagdy_pc.movieguide;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by M.Magdy-pc on 4/29/2016.
 */
public class DataBase {

    SQLiteDatabase db ;
    public DataBase (SQLiteDatabase sql2)
    {
        db = sql2 ;
    }
    public void createTable () {

        try {final String SQL_CREATE_MOVIE_TABLE = "create table if not exists " + MovieContract.MovieEntry.TABLE_NAME +
                 " ( " +
                MovieContract.MovieEntry.COLUMN_ID+" INTEGER PRIMARY KEY   AUTOINCREMENT,"+
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL ," +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_PIC_LINK + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE + " TEXT NOT NULL ); ";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public boolean insertDataInTable (Information info ) {
        if(!isHere(info.id)) {
            try {
                db.execSQL("insert into " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        "(" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "," +
                        MovieContract.MovieEntry.COLUMN_TITLE + "," +
                        MovieContract.MovieEntry.COLUMN_DATE + "," +
                        MovieContract.MovieEntry.COLUMN_OVERVIEW + "," +
                        MovieContract.MovieEntry.COLUMN_PIC_LINK + "," +
                        MovieContract.MovieEntry.COLUMN_VOTE + ") " +
                        " values " +
                        "('" + info.id + "', " +
                        "'" + info.Title + "'," +
                        "'" + info.Date + "'," +
                        "'" + info.OverView + "'," +
                        "'" + info.PIC + "'," +
                        "'" + info.Vote +
                        "'); ");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true ;
        }
        else {
            return false ;
        }
    }
    public List <Information> getAll ()
    {
        List <Information> arr = new ArrayList<Information>();
        Cursor cursor = db.rawQuery("select * from "+ MovieContract.MovieEntry.TABLE_NAME,null);
        if (cursor.getCount()> 0 )
        {
            while (cursor.moveToNext())
            {
                Information m = new Information();
                m.id=cursor.getInt(1);
                m.Title = cursor.getString(2);
                m.Date = cursor.getString(3);
                m.OverView = cursor.getString(4);
                m.PIC=cursor.getString(5);
                m.Vote= cursor.getString(6);
                arr.add(m);

            }

        }
        cursor.close();

        return arr ;
    }
    public  boolean isHere (int n )
    {
        boolean b = false ;
        SQLiteDatabase sq = db;
        String Query = "Select * from " +  MovieContract.MovieEntry.TABLE_NAME + " where " + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + n;
        Cursor cursor = sq.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }




}
