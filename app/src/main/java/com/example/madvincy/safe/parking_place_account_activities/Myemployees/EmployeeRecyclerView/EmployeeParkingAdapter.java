package com.example.madvincy.safe.parking_place_account_activities.Myemployees.EmployeeRecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.madvincy.safe.R;

import java.util.List;

public class EmployeeParkingAdapter extends RecyclerView.Adapter<EmployeeParkViewHolder> {

    private List<EmployeeParkObject> itemList;
    private Context context;

    public EmployeeParkingAdapter(List<EmployeeParkObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public EmployeeParkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_history, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        EmployeeParkViewHolder rcv = new EmployeeParkViewHolder(layoutView);
        return rcv;
    }



    @Override
    public void onBindViewHolder(EmployeeParkViewHolder holder, final int position) {
        holder.employeeId.setText(itemList.get(position).getEmployeeId());
        if(itemList.get(position).getTime()!=null){
            holder.time.setText(itemList.get(position).getTime());
        }
    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

}
