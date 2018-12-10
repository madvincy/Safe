package com.example.madvincy.safe.parking_place_account_activities.Myemployees.EmployeeRecyclerView;

public class EmployeeParkObject {
    private String employeeId;
    private String time;

    public EmployeeParkObject(String employeeId, String time){
        this.employeeId = employeeId;
        this.time = time;
    }

    public String getEmployeeId(){return employeeId;}
    public void setEmployeeId(String parkingId) {
        this.employeeId = employeeId;
    }

    public String getTime(){return time;}
    public void setTime(String time) {
        this.time = time;
    }
}
