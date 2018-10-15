package com.example.user.wifistaterecord.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {WiFIStateData.class},version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WiFiStarteDao dao();

    private static AppDatabase appDatabase;
    public static AppDatabase getI(Context context){
        if(appDatabase == null){
            appDatabase = Room.databaseBuilder(context,AppDatabase.class,"data.db")
                    .allowMainThreadQueries()
                    .build();
        }
        return appDatabase;
    }

}
