package com.example.acuityquick.UI;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.acuityquick.R;
import com.example.acuityquick.database.RoomRepository;
import com.example.acuityquick.entities.HospitalRoom;

import java.util.Arrays;
import java.util.List;

public class RoomDetails extends AppCompatActivity {
    private EditText roomNumberEditText;
    private Spinner careLevelSpinner, airRequirementSpinner, mobilityRequirementSpinner, nutritionRequirementSpinner;
    private TextView totalAcuityScoreTextView;
    private Button saveButton;
    private Button deleteButton;
    private Button shareButton;
    private Button copyButton;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private RoomRepository roomRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room_details);
        roomRepository = new RoomRepository(getApplication());

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeUIComponents();
        loadIntentData();

        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRoomDetails();
            }
        });
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRoom();
            }
        });

        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareRoomDetails();
            }
        });
        copyButton = findViewById(R.id.copyButton);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyRoomDetailsToClipboard();
            }
        });
    }
    private void initializeUIComponents() {
        roomNumberEditText = findViewById(R.id.roomNumberEditText);
        careLevelSpinner = findViewById(R.id.careLevelSpinner);
        airRequirementSpinner = findViewById(R.id.airRequirementSpinner);
        mobilityRequirementSpinner = findViewById(R.id.mobilityRequirementSpinner);
        nutritionRequirementSpinner = findViewById(R.id.nutritionRequirementSpinner);
        totalAcuityScoreTextView = findViewById(R.id.totalAcuityScoreTextView);

        setupSpinners(); // This should setup your spinners with the required data
    }

    private void setupSpinners() {
        // Example arrays could be defined in strings.xml or right here in the code.
        String[] careLevels = {"Select Care Level", "ICU", "PCU", "MedSurg"};
        String[] airRequirements = {"Select Air Requirement", "Room Air", "Low Flow Oxygen", "High Flow Oxygen"};
        String[] mobilityRequirements = {"Select Mobility Requirement", "Independent", "Assisted", "Full Assistance"};
        String[] nutritionRequirements = {"Select Nutrition Requirement", "Normal Diet", "Modified Diet", "Special Diet"};

        setupSpinner(careLevelSpinner, careLevels);
        setupSpinner(airRequirementSpinner, airRequirements);
        setupSpinner(mobilityRequirementSpinner, mobilityRequirements);
        setupSpinner(nutritionRequirementSpinner, nutritionRequirements);
    }

    private void setupSpinner(Spinner spinner, String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    private void loadIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            String roomNumber = intent.getStringExtra("roomNumber");
            if (roomNumber != null) {
                roomNumberEditText.setText(roomNumber);
            } else {
                roomNumberEditText.setText("");  // or any other default or placeholder text
            }

            setSpinnerSelection(careLevelSpinner, intent.getStringExtra("careLevel"));
            setSpinnerSelection(airRequirementSpinner, intent.getStringExtra("airRequirement"));
            setSpinnerSelection(mobilityRequirementSpinner, intent.getStringExtra("mobilityRequirement"));
            setSpinnerSelection(nutritionRequirementSpinner, intent.getStringExtra("nutritionRequirement"));
            totalAcuityScoreTextView.setText(String.valueOf(intent.getIntExtra("totalAcuityScore", 0)));
        } else {
            Log.e("RoomDetails", "Intent was null, no data to load");
            // Optionally set all fields to default values here if the intent is null
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }

    private void saveRoomDetails() {
        try {
            final int roomNumber = Integer.parseInt(roomNumberEditText.getText().toString().trim());
            final HospitalRoom room = new HospitalRoom();
            room.setRoomNumber(roomNumber);
            room.setCareLevel(careLevelSpinner.getSelectedItem().toString());
            room.setAirRequirement(airRequirementSpinner.getSelectedItem().toString());
            room.setMobilityRequirement(mobilityRequirementSpinner.getSelectedItem().toString());
            room.setNutritionRequirement(nutritionRequirementSpinner.getSelectedItem().toString());
            room.calculateTotalScore(); // Calculate the total acuity score based on selected requirements
            totalAcuityScoreTextView.setText(String.valueOf(room.getTotalAcuityScore())); // Display the calculated total acuity score in the TextView

            // Check if this room number already exists
            List<HospitalRoom> allRooms = roomRepository.getAllRooms(); // Assume this method exists and synchronously returns all rooms
            boolean exists = false;
            for (HospitalRoom existingRoom : allRooms) {
                if (existingRoom.getRoomNumber() == roomNumber && existingRoom.getId() != getIntent().getIntExtra("roomId", -1)) {
                    exists = true;
                    break;
                }
            }

            if (exists) {
                // Prompt for confirmation
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Overwrite")
                        .setMessage("A room with this number already exists. Are you sure you want to overwrite it?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            saveOrUpdateRoom(room);
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                saveOrUpdateRoom(room);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error saving room: check your input formats", Toast.LENGTH_LONG).show();
        }


    }

    private void saveOrUpdateRoom(HospitalRoom room) {
        int roomId = getIntent().getIntExtra("roomId", -1);
        if (roomId == -1) { // No room ID passed, hence it's a new room
            roomRepository.insert(room);
            Log.d("RoomDetailsLog", "Room saved: " + room.toString() + " at " + dateFormatter.format(new Date()));
            Toast.makeText(this, "New room saved", Toast.LENGTH_SHORT).show();
        } else { // An existing room, update it
            room.setId(roomId);
            roomRepository.update(room);
            Log.d("RoomDetailsLog", "Room updated: " + room.toString() + " at " + dateFormatter.format(new Date()));
            Toast.makeText(this, "Room updated", Toast.LENGTH_SHORT).show();
        }
        setResult(RESULT_OK); // Indicate success

    }

    private void deleteRoom() {
        try {
            int roomId = getIntent().getIntExtra("roomId", -1);
            if (roomId != -1) {
                // Create a room object to delete
                HospitalRoom room = new HospitalRoom();
                room.setId(roomId);
                roomRepository.delete(room);
                Log.d("RoomDetailsLog", "Room Deleted: " + room.toString() + " at " + dateFormatter.format(new Date()));
                Toast.makeText(this, "Room deleted", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            } else {
                Toast.makeText(this, "Error: No room found to delete", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error deleting room: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void shareRoomDetails() {
        String roomDetails = "Room Details:\n" +
                "Room Number: " + roomNumberEditText.getText().toString() + "\n" +
                "Care Level: " + careLevelSpinner.getSelectedItem().toString() + "\n" +
                "Air Requirement: " + airRequirementSpinner.getSelectedItem().toString() + "\n" +
                "Mobility Requirement: " + mobilityRequirementSpinner.getSelectedItem().toString() + "\n" +
                "Nutrition Requirement: " + nutritionRequirementSpinner.getSelectedItem().toString() + "\n" +
                "Total Acuity Score: " + totalAcuityScoreTextView.getText().toString();

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, roomDetails);
        shareIntent.setType("text/plain");

        startActivity(Intent.createChooser(shareIntent, "Share Room Details Via:"));
    }

    private void copyRoomDetailsToClipboard() {
        String roomDetails = "Room Details:\n" +
                "Room Number: " + roomNumberEditText.getText().toString() + "\n" +
                "Care Level: " + careLevelSpinner.getSelectedItem().toString() + "\n" +
                "Air Requirement: " + airRequirementSpinner.getSelectedItem().toString() + "\n" +
                "Mobility Requirement: " + mobilityRequirementSpinner.getSelectedItem().toString() + "\n" +
                "Nutrition Requirement: " + nutritionRequirementSpinner.getSelectedItem().toString() + "\n" +
                "Total Acuity Score: " + totalAcuityScoreTextView.getText().toString();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Room Details", roomDetails);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Room details copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}