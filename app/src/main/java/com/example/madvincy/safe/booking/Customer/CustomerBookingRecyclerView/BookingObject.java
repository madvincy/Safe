package com.example.madvincy.safe.booking.Customer.CustomerBookingRecyclerView;

public class BookingObject {
    private String bookingId;
    private String time;

    public BookingObject(String bookingId, String time){
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
