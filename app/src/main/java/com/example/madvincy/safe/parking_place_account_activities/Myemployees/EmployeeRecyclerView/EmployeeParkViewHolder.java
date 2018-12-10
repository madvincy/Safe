package com.example.madvincy.safe.parking_place_account_activities.Myemployees.EmployeeRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.madvincy.safe.R;
import com.example.madvincy.safe.parking_place_account_activities.Myemployees.EmployeeSingleActivity;

public class EmployeeParkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView employeeId;
    public TextView time;
    public EmployeeParkViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        employeeId = (TextView) itemView.findViewById(R.id.parkingId);
        time = (TextView) itemView.findViewById(R.id.time);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), EmployeeSingleActivity.class);
        Bundle b = new Bundle();
        b.putString("EmployeeId", employeeId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);
    }
}
