package com.example.acuityquick.database;

import android.app.Application;
import android.util.Log;

import com.example.acuityquick.dao.RoomDAO;
import com.example.acuityquick.entities.HospitalRoom;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoomRepository {
    private RoomDAO roomDAO;
    private List<HospitalRoom> allHospitalRooms;

    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public RoomRepository(Application application) {
        MyDatabaseBuilder db = MyDatabaseBuilder.getDatabase(application);
        roomDAO = db.roomDAO();
    }

    public List<HospitalRoom> getAllRooms() {
        databaseExecutor.execute(() -> {
            allHospitalRooms = roomDAO.getAllRooms();
        });

        try {
            Thread.sleep(1000);  // This sleep is used to wait for the data, consider using LiveData or callbacks for a better async handling
        } catch (InterruptedException e) {
            Log.e("RoomRepository", "Interrupted!", e);
        }
        return allHospitalRooms;
    }

    public boolean insert(HospitalRoom room) {
        databaseExecutor.execute(() -> {
            roomDAO.insert(room);
        });
        return true;
    }

    public void update(HospitalRoom room) {
        databaseExecutor.execute(() -> {
            roomDAO.update(room);
        });
    }

    public boolean delete(HospitalRoom room) {
        databaseExecutor.execute(() -> {
            roomDAO.delete(room);
        });
        return true;
    }
}