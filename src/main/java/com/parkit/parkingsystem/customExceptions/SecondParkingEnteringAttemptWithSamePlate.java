package com.parkit.parkingsystem.customExceptions;

public class SecondParkingEnteringAttemptWithSamePlate extends RuntimeException {

    public SecondParkingEnteringAttemptWithSamePlate(String errorMessage) {

        super(errorMessage);

    }

}
