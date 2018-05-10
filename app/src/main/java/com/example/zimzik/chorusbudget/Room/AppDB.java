package com.example.zimzik.chorusbudget.Room;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Member.class}, version = 1, exportSchema = false)
public abstract class AppDB extends RoomDatabase {
    public abstract MemberDao memberDao();

    private static AppDB sInstance;

    public synchronized static AppDB getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDB.class, "chorusMembers.db").build();
        }
        return sInstance;
    }
}
