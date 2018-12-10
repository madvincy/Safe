package com.example.madvincy.safe.parking_place_account_activities.CarParkHistory.ParkingHistoryRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.madvincy.safe.R;
import com.example.madvincy.safe.parking_place_account_activities.CarParkHistory.ParkingHistorySingleActivity;

public class ParkHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView parkingId;
    public TextView time;
    public ParkHistoryViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        parkingId = (TextView) itemView.findViewById(R.id.parkingId);
        time = (TextView) itemView.findViewById(R.id.time);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), ParkingHistorySingleActivity.class);
        Bundle b = new Bundle();
        b.putString("parkingId", parkingId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);
    }
}
