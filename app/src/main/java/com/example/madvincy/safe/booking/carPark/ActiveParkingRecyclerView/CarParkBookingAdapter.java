package com.example.madvincy.safe.booking.carPark.ActiveParkingRecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.madvincy.safe.R;

import java.util.List;

public class CarParkBookingAdapter extends RecyclerView.Adapter<CarParkBookingViewHolder> {

    private List<CarParkBookingObject> itemList;
    private Context context;

    public CarParkBookingAdapter(List<CarParkBookingObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public CarParkBookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.carparkbooking_history, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        CarParkBookingViewHolder rcv = new CarParkBookingViewHolder(layoutView);
        return rcv;
    }

//    @Override
//    public void onBindViewHolder(@NonNull CarParkBookingViewHolder holder, int position) {
//
//    }

    @Override
    public void onBindViewHolder(CarParkBookingViewHolder holder, final int position) {
        holder.bookingId.setText(itemList.get(position).getBookingId());
        if(itemList.get(position).getTime()!=null){
            holder.time.setText(itemList.get(position).getTime());
        }
    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

}
