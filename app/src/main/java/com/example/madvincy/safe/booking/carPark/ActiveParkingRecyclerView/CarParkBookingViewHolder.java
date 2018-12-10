package com.example.madvincy.safe.booking.carPark.ActiveParkingRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.madvincy.safe.R;
import com.example.madvincy.safe.booking.carPark.CarparkBookingSingle;

public class CarParkBookingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView bookingId;
    public TextView time;
    public CarParkBookingViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        bookingId = (TextView) itemView.findViewById(R.id.parkingId);
        time = (TextView) itemView.findViewById(R.id.time);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), CarparkBookingSingle.class);
        Bundle b = new Bundle();
        b.putString("parkingId", bookingId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);
    }
}
