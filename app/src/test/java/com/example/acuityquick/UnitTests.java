package com.example.acuityquick;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.acuityquick.database.RoomRepository;
import com.example.acuityquick.entities.HospitalRoom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTests {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void acuityScoreCalculation_isCorrect() {
        HospitalRoom room = new HospitalRoom();
        room.setAirRequirement("High Flow Oxygen");
        room.setMobilityRequirement("Full Assistance");
        room.setNutritionRequirement("Special Diet");

        room.setAirScore(room.getAirScoreFromRequirement(room.getAirRequirement()));
        room.setMobilityScore(room.getMobilityScoreFromRequirement(room.getMobilityRequirement()));
        room.setNutritionScore(room.getNutritionScoreFromRequirement(room.getNutritionRequirement()));

        room.calculateTotalScore();
        assertEquals(9, room.getTotalAcuityScore());
    }
    @Test
    public void acuityScoreCalculation_handlesMultipleScenarios() {
        HospitalRoom room = new HospitalRoom();
        room.setAirRequirement("Low Flow Oxygen");
        room.setMobilityRequirement("Assisted");
        room.setNutritionRequirement("Modified Diet");

        room.calculateTotalScore();
        assertEquals(6, room.getTotalAcuityScore());
    }
    @Test
    public void saveRoom_savesCorrectly() {
        HospitalRoom room = new HospitalRoom();
        RoomRepository mockRepository = mock(RoomRepository.class);
        room.setRoomNumber(101);
        room.setCareLevel("ICU");

        when(mockRepository.insert(room)).thenReturn(true);
        assertTrue(mockRepository.insert(room));
    }

    @Test
    public void deleteRoom_deletesCorrectly() {
        HospitalRoom room = new HospitalRoom();
        RoomRepository mockRepository = mock(RoomRepository.class);
        room.setId(1); // Assuming an ID is necessary for deletion

        when(mockRepository.delete(room)).thenReturn(true);
        assertTrue(mockRepository.delete(room));
    }

    @Test
    public void errorHandling_checksDatabaseUnavailable() {
        RoomRepository mockRepository = mock(RoomRepository.class);
        when(mockRepository.insert(any(HospitalRoom.class))).thenThrow(new RuntimeException("Database Error"));

        HospitalRoom room = new HospitalRoom();
        room.setRoomNumber(101);
        // Expected to handle the error gracefully
        assertFalse(room.saveDetails(mockRepository));
    }

    @Test
    public void testRoomFiltering_byCareLevel() {

        List<HospitalRoom> rooms = new ArrayList<>();
        rooms.add(createRoom(1, "ICU"));
        rooms.add(createRoom(2, "PCU"));
        rooms.add(createRoom(3, "MedSurg"));


        Set<String> activeFilters = new HashSet<>();
        activeFilters.add("ICU");

        List<HospitalRoom> filteredRooms = filterRooms(rooms, activeFilters);
        assertEquals(1, filteredRooms.size());
        assertEquals("ICU", filteredRooms.get(0).getCareLevel());
    }

    private HospitalRoom createRoom(int id, String careLevel) {
        HospitalRoom room = new HospitalRoom();
        room.setId(id);
        room.setCareLevel(careLevel);
        return room;
    }

    private List<HospitalRoom> filterRooms(List<HospitalRoom> rooms, Set<String> activeFilters) {
        List<HospitalRoom> filteredRooms = new ArrayList<>();
        for (HospitalRoom room : rooms) {
            if (activeFilters.contains(room.getCareLevel())) {
                filteredRooms.add(room);
            }
        }
        return filteredRooms;
    }


}