package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import org.joda.time.DateTime;
//implementing jodatime for simplified duration calculations
import org.joda.time.Duration;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        DateTime indatetime = new DateTime(ticket.getInTime());
        DateTime outdatetime = new DateTime(ticket.getOutTime());


        Duration parkingduration = new Duration(indatetime,outdatetime);

        int duration = (int)parkingduration.getStandardMinutes();


        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                //dividing the houly rate by 60 to get rate by minutes
                ticket.setPrice(duration * (Fare.CAR_RATE_PER_HOUR/60));
                break;
            }
            case BIKE: {
                //dividing the houly rate by 60 to get rate by minutes
                ticket.setPrice(duration * (Fare.BIKE_RATE_PER_HOUR/60));
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}