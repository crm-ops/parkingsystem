package com.parkit.parkingsystem.customExceptions;

public class ParkingExitWithoutValidTicketException extends RuntimeException {

    public ParkingExitWithoutValidTicketException(String errorMessage) {

        super(errorMessage);

    }



}
