package com.example.acuityquick.UI;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acuityquick.R;
import com.example.acuityquick.database.MyDatabaseBuilder;
import com.example.acuityquick.database.RoomRepository;
import com.example.acuityquick.entities.HospitalRoom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoomList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private RoomRepository roomRepository;
    private List<HospitalRoom> allHospitalRooms;
    private List<HospitalRoom> filteredRooms;
    private ToggleButton icuToggle, pcuToggle, medSurgToggle;
    private Set<String> activeCareLevels;
    private Spinner roomNumberSortSpinner, acuityScoreSortSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize repository and fetch data
        roomRepository = new RoomRepository(getApplication());
        allHospitalRooms = roomRepository.getAllRooms(); // Ensure this method works synchronously

        // Initialize UI components
        recyclerView = findViewById(R.id.roomListRecyclerView);
        roomAdapter = new RoomAdapter(this);
        recyclerView.setAdapter(roomAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize toggles and set listeners
        icuToggle = findViewById(R.id.icuToggle);
        pcuToggle = findViewById(R.id.pcuToggle);
        medSurgToggle = findViewById(R.id.medSurgToggle);
        activeCareLevels = new HashSet<>();

        // Initialize spinners and their listeners
        roomNumberSortSpinner = findViewById(R.id.roomNumberSortSpinner);
        acuityScoreSortSpinner = findViewById(R.id.acuityScoreSortSpinner);
        setupSpinnerListeners();

        // Set default sorting order for Acuity Score to Descending
        acuityScoreSortSpinner.setSelection(2); // Assuming the third item is 'Descending'

        setupToggleButtonListeners();
        filterRooms();
        sortRooms(); // Initial filtering

        Button addRoomButton = findViewById(R.id.addRoomButton);
        addRoomButton.setOnClickListener(v -> {
            Intent intent = new Intent(RoomList.this, RoomDetails.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh allHospitalRooms when resuming the activity
        allHospitalRooms = roomRepository.getAllRooms();
        filterRooms();
        sortRooms();
        acuityScoreSortSpinner.setSelection(2); // Ensure default setting is applied on resume
        sortRooms(); // Apply default sorting on resume
    }

    private void setupToggleButtonListeners() {
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String careLevel = null;

                if (buttonView == icuToggle) {
                    careLevel = "ICU";
                } else if (buttonView == pcuToggle) {
                    careLevel = "PCU";
                } else if (buttonView == medSurgToggle) {
                    careLevel = "MedSurg";
                }

                if (isChecked && careLevel != null) {
                    activeCareLevels.add(careLevel);
                } else {
                    activeCareLevels.remove(careLevel);
                }

                filterRooms();
                sortRooms();
            }
        };

        icuToggle.setOnCheckedChangeListener(listener);
        pcuToggle.setOnCheckedChangeListener(listener);
        medSurgToggle.setOnCheckedChangeListener(listener);
    }
    private void setupSpinnerListeners() {
        roomNumberSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortRooms();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        acuityScoreSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortRooms();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void sortRooms() {
        if (filteredRooms == null || filteredRooms.isEmpty()) {
            filteredRooms = new ArrayList<>(allHospitalRooms); // fallback to all rooms if no filtering applied
        }

        // Room number sorting
        int roomSortOrder = roomNumberSortSpinner.getSelectedItemPosition();
        switch (roomSortOrder) {
            case 1: // Ascending
                Collections.sort(filteredRooms, (o1, o2) -> Integer.compare(o1.getRoomNumber(), o2.getRoomNumber()));
                break;
            case 2: // Descending
                Collections.sort(filteredRooms, (o1, o2) -> Integer.compare(o2.getRoomNumber(), o1.getRoomNumber()));
                break;
        }

        // Acuity score sorting
        int scoreSortOrder = acuityScoreSortSpinner.getSelectedItemPosition();
        switch (scoreSortOrder) {
            case 1: // Ascending
                Collections.sort(filteredRooms, (o1, o2) -> Integer.compare(o1.getTotalAcuityScore(), o2.getTotalAcuityScore()));
                break;
            case 2: // Descending
                Collections.sort(filteredRooms, (o1, o2) -> Integer.compare(o2.getTotalAcuityScore(), o1.getTotalAcuityScore()));
                break;
        }

        // Notify the adapter of the changes
        roomAdapter.setHospitalRooms(filteredRooms);
    }

    private void filterRooms() {
        filteredRooms = new ArrayList<>();
        for (HospitalRoom room : allHospitalRooms) {
            if (activeCareLevels.isEmpty() || activeCareLevels.contains(room.getCareLevel())) {
                filteredRooms.add(room);
            }
        }
        roomAdapter.setHospitalRooms(filteredRooms);
    }

}