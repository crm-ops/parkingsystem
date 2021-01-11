package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import org.joda.time.DateTime;
//implementing jodatime for simplified duration calculations
import org.joda.time.Duration;

import static com.parkit.parkingsystem.App.logger;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        DateTime indatetime = new DateTime(ticket.getInTime());
        DateTime outdatetime = new DateTime(ticket.getOutTime());


        Duration parkingduration = new Duration(indatetime,outdatetime);

        int duration = (int)parkingduration.getStandardMinutes();
        int netDuration = duration;

        //dividing the houly rate by 60 to get rate by minutes
        Double netCarFare = Fare.CAR_RATE_PER_HOUR/60;
        Double netBikeFare= Fare.BIKE_RATE_PER_HOUR/60;

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                //test if vehicule comes for first time or is returning vehicule
                if(ticket.getReturningVehicule().equals(false)) {
                    logger.info("This is a new CAR customer parking for the first time - applying 30 mins free");
                    logger.info("Minutes spent in parking="+ duration);

                    if (duration<30) {
                        logger.info("Gross duration is < 30 min ");
                        netDuration=0;

                    } else {
                        netDuration=duration-30;
                    }
                    logger.info("Duration billed in minutes"+ netDuration);

                }else if (ticket.getReturningVehicule().equals(true)) {
                    logger.info("This is a RETURNING CAR customer parking  - applying 5% discount");
                    logger.info("Gross fare is ="+ netDuration*netCarFare );
                    //applying discount
                    netCarFare =Fare.CAR_RATE_PER_HOUR/60*0.95;
                    logger.info("Net fare is ="+netDuration*netCarFare );

                }

                ticket.setPrice(netDuration * netCarFare);
                break;
            }
            case BIKE: {  //test if vehicule comes for first time or is returning vehicule
                if(ticket.getReturningVehicule().equals(false)) {
                    logger.info("This is a new BIKE customer parking for the first time - applying 30 mins free");
                    logger.info("Minutes spent in parking="+ duration);

                    if (duration<30) {
                        logger.info("Gross duration is < 30 min ");
                        netDuration=0;

                    } else {
                        netDuration=duration-30;
                    }
                    logger.info("Duration billed in minutes"+ netDuration);

                }else if (ticket.getReturningVehicule().equals(true)) {
                    logger.info("This is a RETURNING BIKE customer parking  - applying 5% discount");
                    logger.info("Gross fare is ="+ netDuration*netBikeFare );
                    //applying discount
                    netBikeFare =Fare.BIKE_RATE_PER_HOUR/60*0.95;
                    logger.info("Net fare is ="+netDuration*netBikeFare );

                }

                ticket.setPrice(netDuration * netBikeFare);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}