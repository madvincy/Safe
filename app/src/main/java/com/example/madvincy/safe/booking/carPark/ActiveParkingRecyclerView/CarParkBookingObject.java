package com.example.madvincy.safe.booking.carPark.ActiveParkingRecyclerView;

public class CarParkBookingObject {
    private String bookingId;
    private String time;

    public CarParkBookingObject(String parkingId, String time){
        this.bookingId = bookingId;
        this.time = time;
    }

    public String getBookingId(){return bookingId;}
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getTime(){return time;}
    public void setTime(String time) {
        this.time = time;
    }
}
