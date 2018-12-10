package com.example.madvincy.safe.parking_place_account_activities.CarParkHistory.ParkingHistoryRecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.madvincy.safe.R;
import com.example.madvincy.safe.parking_place_account_activities.ActiveParking.ActiveParkingRecyclerView.CarParkBookingObject;

import java.util.List;

public class ParkHistoryAdapter extends RecyclerView.Adapter<ParkHistoryViewHolder> {

    private List<CarParkBookingObject> itemList;
    private Context context;

    public ParkHistoryAdapter(List<CarParkBookingObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public ParkHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item3_history, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ParkHistoryViewHolder rcv = new ParkHistoryViewHolder(layoutView);
        return rcv;
    }

//    @Override
//    public void onBindViewHolder(@NonNull CarParkBookingViewHolder holder, int position) {
//
//    }

    @Override
    public void onBindViewHolder(ParkHistoryViewHolder holder, final int position) {
        holder.parkingId.setText(itemList.get(position).getParkingId());
        if(itemList.get(position).getTime()!=null){
            holder.time.setText(itemList.get(position).getTime());
        }
    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

}
