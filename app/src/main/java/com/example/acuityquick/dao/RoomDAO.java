package com.example.acuityquick.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.acuityquick.entities.HospitalRoom;

import java.util.List;

@Dao
public interface RoomDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HospitalRoom hospitalRoom);

    @Update
    void update(HospitalRoom hospitalRoom);

    @Delete
    void delete(HospitalRoom hospitalRoom);

    @Query("SELECT * FROM rooms ORDER BY id ASC")
    List<HospitalRoom> getAllRooms();

    @Query("SELECT * FROM rooms WHERE room_number = :roomNumber LIMIT 1")
    HospitalRoom findByRoomNumber(int roomNumber);

}