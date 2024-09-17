package com.example.acuityquick.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.acuityquick.dao.RoomDAO;
import com.example.acuityquick.entities.HospitalRoom;

// Annotation to define the entities and version
@Database(entities = {HospitalRoom.class}, version = 1, exportSchema = false)
public abstract class MyDatabaseBuilder extends RoomDatabase {
    public abstract RoomDAO roomDAO();

    // Singleton instance
    private static volatile MyDatabaseBuilder INSTANCE;

    // Method to get the singleton instance of the database
    public static MyDatabaseBuilder getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyDatabaseBuilder.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MyDatabaseBuilder.class, "MyDatabase.db")
                            .fallbackToDestructiveMigration() // Handle migrations
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}