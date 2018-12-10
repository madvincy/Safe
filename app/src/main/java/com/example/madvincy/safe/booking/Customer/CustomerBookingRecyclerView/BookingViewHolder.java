package com.example.madvincy.safe.booking.Customer.CustomerBookingRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.madvincy.safe.R;

import com.example.madvincy.safe.booking.Customer.BookingSingle;


public class BookingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView bookingId;
    public TextView time;
    public BookingViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        bookingId = (TextView) itemView.findViewById(R.id.parkingId);
        time = (TextView) itemView.findViewById(R.id.time);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), BookingSingle.class);
        Bundle b = new Bundle();
        b.putString("bookingId", bookingId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);
    }
}
