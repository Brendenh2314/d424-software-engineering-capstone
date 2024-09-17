package com.example.acuityquick.UI;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acuityquick.R;
import com.example.acuityquick.entities.HospitalRoom;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewholder> {
    private List<HospitalRoom> mHospitalRooms;
    private final Context context;
    private final LayoutInflater mInflator;

    public RoomAdapter(Context context) {
        mInflator = LayoutInflater.from(context);
        this.context = context;
    }

    public class RoomViewholder extends RecyclerView.ViewHolder {
        private final TextView levelOfCareItemView;
        private final TextView roomNumberItemView;
        private final TextView totalAcuityScoreItemView;

        public RoomViewholder(@NonNull View itemView) {
            super(itemView);
            levelOfCareItemView = itemView.findViewById(R.id.levelOfCareTextView);
            roomNumberItemView = itemView.findViewById(R.id.roomNumberTextView);
            totalAcuityScoreItemView = itemView.findViewById(R.id.totalAcuityScoreTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) { // Check that the position is valid
                        final HospitalRoom current = mHospitalRooms.get(position);
                        Log.d("RoomAdapter", "Clicked room: " + current.getRoomNumber()); // Log the room number

                        Intent intent = new Intent(context, RoomDetails.class);
                        intent.putExtra("roomId", current.getId());
                        intent.putExtra("roomNumber", String.valueOf(current.getRoomNumber()));
                        intent.putExtra("careLevel", current.getCareLevel());
                        intent.putExtra("airRequirement", current.getAirRequirement());
                        intent.putExtra("airScore", current.getAirScore());
                        intent.putExtra("mobilityRequirement", current.getMobilityRequirement());
                        intent.putExtra("mobilityScore", current.getMobilityScore());
                        intent.putExtra("nutritionRequirement", current.getNutritionRequirement());
                        intent.putExtra("nutritionScore", current.getNutritionScore());
                        intent.putExtra("totalAcuityScore", current.getTotalAcuityScore());

                        // Start the activity
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
    @NonNull
    @Override
    public RoomAdapter.RoomViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflator.inflate(R.layout.room_list_item,parent,false);
        return new RoomViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomAdapter.RoomViewholder holder, int position) {
        if (mHospitalRooms != null) {
            HospitalRoom currentRoom = mHospitalRooms.get(position);

            // Check if each value is available and set corresponding TextViews
            if (currentRoom.getCareLevel() != null) {
                holder.levelOfCareItemView.setText(currentRoom.getCareLevel());
            } else {
                holder.levelOfCareItemView.setText("N/A");
            }

            if (currentRoom.getRoomNumber() != 0) {
                holder.roomNumberItemView.setText(String.valueOf(currentRoom.getRoomNumber()));
            } else {
                holder.roomNumberItemView.setText(""); // Leave empty if room number is not available
            }

            if (currentRoom.getTotalAcuityScore() != 0) {
                holder.totalAcuityScoreItemView.setText(String.valueOf(currentRoom.getTotalAcuityScore()));
            } else {
                holder.totalAcuityScoreItemView.setText("N/A");
            }
        } else {
            // If the list is null, display default text for each TextView
            holder.levelOfCareItemView.setText("N/A");
            holder.roomNumberItemView.setText(""); // Leave empty for room number if list is null
            holder.totalAcuityScoreItemView.setText("N/A");
        }
    }



    @Override
    public int getItemCount() {
        if(mHospitalRooms != null){
            return mHospitalRooms.size();
        }
        else return 0;
    }

    public void setHospitalRooms(List<HospitalRoom> hospitalRooms){
        mHospitalRooms = hospitalRooms;
        notifyDataSetChanged();
    }


}
